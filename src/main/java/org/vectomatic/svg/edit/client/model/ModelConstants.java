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
package org.vectomatic.svg.edit.client.model;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Constants;

/**
 * Class to contain text constants used in the application model.
 * @author laaglu
 */
public interface ModelConstants extends Constants {
	public static final ModelConstants INSTANCE = GWT.create(ModelConstants.class);
	public String circle();
	public String ellipse();
	public String line();
	public String rectangle();
	public String polyline();
	public String polygon();
	public String path();
	public String viewBox();
	public String image();
	public String use();
	public String text();
	
	/*======================================
	 = Geometric category property names
	 =======================================*/

	public String circleCx();
	public String circleCy();
	public String circleR();
	
	public String rectX();
	public String rectY();
	public String rectWidth();
	public String rectHeight();
	public String rectRx();
	public String rectRy();
	
	public String ellipseCx();
	public String ellipseCy();
	public String ellipseRx();
	public String ellipseRy();
	
	public String lineX1();
	public String lineX2();
	public String lineY1();
	public String lineY2();

	public String polygonVertices();
	
	public String pathD();

	public String imageHref();

	/*======================================
	 = Global category property names
	 =======================================*/

	public String id();
	
	public String title();
	public String desc();
	
	public String visibility();
	
	/*======================================
	 = Stroke and fill category property names
	 =======================================*/

	public String fill();
	public String fillRule();
	public String fillRuleNonZero();
	public String fillRuleEvenOdd();
	public String fillOpacity();
	public String stroke();
	public String strokeWidth();
	public String strokeLineCap();
	public String strokeLineCapButt();
	public String strokeLineCapRound();
	public String strokeLineCapSquare();
	public String strokeLineJoin();
	public String strokeLineJoinMiter();
	public String strokeLineJoinRound();
	public String strokeLineJoinBevel();
	public String strokeMiterLimit();
	public String strokeOpacity();
	public String strokeDashArray();
	public String strokeDashOffset();

	/*======================================
	 = Metamodel category names
	 =======================================*/
	public String transform();
	public String geometry();
	public String global();
	public String display();
	public String strokeFill();
	
	/*======================================
	 = Command factories
	 =======================================*/

	public String editGeometryCmdFactory();
	public String editGeometryCmdFactoryDesc();
	public String editGeometryCmdFactory1();
	public String editGeometryCmdFactory2();
	
	public String transformCmdFactory();
	public String transformCmdFactoryDesc();
	public String transformCmdFactory1();
	public String transformCmdFactory2();
	
	public String addLineCmdFactory();
	public String addLineCmdFactoryDesc();
	public String addLineCmdFactory1();
	public String addLineCmdFactory2();
	
	public String addCircleCmdFactory();
	public String addCircleCmdFactoryDesc();
	public String addCircleCmdFactory1();
	public String addCircleCmdFactory2();
	
	public String addEllipseCmdFactory();
	public String addEllipseCmdFactoryDesc();
	public String addEllipseCmdFactory1();
	public String addEllipseCmdFactory2();

	public String addRectCmdFactory();
	public String addRectCmdFactoryDesc();
	public String addRectCmdFactory1();
	public String addRectCmdFactory2();
	
	public String addPolylineCmdFactory();
	public String addPolylineCmdFactoryDesc();
	public String addPolylineCmdFactory1();

	public String addPolygonCmdFactory();
	public String addPolygonCmdFactoryDesc();
	public String addPolygonCmdFactory1();

	public String addPathCmdFactory();
	public String addPathCmdFactoryDesc();
	public String addPathCmdFactoryMove();
	public String addPathCmdFactoryLineFirst();
	public String addPathCmdFactoryLineP2();
	public String addPathCmdFactoryQuadraticCp1First();
	public String addPathCmdFactoryQuadraticCp1a();
	public String addPathCmdFactoryQuadraticCp1b();
	public String addPathCmdFactoryQuadraticP2();
	public String addPathCmdFactoryCubicCp1First();
	public String addPathCmdFactoryCubicCp1a();
	public String addPathCmdFactoryCubicCp1b();
	public String addPathCmdFactoryCubicCp2a();
	public String addPathCmdFactoryCubicCp2b();

	public String addGroupCmdFactory();
	public String addGroupCmdFactoryDesc();

	public String removeElementsCmdFactory();
	public String removeElementsCmdFactoryDesc();
	public String removeElementsCmdFactory1();

	public String showPropertiesCmdFactory();
	public String showPropertiesCmdFactoryDesc();
	
	public String renameElementCmdFactory();
	public String renameElementCmdFactoryDesc();
	public String renameElementCmdFactory1();
	public String renameElementCmdFactory2();
	
	public String genericEditCmdFactory();
	public String genericEditCmdFactoryDesc();

	public String editCssPropertyFactory();
	public String editCssPropertyFactoryDesc();
	
	public String clearCssPropertyFactory();
	public String clearCssPropertyFactoryDesc();

	public String dndCmdFactory();
	public String dndCmdFactoryDesc();
	public String dndCmdFactory1();

	/*======================================
	 = Command messages
	 =======================================*/

	public String editGeometryCmd();
	public String transformCmd();
	public String addCmd();
	public String removeCmd();
	public String renameCmd();
	public String editCmd();
	public String dndReorderCmdIn();
	public String dndReorderCmdBefore();
	public String dndReorderCmdAfter();
	public String dndMoveCmdSrc();
	public String dndMoveCmdDestIn();
	public String dndMoveCmdDestBefore();
	public String dndMoveCmdDestAfter();
	public String dndCloneCmdIn();
	public String dndCloneCmdBefore();
	public String dndCloneCmdAfter();
	public String dndCopyCmdIn();
	public String dndCopyCmdBefore();
	public String dndCopyCmdAfter();

	/*======================================
	 = Windows
	 =======================================*/
	
	public String inspectorWindow();
	public String inspectorNoSelection();
	public String inspectorMultiSelection();
	public String commandWindow();
	
	public String removePointsButton();
	public String insertPointButton();
	public String addPointButton();
	public String x();
	public String y();

	public String removeSegmentsButton();
	public String insertSegmentButton();
	public String addSegmentButton();
	public String closed();
	
	/*======================================
	 = Spline editor
	 =======================================*/
	
	public String segX();
	public String segY();
	public String segTypeDesc();
	public String segMoveTo();
	public String segLineTo();
	public String segQuadraticTo();
	public String segCubicTo();
	public String segX1();
	public String segY1();
	public String segX2();
	public String segY2();
	
	/*======================================
	 = Drag and drop
	 =======================================*/

	public String dndCopy();
	public String dndMove();
	public String dndLink();
	public String copyOf();

	/*======================================
	 = Image href inspector
	 =======================================*/
	
	public String externalRadio();
	public String embeddedRadio();

	public String url();
	public String urlTooltip();

	public String dropPanelText();
	public String openLocalImageButton();

	public String originalSizeLabel();
	public String resetHrefButton();
	public String resetHrefTooltip();

	public String imageLoadError();
	public String noImage();
}
