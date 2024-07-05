package org.upm.inesdata.auditevent;

import org.eclipse.edc.connector.controlplane.contract.spi.event.contractnegotiation.ContractNegotiationEvent;
import org.eclipse.edc.connector.controlplane.transfer.spi.event.TransferProcessEvent;
import org.eclipse.edc.spi.event.Event;
import org.eclipse.edc.spi.event.EventEnvelope;
import org.eclipse.edc.spi.event.EventSubscriber;
import org.eclipse.edc.spi.monitor.Monitor;

import java.text.MessageFormat;

public class AuditEventSubscriber implements EventSubscriber {

    private final Monitor monitor;
    private final String participantId;

    public AuditEventSubscriber(Monitor monitor, String participantId) {
        this.monitor = monitor;
        this.participantId = participantId;
    }

    @Override
    public <E extends Event> void on(EventEnvelope<E> event) {
        if(event.getPayload() instanceof ContractNegotiationEvent){
            String simpleName = event.getPayload().getClass().getSimpleName();
            ContractNegotiationEvent payload = (ContractNegotiationEvent) event.getPayload();
            monitor.info(MessageFormat.format("[AUDIT][''{0}''][DSP] ''{1}'' from counterPartyId ''{2}'' with contractNegotiationId ''{3}''",participantId,simpleName, payload.getCounterPartyId(),payload.getContractNegotiationId()));
        }else if (event.getPayload() instanceof TransferProcessEvent){
            String simpleName = event.getPayload().getClass().getSimpleName();
            TransferProcessEvent payload = (TransferProcessEvent) event.getPayload();
            monitor.info(MessageFormat.format("[AUDIT][''{0}''][DSP] ''{1}'' from contractId ''{2}'' with assetId ''{3}'' for type ''{4}''",participantId,simpleName, payload.getContractId(),payload.getAssetId(),payload.getType()));
        }

    }
}