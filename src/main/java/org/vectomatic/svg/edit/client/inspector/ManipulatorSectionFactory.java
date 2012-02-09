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
package org.vectomatic.svg.edit.client.inspector;

import org.vectomatic.svg.edit.client.AppConstants;
import org.vectomatic.svg.edit.client.command.ICommandFactory;
import org.vectomatic.svg.edit.client.model.ModelCategory;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;

public class ManipulatorSectionFactory extends GenericSectionFactory {
	private ICommandFactory commandFactory;
	public ManipulatorSectionFactory(ICommandFactory commandFactory) {
		this.commandFactory = commandFactory;
	}
	@Override
	public IInspectorSection createSection(final ModelCategory category) {
		FormInspectorSection section = (FormInspectorSection) super.createSection(category);
		FormPanel panel = (FormPanel) section.getPanel();
		Button button = new Button(AppConstants.INSTANCE.displayManipulatorButton());
		button.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				commandFactory.start(ManipulatorSectionFactory.this);
			}
		});
		panel.add(button);
		return section;
	}
}
