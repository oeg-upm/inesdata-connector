package org.upm.inesdata.validator;

import org.eclipse.edc.runtime.metamodel.annotation.Extension;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.runtime.metamodel.annotation.Provides;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.edc.validator.spi.JsonObjectValidatorRegistry;

import static org.eclipse.edc.connector.controlplane.asset.spi.domain.Asset.EDC_ASSET_TYPE;

/**
 * Service extension for asset validation.
 */
@Provides(AssetValidatorExtension.class)
@Extension(value = AssetValidatorExtension.NAME)
public class AssetValidatorExtension implements ServiceExtension {

    public static final String NAME = "Asset Validator";

    @Inject
    private JsonObjectValidatorRegistry validator;


    /**
     * Returns the name of the extension.
     *
     * @return the name of the extension
     */
    @Override
    public String name() {
        return NAME;
    }

    @Override
    public void prepare() {
        validator.register(EDC_ASSET_TYPE, InesdataAssetValidator.instance());
    }
}
