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
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.medsea.mimeutil.MimeType;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
 * @since 27.10.11 10:05
 */
public class MimeIconMap {
  private static final Logger log = LoggerFactory.getLogger(MimeIconMap.class);

  public static final String UNKNOWN = "unknown.png";
  public static final String BINARY = "binary.png";
  public static final String ISO_IMAGE = "cdimage.png";
  public static final String DEB = "deb.png";
  public static final String DOCUMENT = "document.png";
  public static final String OO_DRAW = "drawing_document.png";
  public static final String ENCRYPTED = "encrypted.png";
  public static final String EXE = "exec_wine.png";
  public static final String FONT_T1 = "font_type1.png";
  public static final String GIMP = "gimp.png";
  public static final String HTML = "html.png";
  public static final String IMAGE = "image.png";
  public static final String MAKE = "make.png";
  public static final String EMAIL = "message.png";
  public static final String MIDI = "midi.png";
  public static final String PATCH = "mime_diff.png";
  public static final String OO_SOFFICE = "mime_soffice.png";
  public static final String PDF = "pdf.png";
  public static final String PS = "postscript.png";
  public static final String OO_PRES = "presentation_document.png";
  public static final String RPM = "rpm.png";
  public static final String SHELL = "shellscript.png";
  public static final String AUDIO = "sound.png";
  public static final String SOURCE_C = "source_c.png";
  public static final String SOURCE_CPP = "source_cpp.png";
  public static final String SOURCE_H = "source_h.png";
  public static final String SOURCE_JAVA = "source_java.png";
  public static final String SOURCE_MOC = "source_moc.png";
  public static final String SOURCE_O = "source_o.png";
  public static final String SPREADSHEET = "spreadsheet.png";
  public static final String OO_SPREADSHEET = "spreadsheet_document.png";
  public static final String TAR = "tar.png";
  public static final String TEX = "tex.png";
  public static final String OO_TEXT = "text_document.png";
  public static final String ZIP = "tgz.png";
  public static final String TXT = "txt.png";
  public static final String SVG = "vectorgfx.png";
  public static final String VIDEO = "video.png";


  /**
   * maps resource strings to images
   */
  private final Map<String, BufferedImage> imageCache = new HashMap<String, BufferedImage>();

  /**
   * maps mime types to image resources.
   */
  private final Map<String, String> mimeResourceMap = new HashMap<String, String>();

  public MimeIconMap() {
    imageCache.put(UNKNOWN, createImage(UNKNOWN));
    initializeResourceMap();
  }

  protected void initializeResourceMap() {
    mimeResourceMap.put("image", IMAGE);
    mimeResourceMap.put("image/svg+xml", SVG);

    mimeResourceMap.put("audio", AUDIO);
    mimeResourceMap.put("audio/midi", MIDI);

    mimeResourceMap.put("video", VIDEO);

    mimeResourceMap.put("text", TXT);
    mimeResourceMap.put("text/plain", TXT);
    mimeResourceMap.put("text/html", HTML);
    mimeResourceMap.put("text/rtf", DOCUMENT);
    mimeResourceMap.put("text/x-makefile", MAKE);
    mimeResourceMap.put("message/rfc822", EMAIL);
    mimeResourceMap.put("text/x-diff", PATCH);
    mimeResourceMap.put("text/x-c++src", SOURCE_CPP);
    mimeResourceMap.put("text/x-c++hdr", SOURCE_H);
    mimeResourceMap.put("text/x-chdr", SOURCE_H);
    mimeResourceMap.put("text/x-csrc", SOURCE_C);
    mimeResourceMap.put("text/x-moc", SOURCE_MOC);
    mimeResourceMap.put("text/x-sh", SHELL);
    mimeResourceMap.put("text/x-java", SOURCE_JAVA);
    mimeResourceMap.put("text/x-tex", TEX);

    mimeResourceMap.put("application", BINARY);
    mimeResourceMap.put("application/pdf", PDF);
    mimeResourceMap.put("application/x-iso9660-image", ISO_IMAGE);
    mimeResourceMap.put("application/x-debian-package", DEB);
    mimeResourceMap.put("application/x-abiword", DOCUMENT);
    mimeResourceMap.put("application/msword", DOCUMENT);
    mimeResourceMap.put("application/vnd.openxmlformats-officedocument.wordprocessingml.document", DOCUMENT);
    mimeResourceMap.put("application/vnd.oasis.opendocument.graphics", OO_DRAW);
    mimeResourceMap.put("application/vnd.oasis.opendocument.graphics-template", OO_SOFFICE);
    mimeResourceMap.put("application/vnd.oasis.opendocument.image", OO_DRAW);
    mimeResourceMap.put("application/vnd.oasis.opendocument.chart", OO_SOFFICE);
    mimeResourceMap.put("application/vnd.oasis.opendocument.database", OO_SOFFICE);
    mimeResourceMap.put("application/vnd.oasis.opendocument.formula", OO_SOFFICE);
    mimeResourceMap.put("application/vnd.oasis.opendocument.presentation", OO_PRES);
    mimeResourceMap.put("application/vnd.oasis.opendocument.presentation-template", OO_SOFFICE);
    mimeResourceMap.put("application/vnd.oasis.opendocument.spreadsheet", OO_SPREADSHEET);
    mimeResourceMap.put("application/vnd.oasis.opendocument.spreadsheet-template", OO_SOFFICE);
    mimeResourceMap.put("application/vnd.oasis.opendocument.text", OO_TEXT);
    mimeResourceMap.put("application/vnd.oasis.opendocument.text-master", OO_TEXT);
    mimeResourceMap.put("application/vnd.oasis.opendocument.text-template", OO_SOFFICE);
    mimeResourceMap.put("application/vnd.oasis.opendocument.text-web", OO_TEXT);
    mimeResourceMap.put("application/pgp-encrypted", ENCRYPTED);
    mimeResourceMap.put("application/pgp-keys", ENCRYPTED);
    mimeResourceMap.put("application/pgp-signature", ENCRYPTED);
    mimeResourceMap.put("application/x-msdos-program", EXE);
    mimeResourceMap.put("application/x-font", FONT_T1);
    mimeResourceMap.put("application/x-xcf", GIMP);
    mimeResourceMap.put("application/postscript", PS);
    mimeResourceMap.put("application/x-redhat-package-manager", RPM);
    mimeResourceMap.put("application/x-sh", SHELL);
    mimeResourceMap.put("application/x-shar", SHELL);
    mimeResourceMap.put("application/x-gtar", TAR);
    mimeResourceMap.put("application/x-tar", TAR);
    mimeResourceMap.put("application/x-7z-compressed", ZIP);
    mimeResourceMap.put("application/x-gtar-compressed", ZIP);
    mimeResourceMap.put("application/x-gzip", ZIP);
    mimeResourceMap.put("application/x-zip-compressed", ZIP);
    mimeResourceMap.put("application/zip", ZIP);
  }

  @NotNull
  protected BufferedImage createImage(String resource) {
    URL ir = MimeIconMap.class.getResource("mime-types/" + resource);
    try {
      return ImageIO.read(ir);
    } catch (IOException e) {
      log.error("Unable to load icon image! Return 'missing-image'.", e);
      return Utils.getMissingImage();
    }
  }

  @NotNull
  protected String mapMimeToResource(MimeType mime) {
    String resource = mimeResourceMap.get(mime.toString());
    if (resource != null) {
      return resource;
    }
    resource = mimeResourceMap.get(mime.getMediaType());
    if (resource != null) {
      return resource;
    }
    return UNKNOWN;
  }

  public BufferedImage getIconImage(@NotNull MimeType mime) {
    String resource = mapMimeToResource(mime);
    BufferedImage img = imageCache.get(resource);
    if (img != null) {
      return img;
    }
    img = createImage(resource);
    imageCache.put(resource, img);
    return img;
  }
}
