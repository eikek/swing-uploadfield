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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
 * @since 01.10.11 20:44
 */
public class IconViewer extends JPanel {
  private static final Logger log = LoggerFactory.getLogger(IconViewer.class);

  private JList iconList;

  private Dimension previewSize = new Dimension(25, 25);
  
  public IconViewer() {
    super(new BorderLayout());

    iconList = new JList();
    iconList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    iconList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
    iconList.setVisibleRowCount(-1);
    iconList.setCellRenderer(new DefaultListCellRenderer() {
      @Override
      public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        setText(null);
        if (value != null) {
          try {
            BufferedImage img = ImageIO.read((URL) value);
            setIcon(new ImageIcon(Scales.scaleIfNecessary(img, previewSize.width, previewSize.height)));
          } catch (IOException e) {
            log.error("Cannot scale image: " + value, e);
          }
        } else {
          setIcon(null);
        }
        setToolTipText(Utils.lastUrlPart((URL) value));
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        setPreferredSize(previewSize);
        setMaximumSize(previewSize);
        setMinimumSize(previewSize);
        return this;
      }
    });
    add(new JScrollPane(iconList), BorderLayout.CENTER);
  }

  
  public void setIcons(@Nullable Iterable<URL> icons) {
    DefaultListModel model = new DefaultListModel();
    if (icons != null) {
      for (URL icon : icons) {
        model.addElement(icon);
      }
    }
    iconList.setModel(model);
  }

  public void setSelectionMode(int selectionMode) {
    iconList.setSelectionMode(selectionMode);
  }

  public void addListSelectionListener(ListSelectionListener listener) {
    iconList.addListSelectionListener(listener);
  }

  public void removeListSelectionListener(ListSelectionListener listener) {
    iconList.removeListSelectionListener(listener);
  }

  public ListSelectionListener[] getListSelectionListeners() {
    return iconList.getListSelectionListeners();
  }

  public JList getIconList() {
    return iconList;
  }
}
