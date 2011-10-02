/*
 * Copyright 2011 Eike Kettner
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.eknet.swing.imagecomponent;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.OverlayLayout;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An input component for images.
 * <p/>
 * It consists of the following:
 * <ul>
 * <li>two text fields, one for the image url and one for an optional title for name</li>
 * <li>a preview button, that shows the image. onclick a open-file-dialog is presented to choose a file
 * from the file system</li>
 * <li>optional, a set of predefined images can be specified. If so, a small button opens a
 * component that allows the user to choose a image.</li>
 * <li>a button to reset the form</li>
 * </ul>
 * It supports scaling the image to a specified height/width.
 *
 * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
 * @since 01.10.11 15:24
 */
public class ImageInput extends JPanel {

  private static final Logger log = LoggerFactory.getLogger(ImageInput.class);

  private JPanel root;
  private JButton previewButton;
  private JTextField resourceField;
  private JTextField nameField;
  private JPanel previewPanel;
  private JButton proposalsButton;
  private JButton resetButton;
  private JLabel messageLabel;

  private static final ImageIcon folderIcon = new ImageIcon(ImageInput.class.getResource("folder.png"));
  private static final ImageIcon resetIcon = new ImageIcon(ImageInput.class.getResource("arrow_undo.png"));
  private static final ImageIcon imagesIcon = new ImageIcon(ImageInput.class.getResource("images.png"));
  private static final ImageIcon successIcon = new ImageIcon(ImageInput.class.getResource("tick.png"));
  private static final ImageIcon errorIcon = new ImageIcon(ImageInput.class.getResource("error.png"));


  private Set<URL> proposals;
  private Dimension previewSize = new Dimension(50, 50);
  private final Color normalMessageColor;
  private Color errorMessageColor = Color.red;
  private IconsList iconViewer = new IconsList();
  private JDialog dialog;
  private boolean documentListenerMuted = false;

  private JComponent glassPane = new SimpleGlassPane();

  private final MouseAdapter emptyMouseListener = new MouseAdapter() {};
  private final KeyAdapter emptyKeyListener = new KeyAdapter() {};

  private ImageValue image;

  public ImageInput() {
    setLayout(new OverlayLayout(this));
    this.normalMessageColor = messageLabel.getForeground();

    messageLabel.setFont(messageLabel.getFont().deriveFont(10f));

    iconViewer.addListSelectionListener(new ListSelectionListener() {
      @Override
      public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
          URL url = (URL) iconViewer.getSelectedValue();
          if (url != null) {
            ImageValue value = createNewOrCopy();
            value.setImageResource(url);
            setImage(value);
          }
          if (dialog != null) {
            dialog.setVisible(false);
          }
        }
      }
    });

    previewButton.setIcon(folderIcon);
    previewButton.setText("...");
    previewButton.addActionListener(newOpenImageAction());
    previewButton.setBackground(Color.white);
    previewButton.setToolTipText(getOpenButtonTooltip());

    proposalsButton.setIcon(imagesIcon);
    proposalsButton.setText(null);
    proposalsButton.setVisible(false);
    proposalsButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (dialog == null) {
          Window parent = Utils.findWindow(proposalsButton);
          dialog = new JDialog(parent, "Proposals", Dialog.ModalityType.APPLICATION_MODAL) {
            @Override
            protected JRootPane createRootPane() {
              JRootPane rootPane = super.createRootPane();
              KeyStroke escStroke = KeyStroke.getKeyStroke("ESCAPE");
              InputMap map = rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);

              map.put(escStroke, "ESCAPE");
              rootPane.getActionMap().put("ESCAPE", new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                  dialog.setVisible(false);
                }
              });
              return rootPane;
            }
          };
          dialog.getContentPane().setLayout(new BorderLayout());
          dialog.getContentPane().add(new JScrollPane(iconViewer), BorderLayout.CENTER);
        }
        Rectangle bounds = new Rectangle();
        Point mouseLoc = MouseInfo.getPointerInfo().getLocation();
        bounds.x = mouseLoc.x;
        bounds.y = mouseLoc.y;
        bounds.width = 350;
        bounds.height = 250;
        dialog.setBounds(bounds);
        dialog.setVisible(!dialog.isVisible());
      }
    });

    resetButton.setIcon(resetIcon);
    resetButton.setText(null);
    resetButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        setImage(null);
      }
    });

    resourceField.addKeyListener(new KeyAdapter() {
      @Override
      public void keyTyped(KeyEvent e) {
        if (e.getKeyChar() == KeyEvent.VK_ENTER) {
          String url = resourceField.getText();
          if (url == null || "".equals(url.trim())) {
            setImage(null);
          } else {
            ImageValue value = createNewOrCopy();
            try {
              if (!url.contains(":/")) {
                url = new File(url).toURI().toURL().toString();
              }
              value.setImageResource(new URL(url));
              setImage(value);
            } catch (MalformedURLException e1) {
              setMessage(getLoadingErrorMessage(value), true);
            }
          }
        }
      }
    });
    nameField.getDocument().addDocumentListener(new DocumentListener() {
      @Override
      public void insertUpdate(DocumentEvent e) {
        update(e);
      }

      @Override
      public void removeUpdate(DocumentEvent e) {
        update(e);
      }

      @Override
      public void changedUpdate(DocumentEvent e) {
        update(e);
      }

      private void update(DocumentEvent e) {
        if (documentListenerMuted) {
          return;
        }
        String text = nameField.getText();
        String url = resourceField.getText();
        if (url == null && (text == null || "".equals(text.trim()))) {
          setImage(null);
        } else {
          ImageValue value = createNewOrCopy();
          value.setImageName(text);
          ImageValue old = getImage();
          ImageInput.this.image = value;
          ImageInput.this.firePropertyChange("image", old, value);
        }
      }
    });
    add(root, -1);
    setGlassPane(new SimpleGlassPane());
  }


  public void setPreviewSize(int width, int height) {
    this.setPreviewSize(new Dimension(width, height));
  }

  /**
   * Sets the previewSize to the specified value. If the value is {@code null}
   * nothing changes.
   * @param dim
   */
  public void setPreviewSize(Dimension dim) {
    if (dim != null) {
      Dimension old = getPreviewSize();
      this.previewSize = dim;
      previewPanel.setPreferredSize(previewSize);
      previewPanel.setMaximumSize(previewSize);
      previewPanel.setMinimumSize(previewSize);
      firePropertyChange("previewSize", old, dim);
    }
  }

  @NotNull
  public Dimension getPreviewSize() {
    return this.previewSize;
  }

  public JComponent getGlassPane() {
    return glassPane;
  }

  public void setGlassPane(JComponent glassPane) {
    boolean visible = false;
    if (this.glassPane != null) {
      visible = this.glassPane.isVisible();
      remove(this.glassPane);
    }
    this.glassPane = glassPane;
    if (this.glassPane != null) {
      add(this.glassPane, 0);
      this.glassPane.addMouseListener(emptyMouseListener);
      this.glassPane.addKeyListener(emptyKeyListener);
      this.glassPane.setVisible(visible);
    }
  }

  public void setProposals(Iterable<URL> images) {
    if (images != null && images.iterator().hasNext()) {
      this.proposals = new HashSet<URL>();
      for (URL iv : images) {
        this.proposals.add(iv);
      }
      this.iconViewer.setIconElements(proposals);
    } else {
      this.proposals = null;
      this.iconViewer.setIconElements(null);
    }
    proposalsButton.setVisible(this.proposals != null);
  }

  public Iterable<URL> getProposals() {
    return Collections.unmodifiableSet(this.proposals);
  }

  /**
   * Sets the specified image on this component. If the image cannot be read,
   * it updates the component with an error message. Never throws an exception.
   *
   * @param image
   */
  public void setImage(@Nullable ImageValue image) {
    ImageValue old = getImage();

    //if new image url is given, component must be updated by LoadingImageTask
    boolean loading = false;
    if (image != null && image.getImageResource() != null) {
      if (old == null || (!image.getImageResource().equals(old.getImageResource()))) {
        new ImageLoadingTask(image).execute();
        loading = true;
      }
    }
    if (!loading) {
      if (!Utils.nullSafeEquals(image, old)) {
        this.image = image;
        firePropertyChange("image", old, image);
      }
      updateComponent(this.image);
    }
  }

  public void setMessage(String message, boolean isError) {
    if (isError) {
      messageLabel.setForeground(errorMessageColor);
      messageLabel.setIcon(errorIcon);
    } else {
      messageLabel.setForeground(normalMessageColor);
      messageLabel.setIcon(successIcon);
    }
    this.messageLabel.setText(message);
  }

  public void clearMessage() {
    this.messageLabel.setText(null);
    this.messageLabel.setIcon(null);
  }

  @Nullable
  public ImageValue getImage() {
    return image;
  }

  protected Action newOpenImageAction() {
    return new FileOpenAction(this);
  }

  protected String getImageDescription(@NotNull ImageValue value) {
    StringBuilder buf = new StringBuilder();
    if (value.getImage() != null) {
      buf.append(value.getImage().getWidth())
              .append("x")
              .append(value.getImage().getHeight())
              .append("px");
    }
    if (value.getImageFile() != null) {
      if (value.getImage() != null) {
        buf.append("; ");
      }
      buf.append(Utils.toSizeString(value.getImageFile().length()));
    }
    return buf.toString();
  }

  protected String getLoadingErrorMessage(ImageValue value) {
    String name = "";
    if (value == null || value.getImageResource() == null) {
      name = "<null>";
    } else {
      name = Utils.lastUrlPart(value.getImageResource());
    }
    return "Unable to load image: " + name;
  }

  protected String getOpenButtonTooltip() {
    return "Click to select a image from disk";
  }


  private ImageValue createNewOrCopy() {
    if (this.image == null) {
      return new ImageValue();
    }
    return new ImageValue(this.image);
  }

  private void updateComponent(ImageValue value) {
    documentListenerMuted = true;
    if (ImageValue.isNullOrEmpty(value)) {
      resourceField.setText(null);
      nameField.setText(null);
      previewButton.setIcon(folderIcon);
      previewButton.setText("...");
      clearMessage();
    } else {
      URL url = value.getImageResource();
      resourceField.setText(url != null ? url.toString() : null);
      nameField.setText(value.getImageName());
      if (value.getIcon() != null) {
        previewButton.setIcon(value.getIcon());
        previewButton.setText(null);
      }
      setMessage(getImageDescription(value), false);
    }
    documentListenerMuted = false;
  }

  public boolean isOptimizedDrawingEnabled() {
    return false;
  }

  private class ImageLoadingTask extends SwingWorker<ImageValue, Void> {

    private final ImageValue value;

    public ImageLoadingTask(ImageValue value) {
      this.value = value;
      SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {
          if (getGlassPane() != null) {
            getGlassPane().setVisible(true);
          }
        }
      });
    }


    @Override
    protected ImageValue doInBackground() throws Exception {
      if (ImageValue.isNullOrEmpty(value)) {
        return null;
      }
      BufferedImage image = value.getOrReadImage();
      if (image == null) {
        throw new IllegalArgumentException("Unable to set image from: " + value.getImageResource());
      }
      if (!value.getImageResource().getProtocol().equals("file")) {
        File tempFile = File.createTempFile("imageComponent", ".png");
        tempFile.deleteOnExit();
        ImageIO.write(image, "PNG", tempFile);
        value.setImageFile(tempFile);
      } else {
        value.setImageFile(new File(value.getImageResource().getPath()));
      }
      value.setIcon(new ImageIcon(value.getScaledImage(previewSize)));
      return value;
    }

    @Override
    protected void done() {
      try {
        ImageValue old = getImage();
        ImageValue newvValue = get();
        ImageInput.this.image = newvValue;
        updateComponent(newvValue);
        ImageInput.this.firePropertyChange("image", old, newvValue);
      } catch (InterruptedException e) {
        setMessage(getLoadingErrorMessage(value), true);
        log.error("Error loading image!", e);
      } catch (ExecutionException e) {
        setMessage(getLoadingErrorMessage(value), true);
        log.error("Error loading image!", e);
      } finally {
        if (getGlassPane() != null) {
          getGlassPane().setVisible(false);
        }
      }
    }

  }

}
