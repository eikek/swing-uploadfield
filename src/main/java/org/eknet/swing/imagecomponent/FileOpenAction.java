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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
 * @since 01.10.11 16:46
 */
class FileOpenAction extends AbstractAction {

  private final Preferences prefs = Preferences.userNodeForPackage(FileOpenAction.class);

  private final ImageInput component;

  public FileOpenAction(ImageInput component) {
    this.component = component;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    String path = prefs.get("FileOpenAction.lastLocation", null);
    JFileChooser fc = new JFileChooser(path);
    fc.setFileFilter(new FileNameExtensionFilter("Images", "png", "jpg", "bmp", "tif"));
    if (fc.showOpenDialog(getSourceComponent(e)) == JFileChooser.APPROVE_OPTION) {
      File f = fc.getSelectedFile();
      prefs.put("FileOpenAction.lastLocation", f.getAbsolutePath());
      ImageValue old = component.getImage();
      ImageValue current = null;
      try {
        current = old != null? old.clone() : new ImageValue();
      } catch (CloneNotSupportedException e1) {
        throw new Error("Unreachable code!", e1);
      }
      try {
        current.setImageResource(f.toURI().toURL());
        component.setImage(current);
      } catch (MalformedURLException e1) {
        throw new RuntimeException(e1);
      }
    }
  }

  private Component getSourceComponent(ActionEvent e) {
    Object o = e.getSource();
    if (o instanceof Component) {
      return (Component) o;
    }
    return null;
  }
}
