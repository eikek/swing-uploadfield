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

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.alainn.swingbox.test.TestPanel;

/**
 * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
 * @since 01.10.11 19:58
 */
public class TestForm extends JPanel {
  private JPanel root;
  private JTextField textField1;
  private JPanel imageInputPanel;
  
  private ImageInput imageInput;

  public TestForm() {
    super(new BorderLayout());
    imageInput = new ImageInput();
    imageInput.setPreviewSize(80, 80);
    imageInput.setProposals(IconViewerTest.getIconURLs());
    imageInputPanel.add(imageInput, BorderLayout.CENTER);
    root.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
    add(root, BorderLayout.CENTER);

    setPreferredSize(new Dimension(800, 600));
  }


  public static void main(String[] args) {
    final TestForm form = new TestForm();
    form.imageInput.addPropertyChangeListener(new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("image")) {
          System.out.println("New Image: " + evt.getNewValue());
        }
      }
    });
    TestPanel.start(form, "Form Preview");
  }
}
