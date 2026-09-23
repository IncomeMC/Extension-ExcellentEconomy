package net.incomemc.extension;

import com.djrapitops.plan.capability.CapabilityService;
import com.djrapitops.plan.extension.ExtensionService;
import org.bukkit.Bukkit;

public class PlanHook {

    public void hookIntoPlan() {
        if (!areAllCapabilitiesAvailable()) return;

        registerDataExtension();
        listenForPlanReloads();
    }

    private boolean areAllCapabilitiesAvailable() {
        CapabilityService capabilities = CapabilityService.getInstance();
        return capabilities.hasCapability("DATA_EXTENSION_VALUES");
    }

    private void registerDataExtension() {
        ExtensionService.getInstance().register(new ExcellentEconomyExtension());
        Bukkit.getLogger().info("[ExcellentEconomy] Data extension registered with Plan.");

        /*
        } catch (IllegalStateException planIsNotEnabled) {
            // Plan is not enabled
        } catch (IllegalArgumentException dataExtensionImplementationIsInvalid) {
            // The DataExtension implementation has an implementation error
        }
         */
    }

    private void listenForPlanReloads() {
        CapabilityService.getInstance().registerEnableListener(
                isPlanEnabled -> {
                    if (isPlanEnabled) {
                        registerDataExtension();
                    }
                }
        );
    }
}