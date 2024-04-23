package org.upm.inesdata.monitor;

import java.util.function.Supplier;

import org.eclipse.edc.spi.monitor.Monitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Slf4jMonitor implements Monitor {

    private static Logger logger = LoggerFactory.getLogger(Slf4jMonitor.class);

    @Override
    public void debug(Supplier<String> supplier, Throwable... errors) {
        debug(supplier.get(), errors);
    }

    @Override
    public void debug(String message, Throwable... errors) {
        if (errors.length == 0) {
            logger.debug(message);
        }
        else {
            for (Throwable error : errors) {
                logger.debug(message, error);
            }
        }
    }

    @Override
    public void info(Supplier<String> supplier, Throwable... errors) {
        info(supplier.get(), errors);
    }
    
    @Override
    public void info(String message, Throwable... errors) {
        if (errors.length == 0) {
            logger.info("Yepah! " + message);
        }
        else {
            for (Throwable error : errors) {
                logger.info(message, error);
            }
        }
    }
    
    @Override
    public void severe(Supplier<String> supplier, Throwable... errors) {
        severe(supplier.get(), errors);
    }
    
    @Override
    public void severe(String message, Throwable... errors) {
        if (errors.length == 0) {
            logger.error(message);
        }
        else {
            for (Throwable error : errors) {
                logger.error(message, error);
            }
        }
    }
    
    @Override
    public void warning(Supplier<String> supplier, Throwable... errors) {
        warning(supplier.get(), errors);
    }
    
    @Override
    public void warning(String message, Throwable... errors) {
        if (errors.length == 0) {
            logger.warn(message);
        }
        else {
            for (Throwable error : errors) {
                logger.warn(message, error);
            }
        }
    }
    

    
    
}
