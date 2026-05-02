package com.balugaq.constructionwand.core.integrations.pylon;

import com.balugaq.constructionwand.api.providers.IItemProvider;
import com.balugaq.constructionwand.core.integrations.IIntegration;

/**
 * @author balugaq
 */
public class PylonIntegration implements IIntegration {
    @Override
    public void setup() {
        IItemProvider.registerProvider(new SiloItemProvider());
    }

    @Override
    public void shutdown() {
    }
}
