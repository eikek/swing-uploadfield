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
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * This is the default glass pane used with the {@link UploadField} component.
 * 
 * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
 * @since 01.10.11 18:30
 */
public class SimpleGlassPane extends JPanel {
  private Color color1;
  private Color color2;

  public SimpleGlassPane() {
    this.color1 = new Color(0, 0, 0, 175);
    this.color2 = new Color(0, 0, 0, 120);
    this.setVisible(false);

    setLayout(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.CENTER;
    final JLabel label = new JLabel("Loading image...");
    label.setForeground(Color.white);
    add(label, gbc);
  }

  @Override
  protected void paintComponent(Graphics g) {
    if (!isOpaque()) {
      super.paintComponent(g);
      return;
    }
    Graphics2D g2d = (Graphics2D) g;
    GradientPaint fill = new GradientPaint(0, 0, color1, 0, getHeight(), color2);
    g2d.setPaint(fill);
    g2d.fillRect(0, 0, getWidth(), getHeight());

    setOpaque(false);
    super.paintComponent(g);
    setOpaque(true);
  }

}
