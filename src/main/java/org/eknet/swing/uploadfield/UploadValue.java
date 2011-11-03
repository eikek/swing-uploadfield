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
import java.net.URL;

import javax.swing.Icon;

import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
 * @since 03.11.11 23:18
 */
public interface UploadValue {

  URL getResource();

  void setResource(URL resource);

  String getDescription();

  void setDescription(String description);

  String getName();

  void setName(String name);

  BufferedImage getImage();

  void setImage(@Nullable BufferedImage image);

  File getFile();

  void setFile(@Nullable File file);

  Icon getIcon();

  void setIcon(@Nullable Icon icon);

  /**
   * Called if the upload field cannot determine a preview
   * image.
   */
  void setMissingIcon(int w, int h);
}
