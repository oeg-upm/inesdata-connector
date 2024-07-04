package org.upm.inesdata.auditevent;

import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.spi.event.Event;
import org.eclipse.edc.spi.event.EventRouter;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;

public class AuditEventSubscriptionExtension implements ServiceExtension {
    @Inject
    private EventRouter eventRouter;
    @Inject
    private Monitor monitor;

    @Override
    public void initialize(ServiceExtensionContext context) {
        eventRouter.register(Event.class, new AuditEventSubscriber(monitor));
        eventRouter.registerSync(Event.class, new AuditEventSubscriber(monitor));
    }
}