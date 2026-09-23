package net.incomemc.extension;

import org.bukkit.plugin.java.JavaPlugin;

public final class excellenteconomy extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("Extension-ExcellentEconomy for Plan starting...");
        new PlanHook().hookIntoPlan();
        getLogger().info("Extension-ExcellentEconomy enabled.");
    }

    @Override
    public void onDisable() {
    }
}
