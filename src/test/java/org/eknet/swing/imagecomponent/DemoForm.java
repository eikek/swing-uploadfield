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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JEditorPane;
import javax.swing.JPanel;

import org.alainn.swingbox.test.TestPanel;

/**
 * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
 * @since 01.10.11 19:58
 */
public class DemoForm extends JPanel {

  private JPanel root;
  private JPanel imageInputPanel;
  private MultiImageInput multiImageInput;
  private JEditorPane editorPane1;

  private ImageInput imageInput;

  public DemoForm() {
    super(new BorderLayout());
    imageInput = new ImageInput();
    imageInput.setPreviewSize(80, 80);
    imageInput.setProposals(IconViewerTest.getIconURLs());
    imageInputPanel.add(imageInput, BorderLayout.CENTER);

    multiImageInput.setProposals(IconViewerTest.getIconURLs());
    multiImageInput.setPreviewSize(new Dimension(65, 65));
    root.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
    add(root, BorderLayout.CENTER);

    setPreferredSize(new Dimension(800, 600));
  }


  public static void main(String[] args) {
    final DemoForm form = new DemoForm();
    form.imageInput.addPropertyChangeListener(new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("image")) {
          System.out.println("New Image: " + evt.getNewValue());
        }
      }
    });
    form.multiImageInput.addPropertyChangeListener(new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("imageList")) {
          List list = (List) evt.getNewValue();
          System.out.println("new images [ " + list.size() + " ]:" + list);
        }
      }
    });
    TestPanel.start(form, "Form Preview");
  }
}
