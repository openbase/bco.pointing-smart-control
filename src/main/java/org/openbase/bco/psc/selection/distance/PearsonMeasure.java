package org.openbase.bco.psc.selection.distance;

/*-
 * #%L
 * BCO Pointing Smart Control
 * %%
 * Copyright (C) 2016 - 2017 openbase.org
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import org.openbase.bco.psc.selection.BoundingBox;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

/**
 *
 * @author <a href="mailto:thuppke@techfak.uni-bielefeld.de">Thoren Huppke</a>
 */
public class PearsonMeasure extends AbstractDistanceMeasure {
    
    //TODO: Implement the change from center to translation.

    @Override
    protected double distanceProbability(Point3d origin, Vector3d direction, BoundingBox boundingBox) {
        Vector3d dir = getPerpendicularFootDirection(origin, direction, boundingBox.getRootCenter());
        if(dir == null) return Double.MAX_VALUE;
        removeRotation(boundingBox.getOrientation(), dir);
        return pearsonLength(dir, boundingBox.getBoxVector());
    }
    
    private double pearsonLength(final Vector3d vector, final Vector3d size){
        return Math.sqrt((vector.x*vector.x)/(size.x*size.x) + (vector.y*vector.y)/(size.y*size.y) + (vector.z*vector.z)/(size.z*size.z));
    }
    
}