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

import java.awt.Dimension;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.alainn.swingbox.test.TestPanel;

/**
 * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
 * @since 01.10.11 20:52
 */
public class IconViewerTest {


  public static void main(String[] args) {
    List<URL> icons = getIconURLs();
    IconsList v = new IconsList();
    v.setIconElements(icons);
    v.setPreferredSize(new Dimension(400, 250));
    TestPanel.start(v, "Icon Viewer");
    
  }

  public static List<URL> getIconURLs() {
    List<URL> icons = new ArrayList<URL>();
    icons.add(UploadField.class.getResource("arrow_undo.png"));
    icons.add(UploadField.class.getResource("error.png"));
    icons.add(UploadField.class.getResource("folder.png"));
    icons.add(UploadField.class.getResource("images.png"));
    icons.add(UploadField.class.getResource("tick.png"));
    return icons;
  }
}
