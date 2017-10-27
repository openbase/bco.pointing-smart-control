package org.openbase.bco.psc.gc;

import org.openbase.bco.psc.control.jp.JPCooldownTime;
import org.openbase.bco.psc.lib.jp.JPLocalInput;
import org.openbase.bco.psc.lib.jp.JPLocalOutput;
import org.openbase.bco.psc.lib.jp.JPPSCBaseScope;
import org.openbase.bco.psc.lib.jp.JPPostureScope;
import org.openbase.bco.registry.lib.BCO;
import org.openbase.jps.core.JPService;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.InstantiationException;
import org.openbase.jul.pattern.launch.AbstractLauncher;

/*
 * #%L
 * BCO PSC Gesture Control
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program. If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */
/**
 *
 * @author <a href="mailto:thuppke@techfak.uni-bielefeld.de">Thoren Huppke</a>
 */
public class GestureControlLauncher extends AbstractLauncher<GestureControlController> {

    public GestureControlLauncher() throws InstantiationException {
        super(GestureControl.class, GestureControlController.class);
    }

    @Override
    protected void loadProperties() {
        // Scopes
        JPService.registerProperty(JPPSCBaseScope.class);
        JPService.registerProperty(JPPostureScope.class);

        // Component specific
        JPService.registerProperty(JPCooldownTime.class);

        // Transport specification
        JPService.registerProperty(JPLocalInput.class);
        JPService.registerProperty(JPLocalOutput.class);
    }

    /**
     * @param args the command line arguments
     * @throws java.lang.InterruptedException
     * @throws org.openbase.jul.exception.CouldNotPerformException
     */
    public static void main(final String[] args) throws InterruptedException, CouldNotPerformException {
        BCO.printLogo();
        AbstractLauncher.main(args, GestureControl.class, GestureControlLauncher.class);
    }
}