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
package org.vectomatic.svg.edit.client.model.svg;

import java.util.HashMap;
import java.util.Map;

import org.vectomatic.dom.svg.OMSVGLength;
import org.vectomatic.dom.svg.OMSVGPaint;
import org.vectomatic.dom.svg.OMSVGSVGElement;
import org.vectomatic.dom.svg.impl.SVGElement;
import org.vectomatic.dom.svg.utils.OMSVGParser;
import org.vectomatic.dom.svg.utils.SVGConstants;
import org.vectomatic.svg.edit.client.AppBundle;
import org.vectomatic.svg.edit.client.command.GenericEditCommandFactory;
import org.vectomatic.svg.edit.client.engine.SVGModel;
import org.vectomatic.svg.edit.client.gxt.binding.FormPanelUtils;
import org.vectomatic.svg.edit.client.gxt.binding.FormPanelUtils.SvgLengthFieldFactory;
import org.vectomatic.svg.edit.client.inspector.GenericSectionFactory;
import org.vectomatic.svg.edit.client.model.CssMetadata;
import org.vectomatic.svg.edit.client.model.IConverter;
import org.vectomatic.svg.edit.client.model.IValidator;
import org.vectomatic.svg.edit.client.model.MetadataBase;
import org.vectomatic.svg.edit.client.model.ModelCategory;
import org.vectomatic.svg.edit.client.model.ModelConstants;
import org.vectomatic.svg.edit.client.model.ValidationConstants;
import org.vectomatic.svg.edit.client.model.ValidationError;
import org.vectomatic.svg.edit.client.model.ValidationError.Severity;

import com.extjs.gxt.ui.client.data.ModelData;
import com.google.gwt.resources.client.ImageResource;

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
			nameToCategory.put(SVGConstants.CSS_FILL_PROPERTY, new CssMetadata<OMSVGPaint>(
					SVGConstants.CSS_FILL_PROPERTY,
					constants.fill(),
					FormPanelUtils.SVGPAINT_FIELD_FACTORY,
					IConverter.STRING_SVGPAINT_CONVERTER,
					OMSVGParser.parsePaint(SVGConstants.CSS_BLACK_VALUE),
					GenericEditCommandFactory.INSTANTIATOR,
					null));
			
			IValidator<Float, SVGElement> opacityValidator = new IValidator<Float, SVGElement>() {
				final ValidationError negativeOpacity = new ValidationError(Severity.ERROR, ValidationConstants.INSTANCE.outOfRangeOpacity());
				@Override
				public ValidationError validate(SVGElement model, Float value) {
					return (value < 0 || value > 1) ? negativeOpacity : null;
				}
			};
			nameToCategory.put(SVGConstants.CSS_FILL_OPACITY_PROPERTY, new CssMetadata<Float>(
					SVGConstants.CSS_FILL_OPACITY_PROPERTY,
					constants.fillOpacity(),
					new FormPanelUtils.HSliderFieldFactory(0f, 1f),
					IConverter.STRING_FLOAT_CONVERTER,
					1f,
					GenericEditCommandFactory.INSTANTIATOR,
					opacityValidator));
			
			ComboStore fillRuleStore = new ComboStore(new String[] {
					SVGConstants.CSS_NONZERO_VALUE,
					SVGConstants.CSS_EVENODD_VALUE,
					null
					}, 
					new String[] {
						constants.fillRuleNonZero(),	
						constants.fillRuleEvenOdd(),
						constants.inherit()
					}, 
					new ImageResource[] {
						bundle.fillRuleNonZero(),
						bundle.fillRuleEvenOdd(),
						bundle.inherit()
					});
			nameToCategory.put(SVGConstants.CSS_FILL_RULE_PROPERTY, new CssMetadata<ModelData>(
					SVGConstants.CSS_FILL_RULE_PROPERTY,
					constants.fillRule(),
					new FormPanelUtils.CssFieldFactory(new FormPanelUtils.ComboBoxFieldFactory(fillRuleStore)),
					fillRuleStore.getConverter(),
					fillRuleStore.findModel(ComboStore.VALUE, null) /* default value, meaning: inherited*/,
					GenericEditCommandFactory.INSTANTIATOR,
					null));
			
			nameToCategory.put(SVGConstants.CSS_STROKE_PROPERTY, new CssMetadata<OMSVGPaint>(
					SVGConstants.CSS_STROKE_PROPERTY,
					constants.stroke(),
					FormPanelUtils.SVGPAINT_FIELD_FACTORY,
					IConverter.STRING_SVGPAINT_CONVERTER,
					OMSVGParser.parsePaint(SVGConstants.CSS_NONE_VALUE),
					GenericEditCommandFactory.INSTANTIATOR,
					null));
			
			nameToCategory.put(SVGConstants.CSS_STROKE_OPACITY_PROPERTY, new CssMetadata<Float>(
					SVGConstants.CSS_STROKE_OPACITY_PROPERTY,
					constants.strokeOpacity(),
					new FormPanelUtils.HSliderFieldFactory(0f, 1f),
					IConverter.STRING_FLOAT_CONVERTER,
					1f,
					GenericEditCommandFactory.INSTANTIATOR,
					opacityValidator));
			
			OMSVGSVGElement svg = new OMSVGSVGElement();
			nameToCategory.put(SVGConstants.CSS_STROKE_WIDTH_PROPERTY, new CssMetadata<SVGLength>(
					SVGConstants.CSS_STROKE_WIDTH_PROPERTY,
					constants.strokeWidth(),
					new SvgLengthFieldFactory(),
					IConverter.STRING_LENGTH_CONVERTER,
					new SVGLength(svg.createSVGLength(OMSVGLength.SVG_LENGTHTYPE_NUMBER, 1)),
					GenericEditCommandFactory.INSTANTIATOR,
					new IValidator<SVGLength, SVGElement>() {
						final ValidationError negativeStrokeWidth = new ValidationError(Severity.ERROR, ValidationConstants.INSTANCE.negativeStrokeWidth());
						@Override
						public ValidationError validate(SVGElement model, SVGLength value) {
							return (value.getValue() < 0) ? negativeStrokeWidth : null;
						}
					}));
			
			ComboStore lineCapStore = new ComboStore(new String[] {
					SVGConstants.CSS_BUTT_VALUE,
					SVGConstants.CSS_ROUND_VALUE,
					SVGConstants.CSS_SQUARE_VALUE,
					null
				}, 
				new String[] {
					constants.strokeLineCapButt(),	
					constants.strokeLineCapRound(),	
					constants.strokeLineCapSquare(),
					constants.inherit()
				}, 
				new ImageResource[] {
					bundle.lineCapButt(),
					bundle.lineCapRound(),
					bundle.lineCapSquare(),
					bundle.inherit()
				});
			nameToCategory.put(SVGConstants.CSS_STROKE_LINECAP_PROPERTY, new CssMetadata<ModelData>(
					SVGConstants.CSS_STROKE_LINECAP_PROPERTY,
					constants.strokeLineCap(),
					new FormPanelUtils.CssFieldFactory(new FormPanelUtils.ComboBoxFieldFactory(lineCapStore)),
					lineCapStore.getConverter(),
					lineCapStore.findModel(ComboStore.VALUE, null) /* default value, meaning: inherited*/,
					GenericEditCommandFactory.INSTANTIATOR,
					null));
			
			ComboStore lineJoinStore = new ComboStore(new String[] {
					SVGConstants.CSS_MITER_VALUE,	
					SVGConstants.CSS_ROUND_VALUE,	
					SVGConstants.CSS_BEVEL_VALUE,
					null
				}, 
				new String[] {
					constants.strokeLineJoinMiter(),	
					constants.strokeLineJoinRound(),	
					constants.strokeLineJoinBevel(),
					constants.inherit()
				}, 
				new ImageResource[] {
					bundle.lineJoinMiter(),
					bundle.lineJoinRound(),
					bundle.lineJoinBevel(),
					bundle.inherit()
				});
			nameToCategory.put(SVGConstants.CSS_STROKE_LINEJOIN_PROPERTY, new CssMetadata<ModelData>(
					SVGConstants.CSS_STROKE_LINEJOIN_PROPERTY,
					constants.strokeLineJoin(),
					new FormPanelUtils.CssFieldFactory(new FormPanelUtils.ComboBoxFieldFactory(lineJoinStore)),
					lineJoinStore.getConverter(),
					lineJoinStore.findModel(ComboStore.VALUE, null) /* default value, meaning: inherited*/,
					GenericEditCommandFactory.INSTANTIATOR,
					null));
			
			nameToCategory.put(SVGConstants.CSS_STROKE_MITERLIMIT_PROPERTY, new CssMetadata<Double>(
					SVGConstants.CSS_STROKE_MITERLIMIT_PROPERTY,
					constants.strokeMiterLimit(),
					FormPanelUtils.SPINNER_FIELD_FACTORY,
					IConverter.STRING_DOUBLE_CONVERTER,
					4d,
					GenericEditCommandFactory.INSTANTIATOR,
					new IValidator<Double, SVGElement>() {
						final ValidationError negativeMiterLimit = new ValidationError(Severity.ERROR, ValidationConstants.INSTANCE.negativeMiterLimit());
						@Override
						public ValidationError validate(SVGElement model, Double value) {
							return (value < 0) ? negativeMiterLimit : null;
						}
					}));
			
			nameToCategory.put(SVGConstants.CSS_STROKE_DASHARRAY_PROPERTY, new CssMetadata<DashArray>(
					SVGConstants.CSS_STROKE_DASHARRAY_PROPERTY,
					constants.strokeDashArray(),
					FormPanelUtils.DASHARRAY_FIELD_FACTORY,
					IConverter.STRING_DASHARRAY_CONVERTER,
					null,
					GenericEditCommandFactory.INSTANTIATOR,
					null));
			
			nameToCategory.put(SVGConstants.CSS_STROKE_DASHOFFSET_PROPERTY, new CssMetadata<SVGLength>(
					SVGConstants.CSS_STROKE_DASHOFFSET_PROPERTY,
					constants.strokeDashOffset(),
					new SvgLengthFieldFactory(),
					IConverter.STRING_LENGTH_CONVERTER,
					new SVGLength(svg.createSVGLength(OMSVGLength.SVG_LENGTHTYPE_NUMBER, 0)),
					GenericEditCommandFactory.INSTANTIATOR,
					new IValidator<SVGLength, SVGElement>() {
						final ValidationError negativeDashOffset = new ValidationError(Severity.ERROR, ValidationConstants.INSTANCE.negativeDashOffset());
						@Override
						public ValidationError validate(SVGElement model, SVGLength value) {
							return (value.getValue() < 0) ? negativeDashOffset : null;
						}
					}));
			
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
