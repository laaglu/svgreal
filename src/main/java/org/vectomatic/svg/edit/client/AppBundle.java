/**********************************************
 * Copyright (C) 2010 Lukas Laag
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
package org.vectomatic.svg.edit.client;

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
	
	/////////////////////////////////
	// Svg resources
	/////////////////////////////////
	@Source("compass.svg")
	SVGResource compass();
	@Source("dndIcons.svg")
	SVGResource dndIcons();
	
	/////////////////////////////////
	// Treeview icons
	/////////////////////////////////
    @Source("altGlyphDef.png")
    ImageResource altGlyphDef();
    @Source("altGlyphItem.png")
    ImageResource altGlyphItem();
    @Source("altGlyph.png")
    ImageResource altGlyph();
    @Source("animateColor.png")
    ImageResource animateColor();
    @Source("animateMotion.png")
    ImageResource animateMotion();
    @Source("animate.png")
    ImageResource animate();
    @Source("animateTransform.png")
    ImageResource animateTransform();
    @Source("a.png")
    ImageResource a();
    @Source("circle.png")
    ImageResource circle();
    @Source("clipPath.png")
    ImageResource clipPath();
    @Source("color-profile.png")
    ImageResource colorProfile();
    @Source("cursor.png")
    ImageResource cursor();
    @Source("defs.png")
    ImageResource defs();
    @Source("desc.png")
    ImageResource desc();
    @Source("ellipse.png")
    ImageResource ellipse();
    @Source("feBlend.png")
    ImageResource feBlend();
    @Source("feColorMatrix.png")
    ImageResource feColorMatrix();
    @Source("feComponentTransfer.png")
    ImageResource feComponentTransfer();
    @Source("feComposite.png")
    ImageResource feComposite();
    @Source("feConvolveMatrix.png")
    ImageResource feConvolveMatrix();
    @Source("feDiffuseLighting.png")
    ImageResource feDiffuseLighting();
    @Source("feDisplacementMap.png")
    ImageResource feDisplacementMap();
    @Source("feDistantLight.png")
    ImageResource feDistantLight();
    @Source("feFlood.png")
    ImageResource feFlood();
    @Source("feFuncA.png")
    ImageResource feFuncA();
    @Source("feFuncB.png")
    ImageResource feFuncB();
    @Source("feFuncG.png")
    ImageResource feFuncG();
    @Source("feFuncR.png")
    ImageResource feFuncR();
    @Source("feGaussianBlur.png")
    ImageResource feGaussianBlur();
    @Source("feImage.png")
    ImageResource feImage();
    @Source("feMergeNode.png")
    ImageResource feMergeNode();
    @Source("feMerge.png")
    ImageResource feMerge();
    @Source("feMorphology.png")
    ImageResource feMorphology();
    @Source("feOffset.png")
    ImageResource feOffset();
    @Source("fePointLight.png")
    ImageResource fePointLight();
    @Source("feSpecularLight.png")
    ImageResource feSpecularLight();
    @Source("feSpotLight.png")
    ImageResource feSpotLight();
    @Source("feTile.png")
    ImageResource feTile();
    @Source("feTurbulence.png")
    ImageResource feTurbulence();
    @Source("filter.png")
    ImageResource filter();
    @Source("font-face-format.png")
    ImageResource fontFaceFormat();
    @Source("font-face-name.png")
    ImageResource fontFaceName();
    @Source("font-face.png")
    ImageResource fontFace();
    @Source("font-face-src.png")
    ImageResource fontFaceSrc();
    @Source("font-face-uri.png")
    ImageResource fontFaceUri();
    @Source("font.png")
    ImageResource font();
    @Source("foreignObject.png")
    ImageResource foreignObject();
    @Source("glyph.png")
    ImageResource glyph();
    @Source("glyphRef.png")
    ImageResource glyphRef();
    @Source("g.png")
    ImageResource g();
    @Source("hkern.png")
    ImageResource hkern();
    @Source("image.png")
    ImageResource image();
    @Source("linearGradient.png")
    ImageResource linearGradient();
    @Source("line.png")
    ImageResource line();
    @Source("marker.png")
    ImageResource marker();
    @Source("mask.png")
    ImageResource mask();
    @Source("metadata.png")
    ImageResource metadata();
    @Source("missing-glyph.png")
    ImageResource missingGlyph();
    @Source("mpath.png")
    ImageResource mpath();
    @Source("path.png")
    ImageResource path();
    @Source("pattern.png")
    ImageResource pattern();
    @Source("polygon.png")
    ImageResource polygon();
    @Source("polyline.png")
    ImageResource polyline();
    @Source("radialGradient.png")
    ImageResource radialGradient();
    @Source("rect.png")
    ImageResource rect();
    @Source("script.png")
    ImageResource script();
    @Source("set.png")
    ImageResource set();
    @Source("stop.png")
    ImageResource stop();
    @Source("style.png")
    ImageResource style();
    @Source("svg.png")
    ImageResource svg();
    @Source("switch.png")
    ImageResource switch_();
    @Source("symbol.png")
    ImageResource symbol();
    @Source("textPath.png")
    ImageResource textPath();
    @Source("text.png")
    ImageResource text();
    @Source("title.png")
    ImageResource title();
    @Source("tref.png")
    ImageResource tref();
    @Source("tspan.png")
    ImageResource tspan();
    @Source("use.png")
    ImageResource use();
    @Source("view.png")
    ImageResource view();
    @Source("viewBox.png")
    ImageResource viewBox();
    @Source("vkern.png")
    ImageResource vkern();

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

}

