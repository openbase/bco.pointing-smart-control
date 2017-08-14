package org.openbase.bco.psc.identification.selection.distance;

/*-
 * #%L
 * BCO PSC Identification
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

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

/**
 *
 * @author <a href="mailto:thuppke@techfak.uni-bielefeld.de">Thoren Huppke</a>
 */
public class AngleVsMaxMeasure extends AbstractDistanceMeasure {

    @Override
    protected double distanceProbability(Point3d origin, Vector3d direction, float width, float depth, float height) {
        Vector3d toCenter = new Vector3d(origin);
        toCenter.scale(-1.0);
        Vector3d maxPointDirection = new Vector3d(getMaximalPointOnBox(origin, direction, width, depth, height));
        maxPointDirection.sub(origin);
        double angle = getAngle(toCenter, direction);
        if(angle > Math.PI/2) return 0;
        double maxAngle = getAngle(toCenter, maxPointDirection);
        //TODO: Calculate probability:
        double name = angle/Math.sqrt(maxAngle);
        
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
