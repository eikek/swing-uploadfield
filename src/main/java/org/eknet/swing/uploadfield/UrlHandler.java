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

import org.jetbrains.annotations.Nullable;

/**
 * Processes an input from the user.
 *
 * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
 * @since 31.10.11 18:35
 */
public interface UrlHandler {

  /**
   * Creates an image from the specified url. The image is
   * used for creating a preview and may be scaled to an
   * appropriate size.
   *
   * @param url
   * @return
   * @throws IOException
   */
  @Nullable
  BufferedImage createImage(URL url) throws IOException;

  /**
   * Returns the file name of the specified url. Return {@code null}
   * to have the default handler figure out the name.
   * 
   * @param url
   * @return
   */
  @Nullable
  String getName(URL url);

  /**
   * After the file has been loaded, this is invoked to get a more
   * detailed description of the file.
   *
   * @param value
   * @return
   */
  @Nullable
  String getDescription(UploadValue value);
}
