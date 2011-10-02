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
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
 * @since 02.10.11 00:08
 */
public class MultiImageInput extends JPanel {

  private JPanel root;
  private IconsList imageView;
  private ImageInput imageInput;
  private JButton addImageButton;
  private JButton removeImageButton;
  private JPanel imageInputContainer;

  private final static Icon addIcon = new ImageIcon(MultiImageInput.class.getResource("add.png"));
  private final static Icon deleteIcon = new ImageIcon(MultiImageInput.class.getResource("delete.png"));

  private List<ImageValue> imageList = new ArrayList<ImageValue>();
  
  public MultiImageInput() {
    super(new BorderLayout());

    imageInput = new ImageInput();
    imageInputContainer.add(imageInput, BorderLayout.CENTER);

    imageView.setPreviewSize(imageInput.getPreviewSize());
    imageView.addListSelectionListener(new ListSelectionListener() {
      @Override
      public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
          removeImageButton.setEnabled(imageView.getSelectedValue() != null);
        }
      }
    });

    addImageButton.setText(null);
    addImageButton.setIcon(addIcon);
    addImageButton.setPreferredSize(new Dimension(25, 25));
    addImageButton.setEnabled(false);
    addImageButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        List<ImageValue> newValue = new ArrayList<ImageValue>(getImageList());
        newValue.add(imageInput.getImage());
        setImageList(newValue);
      }
    });

    removeImageButton.setText(null);
    removeImageButton.setIcon(deleteIcon);
    removeImageButton.setPreferredSize(new Dimension(25, 25));
    removeImageButton.setEnabled(false);
    removeImageButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        ImageValue selected = (ImageValue) imageView.getSelectedValue();
        if (selected != null) {
          List<ImageValue> newValue = new ArrayList<ImageValue>(getImageList());
          newValue.remove(selected);
          setImageList(newValue);
        }
      }
    });

    imageInput.addPropertyChangeListener(new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("image")) {
          addImageButton.setEnabled(imageInput.getImage() != null);
        }
      }
    });

    add(root, BorderLayout.CENTER);
  }

  public void setPreviewSize(Dimension dim) {
    imageInput.setPreviewSize(dim);
    imageView.setPreviewSize(dim);
  }

  @NotNull
  public Dimension getPreviewSize() {
    return imageInput.getPreviewSize();
  }

  public JComponent getGlassPane() {
    return imageInput.getGlassPane();
  }

  public void setGlassPane(JComponent glassPane) {
    imageInput.setGlassPane(glassPane);
  }

  public void setProposals(Iterable<URL> images) {
    imageInput.setProposals(images);
  }

  public Iterable<URL> getProposals() {
    return imageInput.getProposals();
  }

  public IconsList getImageView() {
    return imageView;
  }

  public ImageInput getImageInput() {
    return imageInput;
  }

  public void setImageList(final List<ImageValue> images) {
    List<ImageValue> old = new ArrayList<ImageValue>(getImageList());
    for (ImageValue value : images) {
      if (!old.contains(value)) {
        this.imageList.add(value);
        imageView.addElement(value);
      }
    }
    Iterator<ImageValue> iter = this.imageList.iterator();
    while (iter.hasNext()) {
      ImageValue img = iter.next();
      if (!images.contains(img)) {
        iter.remove();
        imageView.removeElement(img);
      }
    }
    if (!old.equals(images)) {
      firePropertyChange("imageList", old, images);
    }
  }

  public List<ImageValue> getImageList() {
    return imageList;
  }
}

