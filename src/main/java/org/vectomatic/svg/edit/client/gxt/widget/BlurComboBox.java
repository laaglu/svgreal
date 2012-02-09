/**********************************************
 * Copyright (C) 2011 Lukas Laag
 * This file is part of svgreal.
 * 
 * svgreal is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * svgreal is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with svgreal.  If not, see http://www.gnu.org/licenses/
 **********************************************/
package org.vectomatic.svg.edit.client.gxt.widget;

import org.vectomatic.svg.edit.client.SVGWindow;
import org.vectomatic.svg.edit.client.SvgrealApp;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;

/**
 * Combo box class to make it easier for the end-user to cause the combo box to blur.
 * @author laaglu
 *
 * @param <D>
 */
public class BlurComboBox<D extends ModelData> extends ComboBox<D> {
	/**
	 * Forces blur on a combo box. This is necessary as the
	 * combo box by default keeps the focus after an element has
	 * been chosen.
	 */
	public void triggerBlur() {
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				triggerBlur(null);
				SVGWindow activeWindow = SvgrealApp.getApp().getActiveWindow();
				if (activeWindow != null) {
					activeWindow.focus();
				}
			}
		});
	}
}
