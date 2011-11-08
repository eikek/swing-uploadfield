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

package org.eknet.swing.uploadfield;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DropTarget;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.concurrent.ExecutionException;

import javax.swing.*;
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
 *
 * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
 * @since 01.10.11 15:24
 */
public class UploadField extends JPanel {

  private static final Logger log = LoggerFactory.getLogger(UploadField.class);

  public static final String VALUE_PROPERTY_NAME = "uploadValue";

  private JPanel root;
  private JButton previewButton;
  private JTextField resourceField;
  private JTextField nameField;
  private JPanel previewPanel;
  private JButton proposalsButton;
  private JButton resetButton;
  private JLabel messageLabel;

  private static final ImageIcon folderIcon = new ImageIcon(UploadField.class.getResource("folder.png"));
  private static final ImageIcon resetIcon = new ImageIcon(UploadField.class.getResource("arrow_undo.png"));
  private static final ImageIcon imagesIcon = new ImageIcon(UploadField.class.getResource("images.png"));
  private static final ImageIcon successIcon = new ImageIcon(UploadField.class.getResource("tick.png"));
  private static final ImageIcon errorIcon = new ImageIcon(UploadField.class.getResource("error.png"));

  private Set<URL> proposals;

  private Dimension previewSize = new Dimension(50, 50);
  private final Color normalMessageColor;
  private Color errorMessageColor = Color.red;
  private IconsList iconViewer = new IconsList();
  private JDialog dialog;
  private JComponent glassPane = new SimpleGlassPane();

  private final UrlHandlerList handlers = new UrlHandlerList(previewSize);

  private final MouseAdapter emptyMouseListener = new MouseAdapter() {};
  private final KeyAdapter emptyKeyListener = new KeyAdapter() {};

  private UploadValue uploadValue;

  private final DocumentListener nameFieldOnTypeUpdater = new DocumentListener() {
    @Override
    public void insertUpdate(DocumentEvent e) {
      updateNameField();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
      updateNameField();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
      updateNameField();
    }
  };
  private final KeyAdapter nameFieldEnterUpdater = new KeyAdapter() {
    @Override
    public void keyTyped(KeyEvent e) {
      if (e.getKeyChar() == KeyEvent.VK_ENTER) {
        updateNameField();
      }
    }
  };

  public enum NameFieldUpdater {
    ON_TYPE, ON_ENTER
  }

  private final DropHandler dropHandler = new DropHandler() {
    @Override
    protected boolean setData(List<UploadValue> data) {
      if (data.isEmpty()) {
        return false;
      }
      setUploadValue(data.get(0));
      return true;
    }
  };

  private final ListSelectionListener iconPreviewSelectionListener = new ListSelectionListener() {
    @Override
    public void valueChanged(ListSelectionEvent e) {
      if (!e.getValueIsAdjusting()) {
        URL url = (URL) iconViewer.getSelectedValue();
        if (url != null) {
          DefaultUploadValue value = createNewOrCopy();
          value.setResource(url);
          setUploadValue(value);
        }
        if (dialog != null) {
          dialog.setVisible(false);
        }
      }
    }
  };

  public UploadField() {
    this(NameFieldUpdater.ON_TYPE);
  }

  public UploadField(NameFieldUpdater updateStrategy) {
    setLayout(new OverlayLayout(this));
    this.normalMessageColor = messageLabel.getForeground();

    messageLabel.setFont(messageLabel.getFont().deriveFont(10f));

    iconViewer.addListSelectionListener(iconPreviewSelectionListener);

    previewButton.setIcon(folderIcon);
    previewButton.setText("...");
    previewButton.addActionListener(newOpenFileAction());
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
        setUploadValue(null);
        onReset();
      }
    });

    resourceField.setTransferHandler(null);
    resourceField.addKeyListener(new KeyAdapter() {
      @Override
      public void keyTyped(KeyEvent e) {
        if (e.getKeyChar() == KeyEvent.VK_ENTER) {
          String url = resourceField.getText();
          if (url == null || "".equals(url.trim())) {
            setUploadValue(null);
          } else {
            DefaultUploadValue value = createNewOrCopy();
            try {
              if (!url.contains(":/")) {
                url = new File(url).toURI().toURL().toString();
              }
              value.setResource(new URL(url));
              setUploadValue(value);
            } catch (MalformedURLException e1) {
              setMessage(getLoadingErrorMessage(value), true);
            }
          }
        }
      }
    });
    nameField.setTransferHandler(null);
    if (updateStrategy == NameFieldUpdater.ON_TYPE) {
      nameField.getDocument().addDocumentListener(nameFieldOnTypeUpdater);
    } else {
      nameField.addKeyListener(nameFieldEnterUpdater);
    }

    setUseFallbackHandler(true);
    
    add(root, -1);
    setGlassPane(new SimpleGlassPane());
  }

  public static UploadField forImageFiles() {
    final ImageHandler handler = new ImageHandler();
    UploadField fi = new UploadField() {
      @Override
      protected void customizeFileChooser(JFileChooser fc) {
        fc.setAcceptAllFileFilterUsed(false);
        fc.addChoosableFileFilter(handler.getFileFilter());
      }
    };
    fi.addUrlHandler(handler);
    fi.setUseFallbackHandler(false);
    return fi;
  }

  public static UploadField forAllFiles() {
    final ImageHandler handler = new ImageHandler();
    UploadField fi = new UploadField() {
      @Override
      protected void customizeFileChooser(JFileChooser fc) {
        fc.addChoosableFileFilter(handler.getFileFilter());
      }
    };
    fi.addUrlHandler(handler);
    fi.setUseFallbackHandler(true);
    return fi;
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
      handlers.setIconSize(previewSize);
      firePropertyChange("previewSize", old, dim);
    }
  }

  @NotNull
  public Dimension getPreviewSize() {
    return this.previewSize;
  }

  public void addUrlHandler(UrlHandler handler) {
    if (handler != null) {
      handlers.addHandler(handler);
    }
  }

  public void removeUrlHandler(UrlHandler handler) {
    if (handler != null) {
      handlers.removeHander(handler);
    }
  }

  public Iterable<UrlHandler> getPreviewHandlers() {
    return handlers.copy();
  }

  /* package private*/ UrlHandlerList getHandlerList() {
    return handlers;
  }

  protected void onReset() {
    
  }

  public void setUseFallbackHandler(boolean flag) {
    if (flag) {
      if (Utils.isMimeUtilAvailable()) {
        handlers.setFallback(new MimeIconPreviewHandler());
      } else {
        handlers.setFallback(new PlaceholderIconUrlHandler());
      }
    } else {
      handlers.setFallback(null);
    }
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

  public void setIconViewer(IconsList iconList) {
    if (this.iconViewer != null) {
      this.iconViewer.removeListSelectionListener(iconPreviewSelectionListener);
    }
    this.iconViewer = iconList;
    if (this.iconViewer != null) {
      this.iconViewer.addListSelectionListener(iconPreviewSelectionListener);
    }
    proposalsButton.setVisible(this.iconViewer != null);
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
   * @param uploadValue
   */
  public void setUploadValue(@Nullable UploadValue uploadValue) {
    UploadValue old = getUploadValue();

    //if new image url is given, component must be updated by LoadingImageTask
    boolean loading = false;
    if (uploadValue != null && uploadValue.getResource() != null) {
      if (old == null || (!uploadValue.getResource().equals(old.getResource()))) {
        new UrlLoadingTask(uploadValue).execute();
        loading = true;
      }
    }
    if (!loading) {
      if (!Utils.nullSafeEquals(uploadValue, old)) {
        this.uploadValue = uploadValue;
        firePropertyChange(VALUE_PROPERTY_NAME, old, uploadValue);
      }
      updateComponent(this.uploadValue);
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
  public UploadValue getUploadValue() {
    return uploadValue;
  }

  public void setDropEnabled(boolean flag) {
    if (flag) {
      setTransferHandler(dropHandler);
    } else {
      setTransferHandler(null);
    }
  }

  /**
   * Returns a action that is executed when the preview button
   * is clicked. The default action presents a file-open dialog
   * to the user.
   * 
   * @return
   */
  protected Action newOpenFileAction() {
    return new FileOpenAction(this);
  }

  /**
   * Called when no preview image is available. It returns a short
   * error message indicating this to the user.
   * @param value
   * @return
   */
  protected String getLoadingErrorMessage(UploadValue value) {
    String name = "";
    if (value == null || value.getResource() == null) {
      name = "<null>";
    } else {
      name = Utils.lastUrlPart(value.getResource());
    }
    return "Unable to load preview for: " + name;
  }

  /**
   * Used to set the tooltip on the preview image button. This button
   * triggers a file-open dialog to select a file from the local disk.
   * 
   * @return
   */
  protected String getOpenButtonTooltip() {
    return "Click to select a image from disk";
  }

  protected void customizeFileChooser(JFileChooser fc) {
  }


  private DefaultUploadValue createNewOrCopy() {
    if (this.uploadValue == null) {
      return new DefaultUploadValue();
    }
    return new DefaultUploadValue(this.uploadValue);
  }

  /**
   * Must be called to reflect changes to the nameField only to the model.
   */
  private void updateNameField() {
    String text = nameField.getText();
    String url = resourceField.getText();
    if (url == null && (text == null || "".equals(text.trim()))) {
      setUploadValue(null);
    } else {
      UploadValue value = createNewOrCopy();
      value.setName(text);
      UploadValue old = getUploadValue();
      UploadField.this.uploadValue = value;
      UploadField.this.firePropertyChange(VALUE_PROPERTY_NAME, old, value);
    }
  }

  private void updateComponent(UploadValue value) {
    if (DefaultUploadValue.isNullOrEmpty(value)) {
      resourceField.setText(null);
      nameField.setText(null);
      previewButton.setIcon(folderIcon);
      previewButton.setText("...");
      clearMessage();
    } else {
      URL url = value.getResource();
      resourceField.setText(url != null ? url.toString() : null);
      nameField.setText(value.getName());
      if (value.getIcon() != null) {
        previewButton.setIcon(value.getIcon());
        previewButton.setText(null);
      }
      setMessage(value.getDescription(), false);
    }
  }

  /**
   * Loads the selected url and retrieves information about the contents.
   */
  private class UrlLoadingTask extends SwingWorker<UploadValue, Void> {

    private final UploadValue value;

    public UrlLoadingTask(UploadValue value) {
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
    protected UploadValue doInBackground() throws Exception {
      if (DefaultUploadValue.isNullOrEmpty(value)) {
        return null;
      }
      URL url = value.getResource();
      if (!url.getProtocol().equals("file")) {
        File tempFile = File.createTempFile("fileInput", ".ext");
        tempFile.deleteOnExit();
        Utils.copy(url, tempFile);
        value.setFile(tempFile);
      } else {
        value.setFile(new File(URLDecoder.decode(url.getPath(), "UTF-8")));
      }
      BufferedImage image = handlers.createImage(url);
      if (image != null) {
        value.setImage(image);
      }
      if (image != null) {
        value.setIcon(new ImageIcon(Scales.scaleIfNecessary(image, previewSize.width, previewSize.height)));
      } else {
        value.setMissingIcon(previewSize.width, previewSize.height);
      }
      if (value.getName() == null) {
        value.setName(handlers.getName(url));
      }
      value.setDescription(handlers.getDescription(value));
      return value;
    }

    @Override
    protected void done() {
      try {
        UploadValue old = getUploadValue();
        UploadValue newvValue = get();
        UploadField.this.uploadValue = newvValue;
        updateComponent(newvValue);
        if (newvValue.getImage() == null) {
          setMessage(getLoadingErrorMessage(value), true);
        }
        UploadField.this.firePropertyChange(VALUE_PROPERTY_NAME, old, newvValue);
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
