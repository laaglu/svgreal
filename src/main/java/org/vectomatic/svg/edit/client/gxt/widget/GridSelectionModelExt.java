/**********************************************
 * Copyright (C) 2011 Lukas Laag
 * This file is part of vectomatic2.
 * 
 * vectomatic2 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * vectomatic2 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with vectomatic2.  If not, see http://www.gnu.org/licenses/
 **********************************************/
package org.vectomatic.svg.edit.client.gxt.widget;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.widget.grid.GridSelectionModel;

/**
 * Subclass to GridSelectionModel which allows the grid to be null
 * @author laaglu
 * @param <M>
 * The grid model class.
 */
public class GridSelectionModelExt<M extends ModelData> extends GridSelectionModel<M> {
	  @Override
	  protected void onLastFocusChanged(M oldFocused, M newFocused) {
		  if (grid != null) {
			  super.onLastFocusChanged(oldFocused, newFocused);
		  }
	  }
	  @Override
	  protected void onSelectChange(M model, boolean select) {
		  if (grid != null) {
			  super.onSelectChange(model, select);
		  }
	  }
	  
	  public int getCount() {
		  return selected.size();
	  }
	  
	  public int getSelectedIndex() {
		  return (lastSelected != null) ? getIndex(lastSelected) : -1;
	  }
	  
	  public int getIndex(M model) {
		  for (int i = 0, size = listStore.getCount(); i < size; i++) {
			  if (listStore.getAt(i) == model) {
				  return i;
			  }
		  }
		  return -1;
	  }
}
