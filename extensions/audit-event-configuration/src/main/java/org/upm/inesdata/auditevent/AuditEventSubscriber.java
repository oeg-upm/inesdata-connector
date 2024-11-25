package org.upm.inesdata.auditevent;

import org.eclipse.edc.connector.controlplane.contract.spi.event.contractnegotiation.ContractNegotiationEvent;
import org.eclipse.edc.connector.controlplane.transfer.spi.event.TransferProcessEvent;
import org.eclipse.edc.spi.event.Event;
import org.eclipse.edc.spi.event.EventEnvelope;
import org.eclipse.edc.spi.event.EventSubscriber;
import org.eclipse.edc.spi.monitor.Monitor;

import java.text.MessageFormat;

/**
 * Subscriber for auditing contract negotiation and transfer process events.
 * Logs event details using a specified monitoring interface.
 */
public class AuditEventSubscriber implements EventSubscriber {

    private static final String NEGOTIATION_TEMPLATE_AUDIT_LOG = "[AUDIT][{0}][DSP] ''{1}'' from counterPartyId ''{2}'' with contractNegotiationId ''{3}''";
    private static final String TRANSFER_TEMPLATE_AUDIT_LOG = "[AUDIT][{0}][DSP] ''{1}'' from contractId ''{2}'' with assetId ''{3}'' for type ''{4}''";
    private final Monitor monitor;
    private final String participantId;

    /**
     * Constructor for AuditEventSubscriber.
     *
     * @param monitor      the monitor interface used for logging
     * @param participantId the participant ID for audit log entries
     */
    public AuditEventSubscriber(Monitor monitor, String participantId) {
        this.monitor = monitor;
        this.participantId = participantId;
    }

    /**
     * Processes the received event envelope and logs relevant details based on event type.
     *
     * @param event the event envelope containing the event to be processed
     * @param <E>   the type of the event
     */
    @Override
    public <E extends Event> void on(EventEnvelope<E> event) {
        if (event.getPayload() instanceof ContractNegotiationEvent) {
            String simpleName = event.getPayload().getClass().getSimpleName();
            ContractNegotiationEvent payload = (ContractNegotiationEvent) event.getPayload();
            monitor.info(MessageFormat.format(NEGOTIATION_TEMPLATE_AUDIT_LOG, participantId, simpleName, payload.getCounterPartyId(), payload.getContractNegotiationId()));
        } else if (event.getPayload() instanceof TransferProcessEvent) {
            String simpleName = event.getPayload().getClass().getSimpleName();
            TransferProcessEvent payload = (TransferProcessEvent) event.getPayload();
            monitor.info(MessageFormat.format(TRANSFER_TEMPLATE_AUDIT_LOG, participantId, simpleName, payload.getContractId(), payload.getAssetId(), payload.getType()));
        }
    }
}
