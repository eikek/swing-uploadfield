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

import org.alainn.swingbox.test.TestPanel;

/**
 * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
 * @since 01.10.11 15:25
 */
public class ImageInputTest {

  public static void main(String[] args) {
    ImageInput img = new ImageInput();
    img.setPreviewSize(90, 90);
    img.setProposals(IconViewerTest.getIconURLs());
    TestPanel.start(img, "Image Input");
  }
}
