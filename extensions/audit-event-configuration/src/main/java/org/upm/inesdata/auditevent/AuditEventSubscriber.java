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

    public AuditEventSubscriber(Monitor monitor) {
        this.monitor = monitor;
    }

    @Override
    public <E extends Event> void on(EventEnvelope<E> event) {
        if(event.getPayload() instanceof ContractNegotiationEvent){
            String simpleName = event.getPayload().getClass().getSimpleName();
            ContractNegotiationEvent payload = (ContractNegotiationEvent) event.getPayload();
            monitor.info(MessageFormat.format("[AUDIT][DSP] '{0}' from counterPartyId ''{1}'' with contractNegotiationId ''{2}''",simpleName, payload.getCounterPartyId(),payload.getContractNegotiationId()));
        }else if (event.getPayload() instanceof TransferProcessEvent){
            String simpleName = event.getPayload().getClass().getSimpleName();
            TransferProcessEvent payload = (TransferProcessEvent) event.getPayload();
            monitor.info(MessageFormat.format("[AUDIT][DSP] '{0}' from contractId ''{1}'' with assetId ''{2}'' for type ''{3}''",simpleName, payload.getContractId(),payload.getAssetId(),payload.getType()));
        }

    }
}