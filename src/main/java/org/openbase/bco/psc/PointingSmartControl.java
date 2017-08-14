package org.openbase.bco.psc;

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

import java.util.List;
import java.util.concurrent.TimeUnit;
import org.openbase.bco.psc.jp.JPDistanceType;
import org.openbase.bco.psc.jp.JPInScope;
import org.openbase.bco.psc.jp.JPLocalInput;
import org.openbase.bco.psc.jp.JPRegistryFlags;
import org.openbase.bco.psc.jp.JPSelectorType;
import org.openbase.bco.psc.jp.JPThreshold;
import org.openbase.bco.psc.registry.PointingUnitChecker;
import org.openbase.bco.psc.registry.SelectableObject;
import org.openbase.bco.psc.registry.SelectableObjectFactory;
import org.openbase.bco.psc.rsb.RSBConnection;
import org.openbase.bco.psc.selection.AbstractSelector;
import org.openbase.bco.psc.selection.MaxSelector;
import org.openbase.bco.psc.selection.MeanSelector;
import org.openbase.bco.psc.selection.SelectorType;
import org.openbase.bco.psc.selection.distance.AbstractDistanceMeasure;
import org.openbase.bco.psc.selection.distance.AngleMeasure;
import org.openbase.bco.psc.selection.distance.AngleVsMaxMeasure;
import org.openbase.bco.psc.selection.distance.DistanceType;
import org.openbase.bco.psc.selection.distance.OrthogonalMeasure;
import org.openbase.bco.psc.selection.distance.OrthogonalVsMaxMeasure;
import org.openbase.bco.psc.selection.distance.PearsonMeasure;
import org.openbase.jps.core.JPService;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.slf4j.LoggerFactory;
import rsb.AbstractEventHandler;
import rsb.Event;
import rst.tracking.PointingRay3DFloatCollectionType.PointingRay3DFloatCollection;
import org.openbase.bco.registry.remote.Registries;
import static org.openbase.bco.registry.remote.Registries.getUnitRegistry;
import org.openbase.jps.exception.JPNotAvailableException;
import org.openbase.jul.exception.InstantiationException;
import org.openbase.jul.exception.NotAvailableException;
import org.openbase.jul.exception.VerificationFailedException;
import org.openbase.jul.exception.printer.LogLevel;
import org.openbase.jul.storage.registry.RegistrySynchronizer;
import rst.domotic.unit.UnitConfigType;
import rst.domotic.unit.UnitProbabilityCollectionType.UnitProbabilityCollection;

/**
 * 
 * @author <a href="mailto:thuppke@techfak.uni-bielefeld.de">Thoren Huppke</a>
 */
public class PointingSmartControl extends AbstractEventHandler {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(PointingSmartControl.class);
    private AbstractSelector selector;
    private RSBConnection rsbConnection;
    
    private RegistrySynchronizer<String, SelectableObject, UnitConfigType.UnitConfig, UnitConfigType.UnitConfig.Builder> selectableObjectRegistrySynchronizer;
    
    private List<String> registryFlags;
    private boolean connectedRegistry = false;
    
    // TODO list:
    //-decide for double or float! (Single unitConfig/unitProbabilityDistribution)

    @Override
    public void handleEvent(final Event event) {
//        LOGGER.trace(event.toString());
        if ((event.getData() instanceof PointingRay3DFloatCollection)) {
            PointingRay3DFloatCollection collection = (PointingRay3DFloatCollection) event.getData();
            try {
                UnitProbabilityCollection selectedUnits = selector.getUnitProbabilities(collection);
                // TODO process the results!
            } catch (CouldNotPerformException ex) {
                ExceptionPrinter.printHistory(ex, LOGGER, LogLevel.ERROR);
            }
        }
    }
    
    public PointingSmartControl() {
        try {
            initSelector();
            try {
                registryFlags = JPService.getProperty(JPRegistryFlags.class).getValue();
                
                initializeRegistryConnection();

                rsbConnection = new RSBConnection(this);
            } catch (CouldNotPerformException | JPNotAvailableException | InterruptedException ex) {
//                selectableObjectRegistrySynchronizer.deactivate();
                throw ex;
            }
            try {
                // Wait for events.
                while (true) {
                    Thread.sleep(1);
                }
            } finally {
                // Deactivate the listener after use.
                rsbConnection.deactivate();
            }
        } catch (Exception ex) { 
            ExceptionPrinter.printHistory(new CouldNotPerformException("PointingSmartControl failed", ex), LOGGER);
            System.exit(255);
        }
    }

    public final void initializeRegistryConnection() throws InterruptedException, CouldNotPerformException{
        if(connectedRegistry) return;
        try {
            LOGGER.info("Initializing Registry synchronization.");
            Registries.getUnitRegistry().waitForData(3, TimeUnit.SECONDS);
            
            this.selectableObjectRegistrySynchronizer = new RegistrySynchronizer<String, SelectableObject, UnitConfigType.UnitConfig, UnitConfigType.UnitConfig.Builder>(
                    selector.getSelectedObjectRegistry(), getUnitRegistry().getUnitConfigRemoteRegistry(), SelectableObjectFactory.getInstance()) {
                @Override
                public boolean verifyConfig(UnitConfigType.UnitConfig config) throws VerificationFailedException {
                    try {
                        return PointingUnitChecker.isApplicableUnit(config, registryFlags);
                    } catch (InterruptedException ex) {
                        ExceptionPrinter.printHistory(ex, logger);
                        return false;
                    }
                }
            };
            
            Registries.waitForData(); 
            selectableObjectRegistrySynchronizer.activate();
            connectedRegistry = true;
        } catch (NotAvailableException ex) {
            throw new CouldNotPerformException("Could not connect to the registry.", ex);
        } catch (CouldNotPerformException ex) {
            throw new CouldNotPerformException("The RegistrySynchronization could not be activated although connection to the registry is possible.", ex);
        }
    }
    
    private void initSelector() throws JPNotAvailableException, InstantiationException{
        SelectorType selectorType = JPService.getProperty(JPSelectorType.class).getValue();
        LOGGER.info("Selected Selector implementation: " + selectorType.name());
        DistanceType distanceType = JPService.getProperty(JPDistanceType.class).getValue();
        LOGGER.info("Selected Distance implementation: " + distanceType.name());
        AbstractDistanceMeasure distanceMeasure;
        switch(distanceType) {
            case ANGLE:
                distanceMeasure = new AngleMeasure();
                break;
            case ANGLE_MAX:
                distanceMeasure = new AngleVsMaxMeasure();
                break;
            case ORTHOGONAL:
                distanceMeasure = new OrthogonalMeasure();
                break;
            case ORTHOGONAL_MAX:
                distanceMeasure = new OrthogonalVsMaxMeasure();
                break;
            case PEARSON:
                distanceMeasure = new PearsonMeasure();
                break;
            default:
                distanceMeasure = new AngleMeasure();
                break;
        }
        switch(selectorType) {
            case MAX:
                selector = new MaxSelector(distanceMeasure);
                break;
            case MEAN:
                selector = new MeanSelector(distanceMeasure);
                break;
            default:
                selector = new MeanSelector(distanceMeasure);
                break;
        }
        
    }
    
    public static void main(String[] args) throws InterruptedException {
        /* Setup JPService */
        JPService.setApplicationName(PointingSmartControl.class);
        JPService.registerProperty(JPRegistryFlags.class);
        JPService.registerProperty(JPThreshold.class);
        JPService.registerProperty(JPSelectorType.class);
        JPService.registerProperty(JPDistanceType.class);
        JPService.registerProperty(JPInScope.class);
        JPService.registerProperty(JPLocalInput.class);
        JPService.parseAndExitOnError(args);
        
        PointingSmartControl app = new PointingSmartControl();
    }
}
