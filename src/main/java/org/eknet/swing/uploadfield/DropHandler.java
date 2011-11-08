package org.eknet.swing.uploadfield;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
 * @since 07.11.11 19:05
 */
abstract class DropHandler extends TransferHandler {
  private final static Logger log = LoggerFactory.getLogger(DropHandler.class);
  
  private DataFlavor dataFlavor;

  public DropHandler() {
    try {
      this.dataFlavor = new DataFlavor("text/uri-list; class=java.lang.String");
    } catch (ClassNotFoundException e) {
      log.error("Error creating DataFlavor for DnD. Drop Support is disabled.");
      this.dataFlavor = null;
    }
  }

  @Override
  public boolean canImport(TransferSupport support) {
    if (dataFlavor == null) {
      return false;
    }
    return support.isDataFlavorSupported(dataFlavor);
  }

  @Override
  public boolean importData(TransferSupport support) {
    if (!canImport(support)) {
      return false;
    }
    try {
      Object uriObj = support.getTransferable().getTransferData(dataFlavor);
      if (uriObj instanceof String) {
        String[] uris = ((String) uriObj).split("\\s+");
        if (uris.length > 0) {
          List<UploadValue> values = new ArrayList<UploadValue>(uris.length);
          for (String str : uris) {
            URL url = new URL(str);
            final DefaultUploadValue value = new DefaultUploadValue(url);
            value.setName(Utils.lastUrlPart(url));
            values.add(value);
          }
          return setData(values);
        }
      }
    } catch (UnsupportedFlavorException e) {
      log.error("Error getting transferable data", e);
    } catch (IOException e) {
      log.error("Error getting transferable data", e);
    }
    return false;
  }

  protected abstract boolean setData(List<UploadValue> data);
}
