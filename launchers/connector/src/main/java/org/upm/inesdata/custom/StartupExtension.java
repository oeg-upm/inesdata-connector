/*
 *  Copyright (c) 2024 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Bayerische Motoren Werke Aktiengesellschaft (BMW AG) - initial API and implementation
 *
 */

package org.upm.inesdata.custom;

import org.upm.inesdata.monitor.Slf4jMonitor;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.spi.security.Vault;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.system.MonitorExtension;

public class StartupExtension implements ServiceExtension, MonitorExtension {

    @Override
    public Monitor getMonitor() {
        
        return new Slf4jMonitor();
    }

    @Inject
    private Vault vault;

    private static final String PUBLIC_KEY = """
            -----BEGIN CERTIFICATE-----
            MIIDETCCAfkCFBEOuzF+mRgrzrxVv70zNDUEv+nUMA0GCSqGSIb3DQEBCwUAMEUx
            CzAJBgNVBAYTAkFVMRMwEQYDVQQIDApTb21lLVN0YXRlMSEwHwYDVQQKDBhJbnRl
            cm5ldCBXaWRnaXRzIFB0eSBMdGQwHhcNMjQwMTMxMTIyNTUyWhcNMjUwMTMwMTIy
            NTUyWjBFMQswCQYDVQQGEwJBVTETMBEGA1UECAwKU29tZS1TdGF0ZTEhMB8GA1UE
            CgwYSW50ZXJuZXQgV2lkZ2l0cyBQdHkgTHRkMIIBIjANBgkqhkiG9w0BAQEFAAOC
            AQ8AMIIBCgKCAQEAxHbsW755sB/5YwjPyk0xPJwyQkxkGV2SS4sQQCCz8KV/QoEi
            lk/PzUqUsE2e7LqavWL5+FPSic79ZKEljeeNB1z5xoE2YnKEs55MLi/PgfLJHJ9M
            bA42lJBYI37MeGv0kGkIQDIrtpDjBfSgGSINXeTjs0T/l5sXXPTfSRm4URWX3I+Q
            O9ACYLb7Cz//G8HdYHp8MHNa38x4BeBku7cT7xgEUPfhu4LCnBW1u0pMbeT8A2M9
            zz/TIOJKwUeupacXjJ/tqHGLYMgBLCSRNK4zFJiGzhDbEZA6+NMUp660t7Su6M5i
            x3WIffKlIcxi0bnk9slhjK5NynJ3H/uvbfYnCwIDAQABMA0GCSqGSIb3DQEBCwUA
            A4IBAQBAttZGxh1k87S8OHA6L8fmu/PEtjdkJV3Cu7J3xTGfmSBVMTStl2NI4D4i
            FJUFdTJjvEibEaQEETz2zJakwdp7JGG7ip7e/dKsP0wVMC63MZ8eeDzST7IzBLk6
            mjb+Co0+2p/9rHPBz1AFpHRcJ9jaORh8FlctfcxcBsr6dMptGtCZMzam26WwePxa
            pNd+wBNFHuAXHyTzHl3NIq2Bd+56Roen5muZKv4xqI7iVBbVrejvVyXNDhrqh9QB
            VushjclGdc2GSdCVpUhiv1srX59/yX5HBZG8HRQSgCXpcSiGIhiMmd1gaU/qrXa8
            kwmd6tdKUcAiLU7qXEJX4iBziobm
            -----END CERTIFICATE-----
            """;

    private static final String PRIVATE_KEY = """
            -----BEGIN PRIVATE KEY-----
            MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDEduxbvnmwH/lj
            CM/KTTE8nDJCTGQZXZJLixBAILPwpX9CgSKWT8/NSpSwTZ7supq9Yvn4U9KJzv1k
            oSWN540HXPnGgTZicoSznkwuL8+B8skcn0xsDjaUkFgjfsx4a/SQaQhAMiu2kOMF
            9KAZIg1d5OOzRP+Xmxdc9N9JGbhRFZfcj5A70AJgtvsLP/8bwd1genwwc1rfzHgF
            4GS7txPvGARQ9+G7gsKcFbW7Skxt5PwDYz3PP9Mg4krBR66lpxeMn+2ocYtgyAEs
            JJE0rjMUmIbOENsRkDr40xSnrrS3tK7ozmLHdYh98qUhzGLRueT2yWGMrk3Kcncf
            +69t9icLAgMBAAECggEAMrh/IdplgS5oDITQXGqcXQj4QryFFTVemQkwa1XmDqkz
            VyCOpN67B8WK6I9JidVNMLq2TUGat8BME+g0kVOnybBbvb9LIpRtJRAnm771JBfh
            ivWnXbg3qBgx4QwRzr8UNxoeqrApL+ts6PM0R/jOGaEHlhcoo2PDXtjLMoyk1K26
            JdXibZ8TjqS59MG9BB8j2/NiyaQxC46k8FPz5UXnLAVr8z8kzi0P/LfuZdFO3BjB
            1Pgsgy7zme32nUCTJSQbCUKngY7KAwaeuPVji5QbkJ0Tk1tWop9IKg43RzqVp+Me
            QZWDgIyeWMRkmKb6GXrl6HjuOFEymMQkPlRH7QPDcQKBgQDjOaYaPFQIgkkKAoMa
            DGT0bv1RdPWithYHDTKpa6xMqfZ5/H+09i/+wVH1P8j2osylZOunsCVoIeeeTuv4
            VYC+VTOboOFdbzKD9gOyvZ4nUox+nT/1awK/ZBRcX1GrItZLs7QbrWHswT1VWTcO
            ccj7wVIKs15yuBuV0w97qA1h0QKBgQDdWA2v1Yj0jgOn8leCSGj2LlzYObuEWEn4
            lCNn0b5ZABTqFpG6viNpalyTUHDXGYt3mZpEBpW2O+x7Bv9EO8+G9TtkW13NL+eD
            4+4wftRx4LTuOlG5wAicNG0dvaobMZ3Ba/v+KCs2pQaC6pC0E12j5euPoTl6pUxL
            0caANS72GwKBgA6Yu3Tf/z30sKB1/NoFhc61+ovrCYSEUfHuBR0hD5Z1LKI1eDa1
            weuJm6w3hr9hQeg2DXVeOWjxFKH0y+/N/lE7w+7xbMjFxeb4PNCUob5A7UmM7Hjl
            gO2ujihI70QhHscKKj3E64978mazLng9Ev4pMw4A68ZO0WbUgiAdcY6hAoGBAJA6
            YDDDI4q3PwqgjwUFyd8gNr9uXxHNteWUIb30nePAeeBO9IaKfuihtdEICX8f1hEI
            tcy9vbARO/auiIcdsgxun1S1dFnPbTwzuXFAM45AraTNu5Md3n5qau+GNuGhYvo2
            Mu1Zn07sGWIVFlVAiTPTDbt5gsq4Cw8ckgvYm9EhAoGAJEi0oDOOPS8skP71HsEm
            fwMhp414LL7xFf4LZj8RL4+vDfzyNnbQ3/zpGKCzwO2MWAQx4GB9hNrX8meGLJXP
            w5zMub2RLs0x4pZPuIQVIfcf6Ryuwx/kM3uZAj6lUm5LpfsWJNFZylC2ixuLTLgn
            B1fcrLsMChYYsJvCfzElLSs=
            -----END PRIVATE KEY-----
            """;


    @Override
    public void initialize(ServiceExtensionContext context) {
        vault.storeSecret("public-key", PUBLIC_KEY);
        vault.storeSecret("private-key", PRIVATE_KEY);
        vault.storeSecret("access-key", "DrJ6PmnJ3QtlxYowRSLi");
        vault.storeSecret("secret-key", "gjaVnJstoeV4bWLrJkmswKBMIZxY10PjZOJhp2qk");
    }
}
