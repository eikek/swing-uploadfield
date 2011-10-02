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

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.BufferedImage;

import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
 * @since 04.06.11 20:44
 */
class Scales {

  /**
   * Scales the image using {@link #getScaledInstance(java.awt.image.BufferedImage, int, int, Object, boolean)} if the size of
   * the image exceeds the specified boundaries. The image is scaled proportional. If the image is within the
   * specified boundaries it is returned.
   *
   * @param image
   * @param maxWidth
   * @param maxHeight
   * @return
   */
  public static BufferedImage scaleIfNecessary(@NotNull BufferedImage image, final int maxWidth, final int maxHeight) {
    int h = image.getHeight();
    int w = image.getWidth();
    if (h > maxHeight || w > maxWidth) {
      int dw = Math.abs(maxWidth - w);
      int dh = Math.abs(maxHeight - h);
      if (dw > dh) {
        float factor = (maxWidth * 1.0f) / w;
        int nh = (int) Math.floor(h * factor);
        return getScaledInstance(image, maxWidth, nh, RenderingHints.VALUE_INTERPOLATION_BILINEAR, true);
      } else {
        float factor = (maxHeight * 1.0f) / h;
        int nw = (int) Math.floor(w * factor);
        return getScaledInstance(image, nw, maxHeight, RenderingHints.VALUE_INTERPOLATION_BILINEAR, true);
      }
    }
    return image;
  }

  /**
   * Note, found this code on <a href="http://today.java.net/pub/a/today/2007/04/03/perils-of-image-getscaledinstance.html">this post</a>
   * by <a href="http://today.java.net/pub/au/60">Chris Campbell</a>.
   * <p/>
   * <p/>
   * Convenience method that returns a scaled instance of the
   * provided {@code BufferedImage}.
   *
   * @param img           the original image to be scaled
   * @param targetWidth   the desired width of the scaled instance,
   *                      in pixels
   * @param targetHeight  the desired height of the scaled instance,
   *                      in pixels
   * @param hint          one of the rendering hints that corresponds to
   *                      {@code RenderingHints.KEY_INTERPOLATION} (e.g.
   *                      {@code RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR},
   *                      {@code RenderingHints.VALUE_INTERPOLATION_BILINEAR},
   *                      {@code RenderingHints.VALUE_INTERPOLATION_BICUBIC})
   * @param higherQuality if true, this method will use a multi-step
   *                      scaling technique that provides higher quality than the usual
   *                      one-step technique (only useful in downscaling cases, where
   *                      {@code targetWidth} or {@code targetHeight} is
   *                      smaller than the original dimensions, and generally only when
   *                      the {@code BILINEAR} hint is specified)
   * @return a scaled version of the original {@code BufferedImage}
   */
  public static BufferedImage getScaledInstance(BufferedImage img,
                                                int targetWidth,
                                                int targetHeight,
                                                Object hint,
                                                boolean higherQuality) {

    int type = (img.getTransparency() == Transparency.OPAQUE) ?
            BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
    BufferedImage ret = (BufferedImage) img;
    int w, h;
    if (higherQuality) {
      // Use multi-step technique: start with original size, then
      // scale down in multiple passes with drawImage()
      // until the target size is reached
      w = img.getWidth();
      h = img.getHeight();
    } else {
      // Use one-step technique: scale directly from original
      // size to target size with a single drawImage() call
      w = targetWidth;
      h = targetHeight;
    }

    do {
      if (higherQuality && w > targetWidth) {
        w /= 2;
        if (w < targetWidth) {
          w = targetWidth;
        }
      }

      if (higherQuality && h > targetHeight) {
        h /= 2;
        if (h < targetHeight) {
          h = targetHeight;
        }
      }

      BufferedImage tmp = new BufferedImage(w, h, type);
      Graphics2D g2 = tmp.createGraphics();
      g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, hint);
      g2.drawImage(ret, 0, 0, w, h, null);
      g2.dispose();

      ret = tmp;
    } while (w != targetWidth || h != targetHeight);

    return ret;
  }

}
