package org.upm.inesdata.monitor;

import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.system.MonitorExtension;

public class ConnectorMonitorExtension implements MonitorExtension {

    @Override
    public Monitor getMonitor() {
        
        return new Slf4jMonitor();
    }
}