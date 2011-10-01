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

import java.awt.Component;
import java.awt.Window;
import java.net.URL;

/**
 * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
 * @since 01.10.11 21:09
 */
final class Utils {

  public static String lastUrlPart(URL url) {
    String name = url.toString();
    return name.substring(name.lastIndexOf('/') + 1);
  }

  public static boolean nullSafeEquals(Object v1, Object v2) {
    if (v1 == null && v2 == null) {
      return true;
    }
    if (v1 == null) {
      return false;
    }
    if (v2 == null) {
      return false;
    }
    return v1.equals(v2);
  }

  public static Window findWindow(Component component) {
    Component c = component;
    while (c != null) {
      if (c instanceof Window) {
        return (Window) c;
      }
      c = c.getParent();
    }
    return null;
  }
}
