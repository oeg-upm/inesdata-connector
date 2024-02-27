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

import org.eclipse.edc.spi.monitor.Monitor;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Federated cache directory using Registration Service as backend.
 */
public class ConfigurationTargetNodeDirectoryTest {

    private final Monitor monitor = mock();

    @Test
    void verify_withParticipants() {
        ConfigurationTargetNodeDirectory configurationTargetNodeDirectory =  new ConfigurationTargetNodeDirectory(monitor, "id;name;http://targetUrl:19194/protocol");
        assertThat(configurationTargetNodeDirectory.getAll().size()).isEqualTo(1);
    }

    @Test
    void verify_withCorrectParticipantValues() {
        ConfigurationTargetNodeDirectory configurationTargetNodeDirectory =  new ConfigurationTargetNodeDirectory(monitor, "id;name;http://targetUrl:19194/protocol");
        assertThat(configurationTargetNodeDirectory.getAll().get(0).id()).isEqualTo("id");
        assertThat(configurationTargetNodeDirectory.getAll().get(0).name()).isEqualTo("name");
        assertThat(configurationTargetNodeDirectory.getAll().get(0).targetUrl()).isEqualTo("http://targetUrl:19194/protocol");
    }

    @Test
    void verify_withMultipleParticipants() {
        ConfigurationTargetNodeDirectory configurationTargetNodeDirectory =  new ConfigurationTargetNodeDirectory(monitor, "id1;name1;http://targetUrl1:19194/protocol|id2;name2;http://targetUrl2:19194/protocol|id3;name3;http://targetUrl3:19194/protocol");
        assertThat(configurationTargetNodeDirectory.getAll().size()).isEqualTo(3);
        assertThat(configurationTargetNodeDirectory.getAll().get(0).id()).isEqualTo("id1");
        assertThat(configurationTargetNodeDirectory.getAll().get(1).id()).isEqualTo("id2");
        assertThat(configurationTargetNodeDirectory.getAll().get(2).id()).isEqualTo("id3");
    }

    @Test
    void verify_noParticipants() {
        ConfigurationTargetNodeDirectory configurationTargetNodeDirectory =  new ConfigurationTargetNodeDirectory(monitor, "");
        assertThat(configurationTargetNodeDirectory.getAll().size()).isEqualTo(0);
    }

    @Test
    void verify_wrongParticipantListFormat() {
        ConfigurationTargetNodeDirectory configurationTargetNodeDirectory =  new ConfigurationTargetNodeDirectory(monitor, "id.name.http://url/id1.name1.http://url2");
        assertThat(configurationTargetNodeDirectory.getAll().size()).isEqualTo(0);
    }
}
