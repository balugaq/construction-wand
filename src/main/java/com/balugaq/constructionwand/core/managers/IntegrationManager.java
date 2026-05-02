package com.balugaq.constructionwand.core.managers;

import com.balugaq.constructionwand.core.integrations.IIntegration;
import com.balugaq.constructionwand.core.integrations.pylon.PylonIntegration;
import com.balugaq.constructionwand.implementation.ConstructionWandPlugin;
import com.balugaq.constructionwand.utils.Debug;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * @author balugaq
 */
@NullMarked
public class IntegrationManager implements IManager {
    private boolean enabledPylon;
    private final List<IIntegration> integrations = new ArrayList<>();

    @Override
    public void setup() {
        Bukkit.getScheduler().runTaskLaterAsynchronously(ConstructionWandPlugin.getInstance(), () -> {
            PluginManager pm = Bukkit.getPluginManager();

            enabledPylon = pm.isPluginEnabled("Pylon");

            setupIntegration(enabledPylon, PylonIntegration::new);
        }, 2L);
    }

    private void setupIntegration(boolean enabled, Supplier<IIntegration> integrationSupplier) {
        if (enabled) {
            IIntegration integration = integrationSupplier.get();
            try {
                integration.setup();
                integrations.add(integration);
                Debug.log("Loaded " + integration.getClass().getSimpleName());
            } catch (Exception e) {
                Debug.severe("An error occurred when loading " + integration.getClass().getSimpleName());
                Debug.severe(e);
            }
        }
    }

    @Override
    public void shutdown() {
        for (IIntegration integration : integrations) {
            try {
                integration.shutdown();
                Debug.log("Unloaded " + integration.getClass().getSimpleName());
            } catch (Exception e) {
                Debug.severe("An error occurred when unloading " + integration.getClass().getSimpleName());
                Debug.severe(e);
            }
        }
    }
}
