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
package org.vectomatic.svg.edit.client.command;

import java.util.List;
import java.util.Map;

import org.vectomatic.dom.svg.impl.SVGElement;
import org.vectomatic.svg.edit.client.command.edit.EditCircleGeometryManipulator;
import org.vectomatic.svg.edit.client.command.edit.EditEllipseGeometryManipulator;
import org.vectomatic.svg.edit.client.command.edit.EditLineGeometryManipulator;
import org.vectomatic.svg.edit.client.command.edit.EditManipulatorBase;
import org.vectomatic.svg.edit.client.command.edit.EditPathGeometryManipulator;
import org.vectomatic.svg.edit.client.command.edit.EditRectGeometryManipulator;
import org.vectomatic.svg.edit.client.command.edit.EditSVGPointsManipulator;
import org.vectomatic.svg.edit.client.command.edit.EditViewBoxGeometryManipulator;
import org.vectomatic.svg.edit.client.model.MetaModel;
import org.vectomatic.svg.edit.client.model.ModelCategory;
import org.vectomatic.svg.edit.client.model.ModelConstants;
import org.vectomatic.svg.edit.client.model.svg.SVGCircleElementModel;
import org.vectomatic.svg.edit.client.model.svg.SVGElementModel;
import org.vectomatic.svg.edit.client.model.svg.SVGEllipseElementModel;
import org.vectomatic.svg.edit.client.model.svg.SVGLineElementModel;
import org.vectomatic.svg.edit.client.model.svg.SVGPathElementModel;
import org.vectomatic.svg.edit.client.model.svg.SVGPolygonElementModel;
import org.vectomatic.svg.edit.client.model.svg.SVGPolylineElementModel;
import org.vectomatic.svg.edit.client.model.svg.SVGRectElementModel;
import org.vectomatic.svg.edit.client.model.svg.SVGViewBoxElementModel;

/**
 * Class to edit the attributes belonging to the geometry model category
 * @author laaglu
 */
public class EditGeometryCommandFactory extends ManipulatorCommandFactoryBase {
	@SuppressWarnings("serial")
	public static final IFactoryInstantiator<EditGeometryCommandFactory> INSTANTIATOR = new FactoryInstantiatorBase<EditGeometryCommandFactory>(ModelConstants.INSTANCE.editGeometryCmdFactory(), ModelConstants.INSTANCE.editGeometryCmdFactoryDesc()) {
		@Override
		public EditGeometryCommandFactory create() {
			return new EditGeometryCommandFactory();
		}
	};
//	protected static Map<MetaModel, EditManipulatorBase> metaModelToManipulator;
	
	public EditGeometryCommandFactory() {
		ModelConstants constants = ModelConstants.INSTANCE;
		state1 = constants.editGeometryCmdFactory1();
		state2 = constants.editGeometryCmdFactory2();
		filter = new IModelFilter() {
			@Override
			public boolean accept(List<SVGElementModel> models) {
				return models.size() == 1 && models.get(0).getMetaModel().getCategory(ModelCategory.GEOMETRY) != null;
			}			
		};
	}
	
	@Override
	public IFactoryInstantiator<?> getInstantiator() {
		return INSTANTIATOR;
	}

	@Override
	protected ICommand createCommand(SVGElementModel model, Map<String, Object> changes) {
		return new GenericEditCommand(this, model, changes, ModelConstants.INSTANCE.editGeometryCmd());
	}
	
	@Override
	protected EditManipulatorBase getManipulator(SVGElementModel model) {
//		if (metaModelToManipulator == null) {
//			metaModelToManipulator = new HashMap<MetaModel, EditManipulatorBase>();
//			metaModelToManipulator.put(SVGLineElementModel.getLineElementMetaModel(), new EditLineGeometryManipulator());
//			metaModelToManipulator.put(SVGCircleElementModel.getCircleElementMetaModel(), new EditCircleGeometryManipulator());
//			metaModelToManipulator.put(SVGEllipseElementModel.getEllipseElementMetaModel(), new EditEllipseGeometryManipulator());
//			metaModelToManipulator.put(SVGRectElementModel.getRectElementMetaModel(), new EditRectGeometryManipulator());
//			metaModelToManipulator.put(SVGPolygonElementModel.getPolygonElementMetaModel(), new EditSVGPointsManipulator());
//			metaModelToManipulator.put(SVGPolylineElementModel.getPolylineElementMetaModel(), new EditSVGPointsManipulator());
//			metaModelToManipulator.put(SVGViewBoxElementModel.getViewBoxElementMetaModel(), new EditViewBoxGeometryManipulator());
//			metaModelToManipulator.put(SVGPathElementModel.getPathElementMetaModel(), new EditPathGeometryManipulator());
//		}
//		MetaModel metaModel = model.getMetaModel();
//		assert(metaModel != null);
//		return metaModelToManipulator.get(metaModel);
		
		MetaModel<SVGElement> metaModel = model.getMetaModel();
		if (metaModel == SVGLineElementModel.getLineElementMetaModel()) {
			return new EditLineGeometryManipulator();
		}
		if (metaModel == SVGCircleElementModel.getCircleElementMetaModel()) {
			return new EditCircleGeometryManipulator();
		}
		if (metaModel == SVGEllipseElementModel.getEllipseElementMetaModel()) {
			return new EditEllipseGeometryManipulator();
		}
		if (metaModel == SVGRectElementModel.getRectElementMetaModel()) {
			return new EditRectGeometryManipulator();
		}
		if (metaModel == SVGPolygonElementModel.getPolygonElementMetaModel()) {
			return new EditSVGPointsManipulator();
		}
		if (metaModel == SVGPolylineElementModel.getPolylineElementMetaModel()) {
			return new EditSVGPointsManipulator();
		}
		if (metaModel == SVGViewBoxElementModel.getViewBoxElementMetaModel()) {
			return new EditViewBoxGeometryManipulator();
		}
		if (metaModel == SVGPathElementModel.getPathElementMetaModel()) {
			return new EditPathGeometryManipulator();
		}
		return null;
	}
}
