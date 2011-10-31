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

import java.net.URL;

/**
 * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
 * @since 31.10.11 19:20
 */
public class FilesizeDescriptionUrlHandler extends UrlHandlerAdapter {

  @Override
  public String getDescription(UploadValue value) {
    StringBuilder buf = new StringBuilder();
    if (value.getFile() != null) {
      buf.append(Utils.toSizeString(value.getFile().length()));
    }
    return buf.toString();
  }

  @Override
  public String getName(URL url) {
    return Utils.lastUrlPart(url);
  }
}
