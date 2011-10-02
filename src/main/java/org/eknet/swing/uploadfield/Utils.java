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

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URL;

import javax.swing.filechooser.FileFilter;

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

  private final static double KB = 1024;
  private final static double MB = KB * KB;

  public static String toSizeString(double bytes) {
    BigDecimal mb = null;
    String unit;
    if (bytes > MB) {
      mb = new BigDecimal(bytes / MB);
      unit = "Mb";
    } else {
      mb = new BigDecimal(bytes / KB);
      unit = "Kb";
    }
    mb = mb.setScale(2, BigDecimal.ROUND_HALF_EVEN);
    return mb.toString() + " " + unit;
  }

  private static BufferedImage missingImage = new BufferedImage(128, 128, BufferedImage.TYPE_INT_ARGB);
  static {
    Graphics2D g = missingImage.createGraphics();
    g.setColor(Color.black);
    int gap = 20;
    g.drawLine(gap, gap, missingImage.getWidth() - gap, missingImage.getHeight() - gap);
    g.drawLine(missingImage.getWidth() - gap, gap, gap, missingImage.getHeight() - gap);
  }
  public static BufferedImage getMissingImage() {
    return missingImage;
  }

  public static void copy(URL in, File out) throws IOException {
    InputStream is = in.openStream();
    OutputStream os = new FileOutputStream(out);
    byte[] buffer = new byte[4096];
    int len;
    while ((len = is.read(buffer)) != -1) {
      os.write(buffer, 0, len);
    }
    os.close();
    is.close();
  }

  private final static FileFilter allFiles = new FileFilter() {
    @Override
    public boolean accept(File f) {
      return true;
    }

    @Override
    public String getDescription() {
      return "*";
    }
  };

  public static PreviewHandler allFileHandler() {
    return new PreviewHandler() {
      @Override
      public BufferedImage createImage(URL url) throws IOException {
        return null;
      }

      @Override
      public FileFilter getFileFilter() {
        return allFiles;
      }
    };
  }
}
