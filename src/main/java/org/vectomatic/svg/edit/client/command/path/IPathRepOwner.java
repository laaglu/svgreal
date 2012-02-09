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
package org.vectomatic.svg.edit.client.command.path;

import java.util.List;

import org.vectomatic.dom.svg.OMSVGGElement;
import org.vectomatic.dom.svg.OMSVGRectElement;
import org.vectomatic.dom.svg.OMSVGSVGElement;

/**
 * Interface for classes which own a segment representation
 * @author laaglu
 */
public interface IPathRepOwner {
	/**
	 * Enum to represent the possible edition modes
	 */
	enum Mode {
		/**
		 * Display both vertices and tangents
		 */
		TANGENT,
		/**
		 * Display only vertices
		 */
		VERTEX;
		public void write(OMSVGGElement g) {
			g.setAttribute(MODE_ATTRIBUTE, name());
		}
		public static Mode read(OMSVGGElement g) {
			String mode = g.getAttribute(MODE_ATTRIBUTE);
			assert ! "".equals(mode);
			return Mode.valueOf(mode);
		}
		static final String MODE_ATTRIBUTE = "mode";
	}

	
	/**
	 * Enum to represent the state of a path vertex
	 */
	public enum VertexState {
		NONE {
			@Override
			void write(OMSVGRectElement vertex) {
				vertex.removeAttribute(STATE_ATTRIBUTE);
			}
			@Override
			String getValue() {
				return ""; 
			}
		},
		SELECTED {
			@Override
			void write(OMSVGRectElement vertex) {
				vertex.setAttribute(STATE_ATTRIBUTE, getValue());
			}
			@Override
			String getValue() {
				return "S"; 
			}
		},
		CLOSING {
			@Override
			void write(OMSVGRectElement vertex) {
				vertex.setAttribute(STATE_ATTRIBUTE, getValue());
			}			
			@Override
			String getValue() {
				return "C"; 
			}
		};
		abstract void write(OMSVGRectElement vertex);
		abstract String getValue();
		
		public static VertexState read(OMSVGRectElement vertex) {
			String attr = vertex.getAttribute(STATE_ATTRIBUTE);
			if (VertexState.NONE.getValue().equals(attr)) {
				return VertexState.NONE;
			}
			if (VertexState.SELECTED.getValue().equals(attr)) {
				return VertexState.SELECTED;
			}
			if (VertexState.CLOSING.getValue().equals(attr)) {
				return VertexState.CLOSING;
			}
			assert false;
			return null;
		}
		static final String STATE_ATTRIBUTE = "state";
	}
	OMSVGSVGElement getSvg();
	List<SVGSegRep> getSegments();
	Mode getMode();
}


