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
import java.util.ArrayList;
import java.util.List;

import javax.swing.filechooser.FileFilter;

/**
 * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
 * @since 02.10.11 03:19
 */
class CompoundFileFilter extends FileFilter {

  private List<FileFilter> filters = new ArrayList<FileFilter>();

  public void addFilter(FileFilter filter) {
    if (filter != null) {
      filters.add(filter);
    }
  }

  public void removeFilter(FileFilter filter) {
    if (filter != null) {
      filters.remove(filter);
    }
  }

  public void addAll(Iterable<FileFilter> filters) {
    for (FileFilter f : filters) {
      addFilter(f);
    }
  }

  public void removeAll(Iterable<FileFilter> filters) {
    for (FileFilter f : filters) {
      removeFilter(f);
    }
  }

  @Override
  public boolean accept(File f) {
    for (FileFilter filter : filters) {
      if (filter.accept(f)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public String getDescription() {
    StringBuilder buf = new StringBuilder();
    boolean first = true;
    for (FileFilter filter : filters) {
      if (first) {
        first = false;
      } else {
        buf.append(", ");
      }
      buf.append(filter.getDescription());
    }
    return buf.toString();
  }
}
