package org.upm.inesdata.monitor;

import org.eclipse.edc.spi.monitor.ConsoleMonitor;
import org.eclipse.edc.spi.monitor.ConsoleMonitor.Level;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.system.MonitorExtension;

public class ConnectorMonitorExtension implements MonitorExtension {

    @Override
    public Monitor getMonitor() {
        
        return new ConsoleMonitor("", Level.INFO);
    }
}