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

import java.io.File;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;

import eu.medsea.mimeutil.MimeType;
import eu.medsea.mimeutil.MimeUtil2;
import eu.medsea.mimeutil.detector.ExtensionMimeDetector;
import eu.medsea.mimeutil.detector.MagicMimeMimeDetector;
import eu.medsea.mimeutil.detector.OpendesktopMimeDetector;
import eu.medsea.mimeutil.detector.WindowsRegistryMimeDetector;
import org.jetbrains.annotations.NotNull;

// this file is a modified copy from org.eknet.filedb.impl.util.MimeTypes

/**
 * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
 * @since 23.09.11 22:03
 */
public final class MimeTypes {

  private MimeTypes() {}

  private static MimeUtil2 mime = new MimeUtil2();
  static {
    if (isWindows()) {
      mime.registerMimeDetector(WindowsRegistryMimeDetector.class.getName());
    }
    mime.registerMimeDetector(ExtensionMimeDetector.class.getName());
    mime.registerMimeDetector(MagicMimeMimeDetector.class.getName());
    mime.registerMimeDetector(OpendesktopMimeDetector.class.getName());
  }

  private static boolean isWindows() {
    return System.getProperty("os.name").startsWith("Windows");
  }

  @NotNull
  public static MimeType getMimeType(@NotNull File file) {
    Collection<MimeType> types = mime.getMimeTypes(file);
    Iterator<MimeType> iter = types.iterator();
    if (iter.hasNext()) {
      return iter.next();
    } else {
      return MimeUtil2.UNKNOWN_MIME_TYPE;
    }
  }

  @NotNull
  public static MimeType getMimeType(@NotNull URL url) {
    Collection<MimeType> types = mime.getMimeTypes(url);
    Iterator<MimeType> iter = types.iterator();
    if (iter.hasNext()) {
      return iter.next();
    } else {
      return MimeUtil2.UNKNOWN_MIME_TYPE;
    }
  }
}
