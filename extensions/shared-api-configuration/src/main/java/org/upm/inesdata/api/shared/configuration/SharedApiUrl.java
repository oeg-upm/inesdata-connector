package org.upm.inesdata.api.shared.configuration;

import java.net.URI;

/**
 * Provides the Shared Api URL exposed, useful for setting callbacks.
 */
@FunctionalInterface
public interface SharedApiUrl {

    /**
     * URI on which the Shared API is exposed
     *
     * @return Shared API URI.
     */
    URI get();
}
