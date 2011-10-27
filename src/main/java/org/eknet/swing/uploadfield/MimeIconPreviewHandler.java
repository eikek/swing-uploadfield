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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;

import eu.medsea.mimeutil.MimeType;

/**
 * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
 * @since 27.10.11 10:16
 */
public class MimeIconPreviewHandler implements PreviewHandler {

  private final FileFilter allFilter = new FileFilter() {
    @Override
    public boolean accept(File f) {
      return true;
    }

    @Override
    public String getDescription() {
      return UIManager.getString("FileChooser.acceptAllFileFilterText");
    }
  };
  private final MimeIconMap iconMap = new MimeIconMap();

  @Override
  public BufferedImage createImage(URL url) throws IOException {
    MimeType mime = MimeTypes.getMimeType(url);
    return iconMap.getIconImage(mime);
  }

  @Override
  public FileFilter getFileFilter() {
    return allFilter;
  }
}
