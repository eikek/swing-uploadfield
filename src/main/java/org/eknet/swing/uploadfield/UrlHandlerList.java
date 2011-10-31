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

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
 * @since 31.10.11 18:40
 */
class UrlHandlerList implements UrlHandler {

  private final List<UrlHandler> handlers = new CopyOnWriteArrayList<UrlHandler>();
  private Dimension iconSize = new Dimension(25, 25);
  private UrlHandler fallback;

  UrlHandlerList(UrlHandler fallback, Dimension iconSize) {
    this.fallback = fallback;
    this.iconSize = iconSize;
  }

  UrlHandlerList(Dimension iconSize) {
    this.iconSize = iconSize;
  }

  public void addHandler(@NotNull UrlHandler handler) {
    this.handlers.add(handler);
  }

  public void removeHander(@NotNull UrlHandler handler) {
    this.handlers.remove(handler);
  }

  public void setFallback(@Nullable UrlHandler fallback) {
    this.fallback = fallback;
  }

  public Iterable<UrlHandler> copy() {
    return Arrays.asList(handlers.toArray(new UrlHandler[handlers.size()]));
  }

  public Dimension getIconSize() {
    return iconSize;
  }

  public void setIconSize(Dimension iconSize) {
    this.iconSize = iconSize;
  }

  @Override
  public BufferedImage createImage(URL url) throws IOException {
    return createImage(url, iconSize);
  }

  public BufferedImage createImage(URL url, Dimension maxSize) throws IOException {
    if (url == null) {
      return null;
    }
    BufferedImage image = null;
    for (UrlHandler uh : handlers) {
      image = uh.createImage(url);
      if (image != null) {
        break;
      }
    }
    if (image == null && fallback != null) {
      image = fallback.createImage(url);
    }
    if (image != null) {
      image = Scales.scaleIfNecessary(image, maxSize.width, maxSize.height);
    }
    return image;
  }

  @Override
  public String getName(URL url) {
    for (UrlHandler uh : handlers) {
      String name = uh.getName(url);
      if (name != null) {
        return name;
      }
    }
    if (fallback != null) {
      return fallback.getName(url);
    }
    return null;
  }

  @Override
  public String getDescription(UploadValue value) {
    for (UrlHandler uh : handlers) {
      String name = uh.getDescription(value);
      if (name != null) {
        return name;
      }
    }
    if (fallback != null) {
      return fallback.getDescription(value);
    }
    return null;
  }
}
