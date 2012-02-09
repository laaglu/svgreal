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
package org.vectomatic.svg.edit.client.model.svg;

import java.util.HashMap;
import java.util.Map;

import org.vectomatic.dom.svg.OMSVGPaint;
import org.vectomatic.dom.svg.impl.SVGElement;
import org.vectomatic.dom.svg.utils.OMSVGParser;
import org.vectomatic.dom.svg.utils.SVGConstants;
import org.vectomatic.svg.edit.client.AppBundle;
import org.vectomatic.svg.edit.client.command.GenericEditCommandFactory.GenericEditFactoryInstantiator;
import org.vectomatic.svg.edit.client.engine.SVGModel;
import org.vectomatic.svg.edit.client.gxt.binding.FormPanelUtils;
import org.vectomatic.svg.edit.client.inspector.GenericSectionFactory;
import org.vectomatic.svg.edit.client.model.CssMetadata;
import org.vectomatic.svg.edit.client.model.IConverter;
import org.vectomatic.svg.edit.client.model.MetadataBase;
import org.vectomatic.svg.edit.client.model.ModelCategory;
import org.vectomatic.svg.edit.client.model.ModelConstants;

import com.google.gwt.user.client.ui.AbstractImagePrototype;

/**
 * Base class for SVG element models which have CSS attributes
 * @author laaglu
 */
public abstract class SVGStyledElementModel extends SVGNamedElementModel {
	private static Map<String, MetadataBase<?, SVGElement>> nameToCategory;
	private static ModelCategory<SVGElement> strokeFillCategory;

	public SVGStyledElementModel(SVGModel owner, SVGElement element, SVGElement twin) {
		super(owner, element, twin);
	}
	
	protected static ModelCategory<SVGElement> createStrokeFillCategory(String[] cssProperties) {
		ModelConstants constants = ModelConstants.INSTANCE;
		if (nameToCategory == null) {
			nameToCategory = new HashMap<String, MetadataBase<?, SVGElement>>();
			AppBundle bundle = AppBundle.INSTANCE;
			GenericEditFactoryInstantiator instantiator = new GenericEditFactoryInstantiator(constants.editCssPropertyFactory(), constants.editCssPropertyFactoryDesc());
			nameToCategory.put(SVGConstants.CSS_FILL_PROPERTY, new CssMetadata<OMSVGPaint>(
					SVGConstants.CSS_FILL_PROPERTY,
					constants.fill(),
					FormPanelUtils.SVGPAINT_FIELD_FACTORY,
					IConverter.STRING_SVGPAINT_CONVERTER,
					OMSVGParser.parsePaint(SVGConstants.CSS_BLACK_VALUE),
					instantiator));
			nameToCategory.put(SVGConstants.CSS_FILL_OPACITY_PROPERTY, new CssMetadata<Float>(
					SVGConstants.CSS_FILL_OPACITY_PROPERTY,
					constants.fillOpacity(),
					new FormPanelUtils.HSliderFieldFactory(0f, 1f),
					IConverter.STRING_FLOAT_CONVERTER,
					1f,
					instantiator));
			nameToCategory.put(SVGConstants.CSS_FILL_RULE_PROPERTY, new CssMetadata<String>(
					SVGConstants.CSS_FILL_RULE_PROPERTY,
					constants.fillRule(),
					new FormPanelUtils.ToggleGroupFieldFactory(
						new String[] {
							SVGConstants.CSS_NONZERO_VALUE,
							SVGConstants.CSS_EVENODD_VALUE
						}, 
						new String[] {
							constants.fillRuleNonZero(),	
							constants.fillRuleEvenOdd()
						}, 
						new AbstractImagePrototype[] {
							AbstractImagePrototype.create(bundle.fillRuleNonZero()),
							AbstractImagePrototype.create(bundle.fillRuleEvenOdd())
						}),
					IConverter.NOP_CONVERTER,
					SVGConstants.CSS_NONZERO_VALUE,
					instantiator));
			nameToCategory.put(SVGConstants.CSS_STROKE_PROPERTY, new CssMetadata<OMSVGPaint>(
					SVGConstants.CSS_STROKE_PROPERTY,
					constants.stroke(),
					FormPanelUtils.SVGPAINT_FIELD_FACTORY,
					IConverter.STRING_SVGPAINT_CONVERTER,
					OMSVGParser.parsePaint(SVGConstants.CSS_NONE_VALUE),
					instantiator));
			nameToCategory.put(SVGConstants.CSS_STROKE_OPACITY_PROPERTY, new CssMetadata<Float>(
					SVGConstants.CSS_STROKE_OPACITY_PROPERTY,
					constants.strokeOpacity(),
					new FormPanelUtils.HSliderFieldFactory(0f, 1f),
					IConverter.STRING_FLOAT_CONVERTER,
					1f,
					instantiator));
			nameToCategory.put(SVGConstants.CSS_STROKE_WIDTH_PROPERTY, new CssMetadata<Double>(
					SVGConstants.CSS_STROKE_WIDTH_PROPERTY,
					constants.strokeWidth(),
					FormPanelUtils.SPINNER_FIELD_FACTORY,
					IConverter.STRING_DOUBLE_CONVERTER,
					1d,
					instantiator));
			nameToCategory.put(SVGConstants.CSS_STROKE_LINECAP_PROPERTY, new CssMetadata<String>(
					SVGConstants.CSS_STROKE_LINECAP_PROPERTY,
					constants.strokeLineCap(),
					new FormPanelUtils.ToggleGroupFieldFactory(
						new String[] {
							SVGConstants.CSS_BUTT_VALUE,
							SVGConstants.CSS_ROUND_VALUE,
							SVGConstants.CSS_SQUARE_VALUE
						}, 
						new String[] {
							constants.strokeLineCapButt(),	
							constants.strokeLineCapRound(),	
							constants.strokeLineCapSquare()
						}, 
						new AbstractImagePrototype[] {
							AbstractImagePrototype.create(bundle.lineCapButt()),
							AbstractImagePrototype.create(bundle.lineCapRound()),
							AbstractImagePrototype.create(bundle.lineCapSquare())
						}),
					IConverter.NOP_CONVERTER,
					SVGConstants.CSS_BUTT_VALUE,
					instantiator));
			nameToCategory.put(SVGConstants.CSS_STROKE_LINEJOIN_PROPERTY, new CssMetadata<String>(
					SVGConstants.CSS_STROKE_LINEJOIN_PROPERTY,
					constants.strokeLineJoin(),
					new FormPanelUtils.ToggleGroupFieldFactory(
						new String[] {
								SVGConstants.CSS_MITER_VALUE,	
								SVGConstants.CSS_ROUND_VALUE,	
								SVGConstants.CSS_BEVEL_VALUE	
						}, 
						new String[] {
							constants.strokeLineJoinMiter(),	
							constants.strokeLineJoinRound(),	
							constants.strokeLineJoinBevel()
						}, 
						new AbstractImagePrototype[] {
							AbstractImagePrototype.create(bundle.lineJoinMiter()),
							AbstractImagePrototype.create(bundle.lineJoinRound()),
							AbstractImagePrototype.create(bundle.lineJoinBevel())
						}),
					IConverter.NOP_CONVERTER,
					SVGConstants.CSS_MITER_VALUE,
					instantiator));
			nameToCategory.put(SVGConstants.CSS_STROKE_MITERLIMIT_PROPERTY, new CssMetadata<Double>(
					SVGConstants.CSS_STROKE_MITERLIMIT_PROPERTY,
					constants.strokeMiterLimit(),
					FormPanelUtils.SPINNER_FIELD_FACTORY,
					IConverter.STRING_DOUBLE_CONVERTER,
					4d,
					instantiator));
			nameToCategory.put(SVGConstants.CSS_STROKE_DASHARRAY_PROPERTY, new CssMetadata<DashArray>(
					SVGConstants.CSS_STROKE_DASHARRAY_PROPERTY,
					constants.strokeDashArray(),
					FormPanelUtils.DASHARRAY_FIELD_FACTORY,
					IConverter.STRING_DASHARRAY_CONVERTER,
					null,
					instantiator));
			nameToCategory.put(SVGConstants.CSS_STROKE_DASHOFFSET_PROPERTY, new CssMetadata<Double>(
					SVGConstants.CSS_STROKE_DASHOFFSET_PROPERTY,
					constants.strokeDashOffset(),
					FormPanelUtils.SPINNER_FIELD_FACTORY,
					IConverter.STRING_DOUBLE_CONVERTER,
					0d,
					instantiator));
			
		}
		strokeFillCategory = new ModelCategory<SVGElement>(
				ModelCategory.STROKEFILL, 
				constants.strokeFill(), 
				GenericSectionFactory.INSTANCE);
		for (String cssProperty : cssProperties) {
			MetadataBase<?, SVGElement> metadata = nameToCategory.get(cssProperty);
			if (metadata != null) {
				strokeFillCategory.addMetadata(metadata);
			}
		}
		return strokeFillCategory;
	}
}
