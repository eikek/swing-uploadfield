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

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.Icon;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The value of the {@link ImageInput} component.
 * 
 * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
 * @since 01.10.11 15:39
 */
public final class ImageValue implements Cloneable, Comparable<ImageValue> {

  private final PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

  private URL imageResource;
  private String imageName;

  private BufferedImage image;
  private Icon icon;
  private File imageFile;

  public ImageValue(URL imageResource, String imageName) {
    this.imageResource = imageResource;
    this.imageName = imageName;
  }

  public ImageValue(URL imageResource) {
    this.imageResource = imageResource;
  }

  public ImageValue(ImageValue value) {
    if (value != null) {
      this.imageName = value.getImageName();
      this.imageResource = value.getImageResource();
      this.image = value.getImage();
      this.icon = value.getIcon();
      this.imageFile = value.getImageFile();
    }
  }

  public ImageValue() {
  }

  public URL getImageResource() {
    return imageResource;
  }

  public void setImageResource(URL imageResource) {
    URL old = getImageResource();
    this.imageResource = imageResource;
    changeSupport.firePropertyChange("imageResource", old, imageResource);
    if (getImageName() == null && imageResource != null) {
      setImageName(Utils.lastUrlPart(imageResource));
    }
    setImage(null);
    setImageFile(null);
    setIcon(null);
  }

  public String getImageName() {
    return imageName;
  }

  public void setImageName(String imageName) {
    String old = getImageName();
    this.imageName = imageName;
    changeSupport.firePropertyChange("imageName", old, imageName);
  }

  @Nullable
  public BufferedImage readImage() throws IOException {
    if (isNullOrEmpty(this)) {
      throw new IllegalStateException("No image resource set");
    }
    return ImageIO.read(getImageResource());
  }

  public BufferedImage getOrReadImage() throws IOException {
    if (image == null) {
      image = readImage();
    }
    return image;
  }

  public BufferedImage getImage() {
    return image;
  }

  public void setImage(@Nullable BufferedImage image) {
    BufferedImage old = getImage();
    this.image = image;
    changeSupport.firePropertyChange("image", old, image);
  }

  @NotNull
  public BufferedImage getScaledImage(int maxWidth, int maxHeight) throws IOException {
    final BufferedImage img = getOrReadImage();
    if (img == null) {
      throw new IllegalStateException("Cannot read image from url: " + imageResource);
    }
    return Scales.scaleIfNecessary(img, maxWidth, maxHeight);
  }

  @NotNull
  public BufferedImage getScaledImage(Dimension maxWidthAndHeight) throws IOException {
    return getScaledImage(maxWidthAndHeight.width, maxWidthAndHeight.height);
  }

  public File getImageFile() {
    return imageFile;
  }

  public void setImageFile(@Nullable File imageFile) {
    File old = getImageFile();
    this.imageFile = imageFile;
    changeSupport.firePropertyChange("imageFile", old, imageFile);
  }

  public Icon getIcon() {
    return icon;
  }

  public void setIcon(@Nullable Icon icon) {
    Icon old = getIcon();
    this.icon = icon;
    changeSupport.firePropertyChange("icon", old, icon);
  }

  public void addPropertyChangeListener(PropertyChangeListener listener) {
    if (listener != null) {
      changeSupport.addPropertyChangeListener(listener);
    }
  }

  public void removePropertyChangeListener(PropertyChangeListener listener) {
    if (listener != null) {
      changeSupport.removePropertyChangeListener(listener);
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    ImageValue that = (ImageValue) o;

    if (imageName != null ? !imageName.equals(that.imageName) : that.imageName != null) return false;
    if (imageResource != null ? !imageResource.equals(that.imageResource) : that.imageResource != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = imageResource != null ? imageResource.hashCode() : 0;
    result = 31 * result + (imageName != null ? imageName.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "ImageValue{" +
            "imageResource=" + imageResource +
            ", imageName='" + imageName + '\'' +
            ", image='" + (image != null) + '\'' +
            ", icon='" + (icon != null) + "'" +
            ", imageFile='" + (imageFile != null) + "'" +
            '}';
  }

  @SuppressWarnings({"CloneDoesntCallSuperClone"})
  @Override
  public ImageValue clone() throws CloneNotSupportedException {
    return new ImageValue(this);
  }

  @Override
  public int compareTo(ImageValue o) {
    if (isNullOrEmpty(o)) {
      return -1;
    }
    if (isNullOrEmpty(this)) {
      return +1;
    }
    return imageResource.toString().compareTo(o.imageResource.toString());
  }

  public static boolean isNullOrEmpty(ImageValue value) {
    return value == null
            || value.getImageResource() == null
            && (value.getImageName() == null || "".equals(value.getImageName().trim()));
  }
}
