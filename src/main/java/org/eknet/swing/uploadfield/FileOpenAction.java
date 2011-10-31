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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFileChooser;

/**
 * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
 * @since 01.10.11 16:46
 */
class FileOpenAction extends AbstractAction {

  private final Preferences prefs = Preferences.userNodeForPackage(FileOpenAction.class);

  private final UploadField component;

  private final UrlHandlerList handlers;

  public FileOpenAction(UploadField component) {
    this.component = component;
    this.handlers = component.getHandlerList();
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    String path = prefs.get("FileOpenAction.lastLocation", null);
    JFileChooser fc = newFileChooser(path);
    if (fc.showOpenDialog(getSourceComponent(e)) == JFileChooser.APPROVE_OPTION) {
      if (fc.isMultiSelectionEnabled()) {
        for (File f : fc.getSelectedFiles()) {
          pushFileToComponent(f);
        }
      } else {
        pushFileToComponent(fc.getSelectedFile());
      }
    }
  }

  private void pushFileToComponent(File f) {
    prefs.put("FileOpenAction.lastLocation", f.getAbsolutePath());
    UploadValue old = component.getUploadValue();
    UploadValue current = null;
    try {
      current = old != null ? old.clone() : new UploadValue();
    } catch (CloneNotSupportedException e1) {
      throw new Error("Unreachable code!", e1);
    }
    try {
      current.setResource(f.toURI().toURL());
      component.setUploadValue(current);
    } catch (MalformedURLException e1) {
      throw new RuntimeException(e1);
    }
  }

  protected JFileChooser newFileChooser(String path) {
    JFileChooser fc = new JFileChooser(path);
    fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
    fc.setMultiSelectionEnabled(false);
    fc.setAccessory(new PreviewAccessoir(fc));
    component.customizeFileChooser(fc);
    return fc;
  }

  private Component getSourceComponent(ActionEvent e) {
    Object o = e.getSource();
    if (o instanceof Component) {
      return (Component) o;
    }
    return null;
  }


  private class PreviewAccessoir extends JComponent implements PropertyChangeListener {

    private ImageIcon icon;
    private File file;

    public PreviewAccessoir(JFileChooser fc) {
      setPreferredSize(new Dimension(100, 50));
      fc.addPropertyChangeListener(this);
    }

    public void loadImage() {
      if (file == null) {
        icon = null;
        return;
      }

      try {
        BufferedImage image = handlers.createImage(file.toURI().toURL(), new Dimension(100, 100));
        icon = new ImageIcon(image);
      } catch (IOException e) {
        icon = null;
      }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
      boolean update = false;
      String prop = evt.getPropertyName();

      //If the directory changed, don't show an image.
      if (JFileChooser.DIRECTORY_CHANGED_PROPERTY.equals(prop)) {
        file = null;
        update = true;

        //If a file became selected, find out which one.
      } else if (JFileChooser.SELECTED_FILE_CHANGED_PROPERTY.equals(prop)) {
        file = (File) evt.getNewValue();
        update = true;
      }

      //Update the preview accordingly.
      if (update) {
        icon = null;
        if (isShowing()) {
          loadImage();
          repaint();
        }
      }
    }

    @Override
    protected void paintComponent(Graphics g) {
      if (icon == null) {
        loadImage();
      }
      if (icon != null) {
        int x = getWidth() / 2 - icon.getIconWidth() / 2;
        int y = getHeight() / 2 - icon.getIconHeight() / 2;

        if (y < 0) {
          y = 0;
        }

        if (x < 5) {
          x = 5;
        }
        icon.paintIcon(this, g, x, y);
      }
    }
  }

}
