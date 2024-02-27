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

import org.eclipse.edc.crawler.spi.TargetNode;
import org.eclipse.edc.crawler.spi.TargetNodeDirectory;
import org.eclipse.edc.spi.monitor.Monitor;

import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;

/**
 * Federated cache directory using a string with a list of TargetNode items.
 */
public class ConfigurationTargetNodeDirectory implements TargetNodeDirectory {

    public static final List<String> SUPPORTED_PROTOCOLS = List.of("dataspace-protocol-http");
    public static final String PARTICIPANT_SEPARATOR = "\\|";
    public static final String PARTICIPANT_INFO_SEPARATOR = ";";

    private final Monitor monitor;
    private final List<TargetNode> participantList;

    /**
     * Constructs {@link ConfigurationTargetNodeDirectory}
     *
     * @param monitor   monitor
     * @param rawParticipantList RegistrationService API client.
     */
    public ConfigurationTargetNodeDirectory(Monitor monitor, String rawParticipantList) {
        this.monitor = monitor;
        this.participantList = processRawParticipantList(rawParticipantList);
    }

    @Override
    public List<TargetNode> getAll() {
        return participantList;
    }

    @Override
    public void insert(TargetNode targetNode) {
        throw new UnsupportedOperationException();
    }

    private List<TargetNode> processRawParticipantList(String rawParticipantList) {
        var participants = new ArrayList<TargetNode>();
        try {
            String[] rawParticipants = rawParticipantList.split(ConfigurationTargetNodeDirectory.PARTICIPANT_SEPARATOR);
            for (String rawParticipant : rawParticipants) {         
                String[] participant = rawParticipant.split(ConfigurationTargetNodeDirectory.PARTICIPANT_INFO_SEPARATOR);  
                participants.add(new TargetNode(participant[1], participant[0], participant[2], ConfigurationTargetNodeDirectory.SUPPORTED_PROTOCOLS));
            }
        } catch (Exception ex) {
            monitor.severe(format("Error processing the list of participants. The expected format is: id;name;targetUrl|id;name;targetUrl and the provided list is '%s'. The exception is: %s", rawParticipantList, ex.getMessage()));
        }
        monitor.warning(format("Obtained a total of %s participants", participants.size()));
        return participants;
    }
}
