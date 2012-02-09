/**********************************************
 * Copyright (C) 2010 Lukas Laag
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
package org.vectomatic.svg.edit.client;

import org.vectomatic.dom.svg.ui.SVGResource.Validated;
import org.vectomatic.dom.svg.ui.SVGResource;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * Application resource bundle class
 * @author laaglu
 */
public interface AppBundle extends ClientBundle {
	AppBundle INSTANCE = GWT.create(AppBundle.class);
	/////////////////////////////////
	// Css resources
	/////////////////////////////////
	@Source("app.css")
	AppCss css();

	/////////////////////////////////
	// Sample documents
	/////////////////////////////////
	@Source("altum_angelfish_01.svg")
	SVGResource fish();
	@Source("french_fries_juliane_kr_r.svg")
	SVGResource fries();
	@Source("sample.svg")
	SVGResource sample();
	@Source("chess.svg")
	@Validated(validated=false)
	SVGResource chess();
	
	/////////////////////////////////
	// Svg resources
	/////////////////////////////////
	@Source("compass.svg")
	SVGResource compass();
	@Source("dndIcons.svg")
	SVGResource dndIcons();
	@Source("svgreal.svg")
	SVGResource logo();
	@Source("Gpl-v3-logo.svg")
	SVGResource gplv3();

	/////////////////////////////////
	// Treeview icons
	/////////////////////////////////
    @Source("tree/altGlyphDef.png")
    ImageResource altGlyphDef();
    @Source("tree/altGlyphItem.png")
    ImageResource altGlyphItem();
    @Source("tree/altGlyph.png")
    ImageResource altGlyph();
    @Source("tree/animateColor.png")
    ImageResource animateColor();
    @Source("tree/animateMotion.png")
    ImageResource animateMotion();
    @Source("tree/animate.png")
    ImageResource animate();
    @Source("tree/animateTransform.png")
    ImageResource animateTransform();
    @Source("tree/a.png")
    ImageResource a();
    @Source("tree/circle.png")
    ImageResource circle();
    @Source("tree/clipPath.png")
    ImageResource clipPath();
    @Source("tree/color-profile.png")
    ImageResource colorProfile();
    @Source("tree/cursor.png")
    ImageResource cursor();
    @Source("tree/defs.png")
    ImageResource defs();
    @Source("tree/desc.png")
    ImageResource desc();
    @Source("tree/ellipse.png")
    ImageResource ellipse();
    @Source("tree/feBlend.png")
    ImageResource feBlend();
    @Source("tree/feColorMatrix.png")
    ImageResource feColorMatrix();
    @Source("tree/feComponentTransfer.png")
    ImageResource feComponentTransfer();
    @Source("tree/feComposite.png")
    ImageResource feComposite();
    @Source("tree/feConvolveMatrix.png")
    ImageResource feConvolveMatrix();
    @Source("tree/feDiffuseLighting.png")
    ImageResource feDiffuseLighting();
    @Source("tree/feDisplacementMap.png")
    ImageResource feDisplacementMap();
    @Source("tree/feDistantLight.png")
    ImageResource feDistantLight();
    @Source("tree/feFlood.png")
    ImageResource feFlood();
    @Source("tree/feFuncA.png")
    ImageResource feFuncA();
    @Source("tree/feFuncB.png")
    ImageResource feFuncB();
    @Source("tree/feFuncG.png")
    ImageResource feFuncG();
    @Source("tree/feFuncR.png")
    ImageResource feFuncR();
    @Source("tree/feGaussianBlur.png")
    ImageResource feGaussianBlur();
    @Source("tree/feImage.png")
    ImageResource feImage();
    @Source("tree/feMergeNode.png")
    ImageResource feMergeNode();
    @Source("tree/feMerge.png")
    ImageResource feMerge();
    @Source("tree/feMorphology.png")
    ImageResource feMorphology();
    @Source("tree/feOffset.png")
    ImageResource feOffset();
    @Source("tree/fePointLight.png")
    ImageResource fePointLight();
    @Source("tree/feSpecularLight.png")
    ImageResource feSpecularLight();
    @Source("tree/feSpotLight.png")
    ImageResource feSpotLight();
    @Source("tree/feTile.png")
    ImageResource feTile();
    @Source("tree/feTurbulence.png")
    ImageResource feTurbulence();
    @Source("tree/filter.png")
    ImageResource filter();
    @Source("tree/font-face-format.png")
    ImageResource fontFaceFormat();
    @Source("tree/font-face-name.png")
    ImageResource fontFaceName();
    @Source("tree/font-face.png")
    ImageResource fontFace();
    @Source("tree/font-face-src.png")
    ImageResource fontFaceSrc();
    @Source("tree/font-face-uri.png")
    ImageResource fontFaceUri();
    @Source("tree/font.png")
    ImageResource font();
    @Source("tree/foreignObject.png")
    ImageResource foreignObject();
    @Source("tree/glyph.png")
    ImageResource glyph();
    @Source("tree/glyphRef.png")
    ImageResource glyphRef();
    @Source("tree/g.png")
    ImageResource g();
    @Source("tree/hkern.png")
    ImageResource hkern();
    @Source("tree/image.png")
    ImageResource image();
    @Source("tree/linearGradient.png")
    ImageResource linearGradient();
    @Source("tree/line.png")
    ImageResource line();
    @Source("tree/marker.png")
    ImageResource marker();
    @Source("tree/mask.png")
    ImageResource mask();
    @Source("tree/metadata.png")
    ImageResource metadata();
    @Source("tree/missing-glyph.png")
    ImageResource missingGlyph();
    @Source("tree/mpath.png")
    ImageResource mpath();
    @Source("tree/path.png")
    ImageResource path();
    @Source("tree/pattern.png")
    ImageResource pattern();
    @Source("tree/polygon.png")
    ImageResource polygon();
    @Source("tree/polyline.png")
    ImageResource polyline();
    @Source("tree/radialGradient.png")
    ImageResource radialGradient();
    @Source("tree/rect.png")
    ImageResource rect();
    @Source("tree/script.png")
    ImageResource script();
    @Source("tree/set.png")
    ImageResource set();
    @Source("tree/stop.png")
    ImageResource stop();
    @Source("tree/style.png")
    ImageResource style();
    @Source("tree/svg.png")
    ImageResource svg();
    @Source("tree/switch.png")
    ImageResource switch_();
    @Source("tree/symbol.png")
    ImageResource symbol();
    @Source("tree/textPath.png")
    ImageResource textPath();
    @Source("tree/text.png")
    ImageResource text();
    @Source("tree/title.png")
    ImageResource title();
    @Source("tree/tref.png")
    ImageResource tref();
    @Source("tree/tspan.png")
    ImageResource tspan();
    @Source("tree/use.png")
    ImageResource use();
    @Source("tree/view.png")
    ImageResource view();
    @Source("tree/viewBox.png")
    ImageResource viewBox();
    @Source("tree/vkern.png")
    ImageResource vkern();

	/////////////////////////////////
	// Treeview state icons
	/////////////////////////////////
	@Source("warning_co.gif")
	ImageResource warning();
	@Source("error_ovr.gif")
	ImageResource error();

	/////////////////////////////////
	// Button icons
	/////////////////////////////////
    @Source("addPoint.png")
    ImageResource addPoint();
    @Source("insertPoint.png")
    ImageResource insertPoint();
    @Source("removePoints.png")
    ImageResource removePoints();
    @Source("undo.png")
    ImageResource undo();
    @Source("redo.png")
    ImageResource redo();

	/////////////////////////////////
	// Path icons
	/////////////////////////////////
    @Source("path-move.png")
    ImageResource pathMove();
    @Source("path-line.png")
    ImageResource pathLine();
    @Source("path-quadratic.png")
    ImageResource pathQuadratic();
    @Source("path-cubic.png")
    ImageResource pathCubic();
    @Source("path-close.png")
    ImageResource pathClose();
    
	/////////////////////////////////
	// Color editor icons
	/////////////////////////////////
	ImageResource cvslider();
	ImageResource cvsliderSliding();
	ImageResource chslider();
	ImageResource chsliderSliding();

	/////////////////////////////////
	// CSS property icons
	/////////////////////////////////
    @Source("paint-none.png")
    ImageResource paintNone();
    @Source("paint-current.png")
    ImageResource paintCurrent();
    @Source("paint-plain.png")
    ImageResource paintPlain();
    @Source("paint-linear.png")
    ImageResource paintLinear();
    @Source("paint-radial.png")
    ImageResource paintRadial();
    @Source("paint-pattern.png")
    ImageResource paintPattern();
    
    @Source("lineJoinMiter.png")
    ImageResource lineJoinMiter();
    @Source("lineJoinRound.png")
    ImageResource lineJoinRound();
    @Source("lineJoinBevel.png")
    ImageResource lineJoinBevel();

    @Source("lineCapButt.png")
    ImageResource lineCapButt();
    @Source("lineCapRound.png")
    ImageResource lineCapRound();
    @Source("lineCapSquare.png")
    ImageResource lineCapSquare();

    @Source("fillRuleNonZero.png")
    ImageResource fillRuleNonZero();
    @Source("fillRuleEvenOdd.png")
    ImageResource fillRuleEvenOdd();
    
    @Source("inherit.png")
    ImageResource inherit();

	/////////////////////////////////
	// Grid icons
	/////////////////////////////////
    @Source("grid.png")
    ImageResource grid();

	/////////////////////////////////
	// Country flag icons
	/////////////////////////////////
    @Source("flags/fr.png")
    ImageResource flagFr();
    @Source("flags/us.png")
    ImageResource flagUs();

}

