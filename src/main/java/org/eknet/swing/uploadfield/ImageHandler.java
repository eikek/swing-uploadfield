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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.filechooser.FileFilter;

import org.jetbrains.annotations.NotNull;

/**
 * A very simple preview handler which uses {@link ImageIO} to read an image
 * from the specified url.
 * 
 * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
 * @since 02.10.11 03:13
 */
public class ImageHandler extends FilesizeDescriptionUrlHandler {

  private Set<String> extensions = new HashSet<String>();

  private final FileFilter fileFilter = new FileFilter() {
    @Override
    public boolean accept(File f) {
      if (f.isDirectory()) {
        return true;
      }
      try {
        String ext = extractExtension(f.toURI().toURL());
        return extensions.contains(ext);
      } catch (MalformedURLException e) {
        return false;
      }
    }

    @Override
    public String getDescription() {
      return "Image files";
    }
  };

  public ImageHandler(String... extensions) {
    setExtensions(extensions);
  }

  public ImageHandler() {
    extensions.add("jpg");
    extensions.add("jpeg");
    extensions.add("gif");
    extensions.add("tif");
    extensions.add("tiff");
    extensions.add("png");
  }

  @Override
  public BufferedImage createImage(@NotNull URL url) throws IOException {
    return ImageIO.read(url);
  }

  @Override
  public String getDescription(UploadValue value) {
    if (!DefaultUploadValue.isNullOrEmpty(value)) {
      String ext = extractExtension(value.getResource());
      if (extensions.contains(ext)) {
        StringBuilder buf = new StringBuilder();
        if (value.getImage() != null) {
          buf.append(value.getImage().getWidth())
                  .append("x")
                  .append(value.getImage().getHeight())
                  .append("px");
          buf.append("; ");
        }
        buf.append(super.getDescription(value));
        return buf.toString();
      }
    }
    return null;
  }

  private String extractExtension(URL url) {
    if (url == null) {
      return null;
    }
    String path = url.getPath();
    int index = path.lastIndexOf(".");
    if (index >= 0) {
      return path.substring(index + 1).toLowerCase();
    }
    return null;
  }
  
  public Set<String> getExtensions() {
    return extensions;
  }

  public void setExtensions(Set<String> extensions) {
    if (extensions == null) {
      this.extensions = new HashSet<String>();
    } else {
      this.extensions = extensions;
    }
  }

  public void setExtensions(String... extensions) {
    this.extensions.clear();
    if (extensions != null) {
      Collections.addAll(this.extensions, extensions);
    }
  }

  public FileFilter getFileFilter() {
    return fileFilter;
  }
}
