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
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.jetbrains.annotations.NotNull;

/**
 * A very simple preview handler which uses {@link ImageIO} to read an image
 * from the specified url.
 * 
 * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
 * @since 02.10.11 03:13
 */
public class ImagePreviewHandler implements PreviewHandler {

  private String[] extensions = new String[]{"png", "jpg", "jpeg", "tif", "bmp"};

  public ImagePreviewHandler(String... extensions) {
    setExtensions(extensions);
  }

  public ImagePreviewHandler() {
  }

  @Override
  public BufferedImage createImage(@NotNull URL url) throws IOException {
    return ImageIO.read(url);
  }

  @Override
  public FileFilter getFileFilter() {
    return new FileNameExtensionFilter("Images", extensions);
  }

  public String[] getExtensions() {
    return extensions;
  }

  public void setExtensions(String... extensions) {
    this.extensions = extensions;
  }
}
