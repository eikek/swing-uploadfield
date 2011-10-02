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
import java.awt.event.ActionEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

/**
 * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
 * @since 01.10.11 16:46
 */
class FileOpenAction extends AbstractAction {

  private final Preferences prefs = Preferences.userNodeForPackage(FileOpenAction.class);

  private final UploadField component;

  private final Iterable<PreviewHandler> previewHandlers;

  public FileOpenAction(UploadField component) {
    this.component = component;
    this.previewHandlers = component.getPreviewHandlers();
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
    UploadValue old = component.getImage();
    UploadValue current = null;
    try {
      current = old != null? old.clone() : new UploadValue();
    } catch (CloneNotSupportedException e1) {
      throw new Error("Unreachable code!", e1);
    }
    try {
      current.setResource(f.toURI().toURL());
      component.setImage(current);
    } catch (MalformedURLException e1) {
      throw new RuntimeException(e1);
    }
  }

  protected JFileChooser newFileChooser(String path) {
    JFileChooser fc = new JFileChooser(path);
    fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
    fc.setFileFilter(createFilter());
    fc.setMultiSelectionEnabled(false);
    return fc;
  }

  private FileFilter createFilter() {
    CompoundFileFilter filter = new CompoundFileFilter();
    for (PreviewHandler handler : previewHandlers) {
      filter.addFilter(handler.getFileFilter());
    }
    return filter;
  }
  
  private Component getSourceComponent(ActionEvent e) {
    Object o = e.getSource();
    if (o instanceof Component) {
      return (Component) o;
    }
    return null;
  }
}
