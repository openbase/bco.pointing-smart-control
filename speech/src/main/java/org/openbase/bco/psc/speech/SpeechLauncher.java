package org.openbase.bco.psc.speech;

/*
 * -
 * #%L
 * BCO PSC Speech
 * %%
 * Copyright (C) 2016 - 2020 openbase.org
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

import org.openbase.bco.authentication.lib.BCO;
import org.openbase.bco.psc.lib.jp.JPPSCBaseScope;
import org.openbase.bco.psc.lib.jp.JPPscUnitFilterList;
import org.openbase.jps.core.JPService;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.InstantiationException;
import org.openbase.jul.extension.rsb.com.jp.JPRSBHost;
import org.openbase.jul.extension.rsb.com.jp.JPRSBPort;
import org.openbase.jul.extension.rsb.com.jp.JPRSBTransport;
import org.openbase.jul.pattern.launch.AbstractLauncher;


/**
 * The class that launches the speech component.
 *
 * @author <a href="mailto:thuppke@techfak.uni-bielefeld.de">Thoren Huppke</a>
 * @author <a href="mailto:dreinsch@techfak.uni-bielefeld.de">Dennis Reinsch</a>
 * @author <a href="mailto:jbitschene@techfak.uni-bielefeld.de">Jennifer Bitschene</a>
 * @author <a href="mailto:jniermann@techfak.uni-bielefeld.de">Julia Niermann</a>
 */
public class SpeechLauncher extends AbstractLauncher<SpeechController> {

    public SpeechLauncher() throws InstantiationException {
        super(Speech.class, SpeechController.class);
    }

    @Override
    protected void loadProperties() {
        // Scopes
        JPService.registerProperty(JPPSCBaseScope.class);
        //JPService.registerProperty(JPRayScope.class);
        //JPService.registerProperty(JPIntentScope.class);

        // Threshold
        //JPService.registerProperty(JPIdentificationThreshold.class);

        // Unit filter
        JPService.registerProperty(JPPscUnitFilterList.class);

        // Component specific
        //JPService.registerProperty(JPUnitSelectorType.class);
        //JPService.registerProperty(JPDistanceType.class);

        // Transport specification

        JPService.registerProperty(JPRSBTransport.class);
        JPService.registerProperty(JPRSBHost.class);
        JPService.registerProperty(JPRSBPort.class);
    }

    /**
     * @param args the command line arguments
     * @throws java.lang.InterruptedException
     * @throws org.openbase.jul.exception.CouldNotPerformException
     */
    public static void main(final String[] args) throws InterruptedException, CouldNotPerformException {
        BCO.printLogo();
        AbstractLauncher.main(args, Speech.class, SpeechLauncher.class);

    }
}
