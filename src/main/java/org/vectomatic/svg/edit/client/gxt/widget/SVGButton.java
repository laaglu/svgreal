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

import org.vectomatic.dom.svg.ui.SVGImage;

import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.util.Util;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Accessibility;

/**
 * Button subclass which supports having an SVG image instead
 * of an icon
 * @author laaglu
 */
public class SVGButton extends Button {
	protected SVGImage svgImage;
	
	public SVGButton(SVGImage svgImage) {
		setSVGImage(svgImage);
	}

	public SVGImage getSVGImage() {
		return svgImage;
	}

	/**
	 * Sets the button's icon style. The style name should match a CSS style
	 * that specifies a background image using the following format:
	 * 
	 * <pre>
	 * 
	 * &lt;code&gt; .my-icon { background: url(images/icons/my-icon.png) no-repeat
	 * center left !important; } &lt;/code&gt;
	 * 
	 * </pre>
	 * 
	 * @param svgImage
	 *            the icon
	 */
	public void setSVGImage(SVGImage svgImage) {
		if (rendered) {
			El oldIcon = buttonEl.selectNode("." + baseStyle + "-image");
			if (oldIcon != null) {
				oldIcon.remove();
				el().removeStyleName(baseStyle + "-text-icon",
						baseStyle + "-icon", baseStyle + "-noicon");
			}
			el().addStyleName(
					(svgImage != null ? (!Util.isEmptyString(text) ? " "
							+ baseStyle + "-text-icon" : " " + baseStyle
							+ "-icon") : " " + baseStyle + "-noicon"));
			Element e = null;

			if (svgImage != null) {
				e = (Element) svgImage.getElement().cast();

				Accessibility.setRole(e, "presentation");
				fly(e).addStyleName(baseStyle + "-image");

				buttonEl.insertFirst(e);
				El.fly(e).makePositionable(true);

			}
			autoWidth();
			alignIcon(e);
		}
		this.svgImage = svgImage;
	}

	@Override
	protected void afterRender() {
		super.afterRender();
		setSVGImage(svgImage);
	}

}
