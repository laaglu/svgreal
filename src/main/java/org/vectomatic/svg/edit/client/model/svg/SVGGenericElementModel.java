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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.vectomatic.dom.svg.impl.SVGElement;
import org.vectomatic.dom.svg.utils.SVGConstants;
import org.vectomatic.svg.edit.client.AppBundle;
import org.vectomatic.svg.edit.client.command.EditTransformCommandFactory;
import org.vectomatic.svg.edit.client.command.IFactoryInstantiator;
import org.vectomatic.svg.edit.client.engine.SVGModel;
import org.vectomatic.svg.edit.client.model.MetaModel;
import org.vectomatic.svg.edit.client.model.ModelCategory;

import com.google.gwt.user.client.ui.AbstractImagePrototype;

/**
 * Generic model class for svg elements for which a dedicated model has
 * not yet been written.
 * @author laaglu
 */
public class SVGGenericElementModel extends SVGNamedElementModel {
	private static Map<String, AbstractImagePrototype> elementNameToIcon;
	static {
		elementNameToIcon = new HashMap<String, AbstractImagePrototype>();
		elementNameToIcon.put(SVGConstants.SVG_ALT_GLYPH_DEF_TAG, AbstractImagePrototype.create(AppBundle.INSTANCE.altGlyphDef()));
		elementNameToIcon.put(SVGConstants.SVG_ALT_GLYPH_ITEM_TAG, AbstractImagePrototype.create(AppBundle.INSTANCE.altGlyphItem()));
		elementNameToIcon.put(SVGConstants.SVG_ALT_GLYPH_TAG, AbstractImagePrototype.create(AppBundle.INSTANCE.altGlyph()));
		elementNameToIcon.put(SVGConstants.SVG_ANIMATE_COLOR_TAG, AbstractImagePrototype.create(AppBundle.INSTANCE.animateColor()));
		elementNameToIcon.put(SVGConstants.SVG_ANIMATE_MOTION_TAG, AbstractImagePrototype.create(AppBundle.INSTANCE.animateMotion()));
		elementNameToIcon.put(SVGConstants.SVG_ANIMATE_TAG, AbstractImagePrototype.create(AppBundle.INSTANCE.animate()));
		elementNameToIcon.put(SVGConstants.SVG_ANIMATE_TRANSFORM_TAG, AbstractImagePrototype.create(AppBundle.INSTANCE.animateTransform()));
		elementNameToIcon.put(SVGConstants.SVG_CIRCLE_TAG, AbstractImagePrototype.create(AppBundle.INSTANCE.circle()));
		elementNameToIcon.put(SVGConstants.SVG_CLIP_PATH_TAG, AbstractImagePrototype.create(AppBundle.INSTANCE.clipPath()));
		elementNameToIcon.put(SVGConstants.SVG_COLOR_PROFILE_TAG, AbstractImagePrototype.create(AppBundle.INSTANCE.colorProfile()));
		elementNameToIcon.put(SVGConstants.SVG_CURSOR_TAG, AbstractImagePrototype.create(AppBundle.INSTANCE.cursor()));
		elementNameToIcon.put(SVGConstants.SVG_DEFS_TAG, AbstractImagePrototype.create(AppBundle.INSTANCE.defs()));
		elementNameToIcon.put(SVGConstants.SVG_DESC_TAG, AbstractImagePrototype.create(AppBundle.INSTANCE.desc()));
		elementNameToIcon.put(SVGConstants.SVG_ELLIPSE_TAG, AbstractImagePrototype.create(AppBundle.INSTANCE.ellipse()));
		elementNameToIcon.put(SVGConstants.SVG_FE_BLEND_TAG, AbstractImagePrototype.create(AppBundle.INSTANCE.feBlend()));
		elementNameToIcon.put(SVGConstants.SVG_FE_COLOR_MATRIX_TAG, AbstractImagePrototype.create(AppBundle.INSTANCE.feColorMatrix()));
		elementNameToIcon.put(SVGConstants.SVG_FE_COMPONENT_TRANSFER_TAG, AbstractImagePrototype.create(AppBundle.INSTANCE.feComponentTransfer()));
		elementNameToIcon.put(SVGConstants.SVG_FE_COMPOSITE_TAG, AbstractImagePrototype.create(AppBundle.INSTANCE.feComposite()));
		elementNameToIcon.put(SVGConstants.SVG_FE_CONVOLVE_MATRIX_TAG, AbstractImagePrototype.create(AppBundle.INSTANCE.feConvolveMatrix()));
		elementNameToIcon.put(SVGConstants.SVG_FE_DIFFUSE_LIGHTING_TAG, AbstractImagePrototype.create(AppBundle.INSTANCE.feDiffuseLighting()));
		elementNameToIcon.put(SVGConstants.SVG_FE_DISPLACEMENT_MAP_TAG, AbstractImagePrototype.create(AppBundle.INSTANCE.feDisplacementMap()));
		elementNameToIcon.put(SVGConstants.SVG_FE_DISTANT_LIGHT_TAG, AbstractImagePrototype.create(AppBundle.INSTANCE.feDistantLight()));
		elementNameToIcon.put(SVGConstants.SVG_FE_FLOOD_TAG, AbstractImagePrototype.create(AppBundle.INSTANCE.feFlood()));
		elementNameToIcon.put(SVGConstants.SVG_FE_FUNC_A_TAG, AbstractImagePrototype.create(AppBundle.INSTANCE.feFuncA()));
		elementNameToIcon.put(SVGConstants.SVG_FE_FUNC_B_TAG, AbstractImagePrototype.create(AppBundle.INSTANCE.feFuncB()));
		elementNameToIcon.put(SVGConstants.SVG_FE_FUNC_G_TAG, AbstractImagePrototype.create(AppBundle.INSTANCE.feFuncG()));
		elementNameToIcon.put(SVGConstants.SVG_FE_FUNC_R_TAG, AbstractImagePrototype.create(AppBundle.INSTANCE.feFuncR()));
		elementNameToIcon.put(SVGConstants.SVG_FE_GAUSSIAN_BLUR_TAG, AbstractImagePrototype.create(AppBundle.INSTANCE.feGaussianBlur()));
		elementNameToIcon.put(SVGConstants.SVG_FE_MERGE_NODE_TAG, AbstractImagePrototype.create(AppBundle.INSTANCE.feMergeNode()));
		elementNameToIcon.put(SVGConstants.SVG_FE_MERGE_TAG, AbstractImagePrototype.create(AppBundle.INSTANCE.feMerge()));
		elementNameToIcon.put(SVGConstants.SVG_FE_MORPHOLOGY_TAG, AbstractImagePrototype.create(AppBundle.INSTANCE.feMorphology()));
		elementNameToIcon.put(SVGConstants.SVG_FE_OFFSET_TAG, AbstractImagePrototype.create(AppBundle.INSTANCE.feOffset()));
		elementNameToIcon.put(SVGConstants.SVG_FE_POINT_LIGHT_TAG, AbstractImagePrototype.create(AppBundle.INSTANCE.fePointLight()));
		elementNameToIcon.put(SVGConstants.SVG_FE_SPECULAR_LIGHTING_TAG, AbstractImagePrototype.create(AppBundle.INSTANCE.feSpecularLight()));
		elementNameToIcon.put(SVGConstants.SVG_FE_SPOT_LIGHT_TAG, AbstractImagePrototype.create(AppBundle.INSTANCE.feSpotLight()));
		elementNameToIcon.put(SVGConstants.SVG_FE_TILE_TAG, AbstractImagePrototype.create(AppBundle.INSTANCE.feTile()));
		elementNameToIcon.put(SVGConstants.SVG_FE_TURBULENCE_TAG, AbstractImagePrototype.create(AppBundle.INSTANCE.feTurbulence()));
		elementNameToIcon.put(SVGConstants.SVG_FILTER_TAG, AbstractImagePrototype.create(AppBundle.INSTANCE.filter()));
		elementNameToIcon.put(SVGConstants.SVG_FONT_FACE_FORMAT_TAG, AbstractImagePrototype.create(AppBundle.INSTANCE.fontFaceFormat()));
		elementNameToIcon.put(SVGConstants.SVG_FONT_FACE_NAME_TAG, AbstractImagePrototype.create(AppBundle.INSTANCE.fontFaceName()));
		elementNameToIcon.put(SVGConstants.SVG_FONT_FACE_TAG, AbstractImagePrototype.create(AppBundle.INSTANCE.fontFace()));
		elementNameToIcon.put(SVGConstants.SVG_FONT_FACE_SRC_TAG, AbstractImagePrototype.create(AppBundle.INSTANCE.fontFaceSrc()));
		elementNameToIcon.put(SVGConstants.SVG_FONT_FACE_URI_TAG, AbstractImagePrototype.create(AppBundle.INSTANCE.fontFaceUri()));
		elementNameToIcon.put(SVGConstants.SVG_FONT_TAG, AbstractImagePrototype.create(AppBundle.INSTANCE.font()));
		elementNameToIcon.put(SVGConstants.SVG_FOREIGN_OBJECT_TAG, AbstractImagePrototype.create(AppBundle.INSTANCE.foreignObject()));
		elementNameToIcon.put(SVGConstants.SVG_GLYPH_TAG, AbstractImagePrototype.create(AppBundle.INSTANCE.glyph()));
		elementNameToIcon.put(SVGConstants.SVG_GLYPH_REF_TAG, AbstractImagePrototype.create(AppBundle.INSTANCE.glyphRef()));
		elementNameToIcon.put(SVGConstants.SVG_G_TAG, AbstractImagePrototype.create(AppBundle.INSTANCE.g()));
		elementNameToIcon.put(SVGConstants.SVG_H_KERN_TAG, AbstractImagePrototype.create(AppBundle.INSTANCE.hkern()));
		elementNameToIcon.put(SVGConstants.SVG_IMAGE_TAG, AbstractImagePrototype.create(AppBundle.INSTANCE.image()));
		elementNameToIcon.put(SVGConstants.SVG_LINEAR_GRADIENT_TAG, AbstractImagePrototype.create(AppBundle.INSTANCE.linearGradient()));
		elementNameToIcon.put(SVGConstants.SVG_LINE_TAG, AbstractImagePrototype.create(AppBundle.INSTANCE.line()));
		elementNameToIcon.put(SVGConstants.SVG_MARKER_TAG, AbstractImagePrototype.create(AppBundle.INSTANCE.marker()));
		elementNameToIcon.put(SVGConstants.SVG_MASK_TAG, AbstractImagePrototype.create(AppBundle.INSTANCE.mask()));
		elementNameToIcon.put(SVGConstants.SVG_METADATA_TAG, AbstractImagePrototype.create(AppBundle.INSTANCE.metadata()));
		elementNameToIcon.put(SVGConstants.SVG_MISSING_GLYPH_TAG, AbstractImagePrototype.create(AppBundle.INSTANCE.missingGlyph()));
		elementNameToIcon.put(SVGConstants.SVG_M_PATH_TAG, AbstractImagePrototype.create(AppBundle.INSTANCE.mpath()));
		elementNameToIcon.put(SVGConstants.SVG_PATH_TAG, AbstractImagePrototype.create(AppBundle.INSTANCE.path()));
		elementNameToIcon.put(SVGConstants.SVG_PATTERN_TAG, AbstractImagePrototype.create(AppBundle.INSTANCE.pattern()));
		elementNameToIcon.put(SVGConstants.SVG_POLYGON_TAG, AbstractImagePrototype.create(AppBundle.INSTANCE.polygon()));
		elementNameToIcon.put(SVGConstants.SVG_POLYLINE_TAG, AbstractImagePrototype.create(AppBundle.INSTANCE.polyline()));
		elementNameToIcon.put(SVGConstants.SVG_RADIAL_GRADIENT_TAG, AbstractImagePrototype.create(AppBundle.INSTANCE.radialGradient()));
		elementNameToIcon.put(SVGConstants.SVG_RECT_TAG, AbstractImagePrototype.create(AppBundle.INSTANCE.rect()));
		elementNameToIcon.put(SVGConstants.SVG_SCRIPT_TAG, AbstractImagePrototype.create(AppBundle.INSTANCE.script()));
		elementNameToIcon.put(SVGConstants.SVG_SET_TAG, AbstractImagePrototype.create(AppBundle.INSTANCE.set()));
		elementNameToIcon.put(SVGConstants.SVG_STOP_TAG, AbstractImagePrototype.create(AppBundle.INSTANCE.stop()));
		elementNameToIcon.put(SVGConstants.SVG_STYLE_TAG, AbstractImagePrototype.create(AppBundle.INSTANCE.style()));
		elementNameToIcon.put(SVGConstants.SVG_SVG_TAG, AbstractImagePrototype.create(AppBundle.INSTANCE.svg()));
		elementNameToIcon.put(SVGConstants.SVG_SWITCH_TAG, AbstractImagePrototype.create(AppBundle.INSTANCE.switch_()));
		elementNameToIcon.put(SVGConstants.SVG_SYMBOL_TAG, AbstractImagePrototype.create(AppBundle.INSTANCE.symbol()));
		elementNameToIcon.put(SVGConstants.SVG_TEXT_PATH_TAG, AbstractImagePrototype.create(AppBundle.INSTANCE.textPath()));
		elementNameToIcon.put(SVGConstants.SVG_TEXT_TAG, AbstractImagePrototype.create(AppBundle.INSTANCE.text()));
		elementNameToIcon.put(SVGConstants.SVG_TITLE_TAG, AbstractImagePrototype.create(AppBundle.INSTANCE.title()));
		elementNameToIcon.put(SVGConstants.SVG_T_REF_TAG, AbstractImagePrototype.create(AppBundle.INSTANCE.tref()));
		elementNameToIcon.put(SVGConstants.SVG_T_SPAN_TAG, AbstractImagePrototype.create(AppBundle.INSTANCE.tspan()));
		elementNameToIcon.put(SVGConstants.SVG_USE_TAG, AbstractImagePrototype.create(AppBundle.INSTANCE.use()));
		elementNameToIcon.put(SVGConstants.SVG_VIEW_TAG, AbstractImagePrototype.create(AppBundle.INSTANCE.view()));
		elementNameToIcon.put(SVGConstants.SVG_V_KERN_TAG, AbstractImagePrototype.create(AppBundle.INSTANCE.vkern()));
	}
	private MetaModel<SVGElement> metaModel;
	public SVGGenericElementModel(SVGModel owner, SVGElement element, SVGElement twin) {
		super(owner, element, twin);
	}

	@Override
	public MetaModel<SVGElement> getMetaModel() {
		if (metaModel == null) {
			IFactoryInstantiator<?>[][] contextMenuFactories = new IFactoryInstantiator<?>[][] {
				{
					EditTransformCommandFactory.INSTANTIATOR
				}
			};
			metaModel = new MetaModel<SVGElement>();
			List<ModelCategory<SVGElement>> categories = new ArrayList<ModelCategory<SVGElement>>();
			categories.add(SVGNamedElementModel.getNamingCategory());
			categories.add(SVGElementModel.getTransformCategory());
			metaModel.init(
				null,
				elementNameToIcon.get(element.getTagName()), 
				categories,
				contextMenuFactories);

		}
		return metaModel;
	}

}
