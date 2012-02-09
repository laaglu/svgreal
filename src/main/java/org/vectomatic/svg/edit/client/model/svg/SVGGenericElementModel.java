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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.vectomatic.dom.svg.impl.SVGElement;
import org.vectomatic.dom.svg.utils.SVGConstants;
import org.vectomatic.svg.edit.client.AppBundle;
import org.vectomatic.svg.edit.client.engine.SVGModel;
import org.vectomatic.svg.edit.client.model.MetaModel;
import org.vectomatic.svg.edit.client.model.ModelCategory;

import com.google.gwt.resources.client.ImageResource;

/**
 * Generic model class for svg elements for which a dedicated model has
 * not yet been written.
 * @author laaglu
 */
public class SVGGenericElementModel extends SVGNamedElementModel {
	protected static Map<String, ImageResource> elementNameToIcon;
	static {
		elementNameToIcon = new HashMap<String, ImageResource>();
		elementNameToIcon.put(SVGConstants.SVG_ALT_GLYPH_DEF_TAG, AppBundle.INSTANCE.altGlyphDef());
		elementNameToIcon.put(SVGConstants.SVG_ALT_GLYPH_ITEM_TAG, AppBundle.INSTANCE.altGlyphItem());
		elementNameToIcon.put(SVGConstants.SVG_ALT_GLYPH_TAG, AppBundle.INSTANCE.altGlyph());
		elementNameToIcon.put(SVGConstants.SVG_ANIMATE_COLOR_TAG, AppBundle.INSTANCE.animateColor());
		elementNameToIcon.put(SVGConstants.SVG_ANIMATE_MOTION_TAG, AppBundle.INSTANCE.animateMotion());
		elementNameToIcon.put(SVGConstants.SVG_ANIMATE_TAG, AppBundle.INSTANCE.animate());
		elementNameToIcon.put(SVGConstants.SVG_ANIMATE_TRANSFORM_TAG, AppBundle.INSTANCE.animateTransform());
		elementNameToIcon.put(SVGConstants.SVG_CIRCLE_TAG, AppBundle.INSTANCE.circle());
		elementNameToIcon.put(SVGConstants.SVG_CLIP_PATH_TAG, AppBundle.INSTANCE.clipPath());
		elementNameToIcon.put(SVGConstants.SVG_COLOR_PROFILE_TAG, AppBundle.INSTANCE.colorProfile());
		elementNameToIcon.put(SVGConstants.SVG_CURSOR_TAG, AppBundle.INSTANCE.cursor());
		elementNameToIcon.put(SVGConstants.SVG_DEFS_TAG, AppBundle.INSTANCE.defs());
		elementNameToIcon.put(SVGConstants.SVG_DESC_TAG, AppBundle.INSTANCE.desc());
		elementNameToIcon.put(SVGConstants.SVG_ELLIPSE_TAG, AppBundle.INSTANCE.ellipse());
		elementNameToIcon.put(SVGConstants.SVG_FE_BLEND_TAG, AppBundle.INSTANCE.feBlend());
		elementNameToIcon.put(SVGConstants.SVG_FE_COLOR_MATRIX_TAG, AppBundle.INSTANCE.feColorMatrix());
		elementNameToIcon.put(SVGConstants.SVG_FE_COMPONENT_TRANSFER_TAG, AppBundle.INSTANCE.feComponentTransfer());
		elementNameToIcon.put(SVGConstants.SVG_FE_COMPOSITE_TAG, AppBundle.INSTANCE.feComposite());
		elementNameToIcon.put(SVGConstants.SVG_FE_CONVOLVE_MATRIX_TAG, AppBundle.INSTANCE.feConvolveMatrix());
		elementNameToIcon.put(SVGConstants.SVG_FE_DIFFUSE_LIGHTING_TAG, AppBundle.INSTANCE.feDiffuseLighting());
		elementNameToIcon.put(SVGConstants.SVG_FE_DISPLACEMENT_MAP_TAG, AppBundle.INSTANCE.feDisplacementMap());
		elementNameToIcon.put(SVGConstants.SVG_FE_DISTANT_LIGHT_TAG, AppBundle.INSTANCE.feDistantLight());
		elementNameToIcon.put(SVGConstants.SVG_FE_FLOOD_TAG, AppBundle.INSTANCE.feFlood());
		elementNameToIcon.put(SVGConstants.SVG_FE_FUNC_A_TAG, AppBundle.INSTANCE.feFuncA());
		elementNameToIcon.put(SVGConstants.SVG_FE_FUNC_B_TAG, AppBundle.INSTANCE.feFuncB());
		elementNameToIcon.put(SVGConstants.SVG_FE_FUNC_G_TAG, AppBundle.INSTANCE.feFuncG());
		elementNameToIcon.put(SVGConstants.SVG_FE_FUNC_R_TAG, AppBundle.INSTANCE.feFuncR());
		elementNameToIcon.put(SVGConstants.SVG_FE_GAUSSIAN_BLUR_TAG, AppBundle.INSTANCE.feGaussianBlur());
		elementNameToIcon.put(SVGConstants.SVG_FE_MERGE_NODE_TAG, AppBundle.INSTANCE.feMergeNode());
		elementNameToIcon.put(SVGConstants.SVG_FE_MERGE_TAG, AppBundle.INSTANCE.feMerge());
		elementNameToIcon.put(SVGConstants.SVG_FE_MORPHOLOGY_TAG, AppBundle.INSTANCE.feMorphology());
		elementNameToIcon.put(SVGConstants.SVG_FE_OFFSET_TAG, AppBundle.INSTANCE.feOffset());
		elementNameToIcon.put(SVGConstants.SVG_FE_POINT_LIGHT_TAG, AppBundle.INSTANCE.fePointLight());
		elementNameToIcon.put(SVGConstants.SVG_FE_SPECULAR_LIGHTING_TAG, AppBundle.INSTANCE.feSpecularLight());
		elementNameToIcon.put(SVGConstants.SVG_FE_SPOT_LIGHT_TAG, AppBundle.INSTANCE.feSpotLight());
		elementNameToIcon.put(SVGConstants.SVG_FE_TILE_TAG, AppBundle.INSTANCE.feTile());
		elementNameToIcon.put(SVGConstants.SVG_FE_TURBULENCE_TAG, AppBundle.INSTANCE.feTurbulence());
		elementNameToIcon.put(SVGConstants.SVG_FILTER_TAG, AppBundle.INSTANCE.filter());
		elementNameToIcon.put(SVGConstants.SVG_FONT_FACE_FORMAT_TAG, AppBundle.INSTANCE.fontFaceFormat());
		elementNameToIcon.put(SVGConstants.SVG_FONT_FACE_NAME_TAG, AppBundle.INSTANCE.fontFaceName());
		elementNameToIcon.put(SVGConstants.SVG_FONT_FACE_TAG, AppBundle.INSTANCE.fontFace());
		elementNameToIcon.put(SVGConstants.SVG_FONT_FACE_SRC_TAG, AppBundle.INSTANCE.fontFaceSrc());
		elementNameToIcon.put(SVGConstants.SVG_FONT_FACE_URI_TAG, AppBundle.INSTANCE.fontFaceUri());
		elementNameToIcon.put(SVGConstants.SVG_FONT_TAG, AppBundle.INSTANCE.font());
		elementNameToIcon.put(SVGConstants.SVG_FOREIGN_OBJECT_TAG, AppBundle.INSTANCE.foreignObject());
		elementNameToIcon.put(SVGConstants.SVG_GLYPH_TAG, AppBundle.INSTANCE.glyph());
		elementNameToIcon.put(SVGConstants.SVG_GLYPH_REF_TAG, AppBundle.INSTANCE.glyphRef());
		elementNameToIcon.put(SVGConstants.SVG_G_TAG, AppBundle.INSTANCE.g());
		elementNameToIcon.put(SVGConstants.SVG_H_KERN_TAG, AppBundle.INSTANCE.hkern());
		elementNameToIcon.put(SVGConstants.SVG_IMAGE_TAG, AppBundle.INSTANCE.image());
		elementNameToIcon.put(SVGConstants.SVG_LINEAR_GRADIENT_TAG, AppBundle.INSTANCE.linearGradient());
		elementNameToIcon.put(SVGConstants.SVG_LINE_TAG, AppBundle.INSTANCE.line());
		elementNameToIcon.put(SVGConstants.SVG_MARKER_TAG, AppBundle.INSTANCE.marker());
		elementNameToIcon.put(SVGConstants.SVG_MASK_TAG, AppBundle.INSTANCE.mask());
		elementNameToIcon.put(SVGConstants.SVG_METADATA_TAG, AppBundle.INSTANCE.metadata());
		elementNameToIcon.put(SVGConstants.SVG_MISSING_GLYPH_TAG, AppBundle.INSTANCE.missingGlyph());
		elementNameToIcon.put(SVGConstants.SVG_M_PATH_TAG, AppBundle.INSTANCE.mpath());
		elementNameToIcon.put(SVGConstants.SVG_PATH_TAG, AppBundle.INSTANCE.path());
		elementNameToIcon.put(SVGConstants.SVG_PATTERN_TAG, AppBundle.INSTANCE.pattern());
		elementNameToIcon.put(SVGConstants.SVG_POLYGON_TAG, AppBundle.INSTANCE.polygon());
		elementNameToIcon.put(SVGConstants.SVG_POLYLINE_TAG, AppBundle.INSTANCE.polyline());
		elementNameToIcon.put(SVGConstants.SVG_RADIAL_GRADIENT_TAG, AppBundle.INSTANCE.radialGradient());
		elementNameToIcon.put(SVGConstants.SVG_RECT_TAG, AppBundle.INSTANCE.rect());
		elementNameToIcon.put(SVGConstants.SVG_SCRIPT_TAG, AppBundle.INSTANCE.script());
		elementNameToIcon.put(SVGConstants.SVG_SET_TAG, AppBundle.INSTANCE.set());
		elementNameToIcon.put(SVGConstants.SVG_STOP_TAG, AppBundle.INSTANCE.stop());
		elementNameToIcon.put(SVGConstants.SVG_STYLE_TAG, AppBundle.INSTANCE.style());
		elementNameToIcon.put(SVGConstants.SVG_SVG_TAG, AppBundle.INSTANCE.svg());
		elementNameToIcon.put(SVGConstants.SVG_SWITCH_TAG, AppBundle.INSTANCE.switch_());
		elementNameToIcon.put(SVGConstants.SVG_SYMBOL_TAG, AppBundle.INSTANCE.symbol());
		elementNameToIcon.put(SVGConstants.SVG_TEXT_PATH_TAG, AppBundle.INSTANCE.textPath());
		elementNameToIcon.put(SVGConstants.SVG_TEXT_TAG, AppBundle.INSTANCE.text());
		elementNameToIcon.put(SVGConstants.SVG_TITLE_TAG, AppBundle.INSTANCE.title());
		elementNameToIcon.put(SVGConstants.SVG_T_REF_TAG, AppBundle.INSTANCE.tref());
		elementNameToIcon.put(SVGConstants.SVG_T_SPAN_TAG, AppBundle.INSTANCE.tspan());
		elementNameToIcon.put(SVGConstants.SVG_USE_TAG, AppBundle.INSTANCE.use());
		elementNameToIcon.put(SVGConstants.SVG_VIEW_TAG, AppBundle.INSTANCE.view());
		elementNameToIcon.put(SVGConstants.SVG_V_KERN_TAG, AppBundle.INSTANCE.vkern());
	}
	private MetaModel<SVGElement> metaModel;
	public SVGGenericElementModel(SVGModel owner, SVGElement element, SVGElement twin) {
		super(owner, element, twin);
	}

	@Override
	public MetaModel<SVGElement> getMetaModel() {
		if (metaModel == null) {
			metaModel = new MetaModel<SVGElement>();
			List<ModelCategory<SVGElement>> categories = new ArrayList<ModelCategory<SVGElement>>();
			categories.add(SVGNamedElementModel.getGlobalCategory());
			metaModel.init(
				null,
				elementNameToIcon.get(element.getTagName()), 
				categories,
				null);

		}
		return metaModel;
	}

}
