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
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
 * @since 02.10.11 00:08
 */
public class MultiUploadField extends JPanel {

  private JPanel root;
  private IconsList previewList;
  private UploadField fileInput;
  private JButton addFileButton;
  private JButton removeFileButton;
  private JPanel fileInputContainer;
  private JScrollPane previewScroller;

  private final static Icon addIcon = new ImageIcon(MultiUploadField.class.getResource("add.png"));
  private final static Icon deleteIcon = new ImageIcon(MultiUploadField.class.getResource("delete.png"));

  private List<UploadValue> imageList = new ArrayList<UploadValue>();

  private final ActionListener addAction = new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
      List<UploadValue> newValue = new ArrayList<UploadValue>(getImageList());
      newValue.add(fileInput.getImage());
      setImageList(newValue);
    }
  };

  private final ActionListener removeAction = new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
      UploadValue selected = (UploadValue) previewList.getSelectedValue();
      if (selected != null) {
        List<UploadValue> newValue = new ArrayList<UploadValue>(getImageList());
        newValue.remove(selected);
        setImageList(newValue);
        fileInput.setImage(null);
      }
    }
  };

  private final ActionListener replaceAction = new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
      UploadValue selected = (UploadValue) previewList.getSelectedValue();
      if (selected != null && fileInput.getImage() != null) {
        List<UploadValue> newValue = new ArrayList<UploadValue>(getImageList());
        int index = newValue.indexOf(selected);
        newValue.remove(selected);
        newValue.add(index, fileInput.getImage());
        setImageList(newValue);
      }
    }
  };

  public MultiUploadField() {
    super(new BorderLayout());

    fileInput = new UploadField(UploadField.NameFieldUpdater.ON_ENTER) {
      @Override
      protected Action newOpenFileAction() {
        return new FileOpenAction(this) {
          @Override
          protected JFileChooser newFileChooser(String path) {
            JFileChooser fc = super.newFileChooser(path);
            fc.setMultiSelectionEnabled(true);
            return fc;
          }
        };
      }
    };
    fileInputContainer.add(fileInput, BorderLayout.CENTER);

    previewList.setPreviewSize(fileInput.getPreviewSize());
    previewScroller.setBorder(BorderFactory.createEtchedBorder());
    previewList.addListSelectionListener(new ListSelectionListener() {
      @Override
      public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
          removeFileButton.setEnabled(previewList.getSelectedValue() != null);
          UploadValue value = (UploadValue) previewList.getSelectedValue();
          if (value != null) {
            fileInput.setImage(value);
          }
        }
      }
    });

    addFileButton.setText(null);
    addFileButton.setIcon(addIcon);
    addFileButton.setPreferredSize(new Dimension(25, 25));
    addFileButton.setEnabled(false);
    addFileButton.setVisible(false);
    
    removeFileButton.setText(null);
    removeFileButton.setIcon(deleteIcon);
    removeFileButton.setPreferredSize(new Dimension(25, 25));
    removeFileButton.setEnabled(false);
    removeFileButton.addActionListener(removeAction);

    fileInput.addPropertyChangeListener(new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("image")) {
//          addFileButton.setEnabled(fileInput.getImage() != null && previewList.getSelectedValue() != null);
          if (evt.getNewValue() != null) {
            UploadValue ov = (UploadValue) evt.getOldValue();
            UploadValue nv = (UploadValue) evt.getNewValue();
            if (ov == null || !Utils.nullSafeEquals(ov.getResource(), nv.getResource())) {
              addAction.actionPerformed(new ActionEvent(fileInput, ActionEvent.ACTION_PERFORMED, null, 0));
            } else {
              replaceAction.actionPerformed(new ActionEvent(fileInput, ActionEvent.ACTION_PERFORMED, null, 0));
            }
          }
        }
      }
    });

    add(root, BorderLayout.CENTER);
  }

  public static MultiUploadField forImageFiles() {
    MultiUploadField mf = new MultiUploadField();
    mf.addPreviewHandler(new ImagePreviewHandler());
    return mf;
  }

  public static MultiUploadField forAllFiles() {
    MultiUploadField mf = forImageFiles();
    mf.addPreviewHandler(Utils.allFileHandler());
    return mf;
  }

  public void setPreviewSize(Dimension dim) {
    fileInput.setPreviewSize(dim);
    previewList.setPreviewSize(dim);
  }

  @NotNull
  public Dimension getPreviewSize() {
    return fileInput.getPreviewSize();
  }

  public JComponent getGlassPane() {
    return fileInput.getGlassPane();
  }

  public void setGlassPane(JComponent glassPane) {
    fileInput.setGlassPane(glassPane);
  }

  public void setProposals(Iterable<URL> images) {
    fileInput.setProposals(images);
  }

  public Iterable<URL> getProposals() {
    return fileInput.getProposals();
  }

  public void addPreviewHandler(PreviewHandler handler) {
    fileInput.addPreviewHandler(handler);
  }

  public void removePreviewHandler(PreviewHandler handler) {
    fileInput.removePreviewHandler(handler);
  }

  public IconsList getPreviewList() {
    return previewList;
  }

  public UploadField getFileInput() {
    return fileInput;
  }

  public void setImageList(final List<UploadValue> images) {
    List<UploadValue> old = new ArrayList<UploadValue>(getImageList());
    for (UploadValue value : images) {
      if (!old.contains(value)) {
        this.imageList.add(value);
        previewList.addElement(value);
      }
    }
    Iterator<UploadValue> iter = this.imageList.iterator();
    while (iter.hasNext()) {
      UploadValue img = iter.next();
      if (!images.contains(img)) {
        iter.remove();
        previewList.removeElement(img);
      }
    }
    if (!old.equals(images)) {
      firePropertyChange("imageList", old, images);
    }
  }

  public List<UploadValue> getImageList() {
    return imageList;
  }
}

