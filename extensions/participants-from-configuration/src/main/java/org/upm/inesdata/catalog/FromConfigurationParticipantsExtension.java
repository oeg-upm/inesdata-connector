/*
 *  Copyright (c) 2022 Microsoft Corporation
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Microsoft Corporation - initial API and implementation
 *
 */

package org.upm.inesdata.catalog;

import org.eclipse.edc.crawler.spi.TargetNodeDirectory;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.runtime.metamodel.annotation.Provider;
import org.eclipse.edc.runtime.metamodel.annotation.Setting;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;


/**
 * Extension to set up federated target node directory using a configuration variable.
 */
public class FromConfigurationParticipantsExtension implements ServiceExtension {

    @Setting
    public static final String INESDATA_FC_PARTICIPANT_LIST = "edc.catalog.configuration.participant.list";

    @Provider
    public TargetNodeDirectory federatedCacheNodeDirectory(ServiceExtensionContext context) {
        var monitor = context.getMonitor();
        var participantList = context.getConfig().getString(INESDATA_FC_PARTICIPANT_LIST);
        monitor.debug("Retrieved the participant list from configuration: " + participantList);
        
        return new ConfigurationTargetNodeDirectory(monitor, participantList);
    }
}


