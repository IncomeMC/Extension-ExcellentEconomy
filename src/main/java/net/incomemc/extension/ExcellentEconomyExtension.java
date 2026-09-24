package net.incomemc.extension;

import com.djrapitops.plan.extension.DataExtension;
import com.djrapitops.plan.extension.NotReadyException;
import com.djrapitops.plan.extension.annotation.DataBuilderProvider;
import com.djrapitops.plan.extension.annotation.PluginInfo;
import com.djrapitops.plan.extension.annotation.TableProvider;
import com.djrapitops.plan.extension.builder.ExtensionDataBuilder;
import com.djrapitops.plan.extension.icon.Color;
import com.djrapitops.plan.extension.icon.Family;
import com.djrapitops.plan.extension.icon.Icon;
import com.djrapitops.plan.extension.table.Table;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import su.nightexpress.excellenteconomy.api.ExcellentEconomyAPI;
import su.nightexpress.excellenteconomy.api.currency.ExcellentCurrency;
import su.nightexpress.excellenteconomy.tops.TopManager;

import java.util.UUID;

@PluginInfo(
        name = "ExcellentEconomy",
        iconName = "money-bill-1",
        iconFamily = Family.SOLID,
        color = Color.GREEN
)
public class ExcellentEconomyExtension implements DataExtension {

    public static ExcellentEconomyAPI getAPI() {
        RegisteredServiceProvider<ExcellentEconomyAPI> provider =
                Bukkit.getServicesManager().getRegistration(ExcellentEconomyAPI.class);

        if (provider == null) {
            throw new NotReadyException();
        }

        return provider.getProvider();
    }

    private TopManager getTopManager() {
        return getAPI().topManager().get();
    }

    private double getTotal(ExcellentCurrency currency) {
        return getTopManager().getTotalBalance(currency);
    }

    private double getAverage(ExcellentCurrency currency) {
        double total = getTopManager().getTotalBalance(currency);
        int entries = getTopManager().getTopEntries(currency).size();

        return entries == 0 ? 0 : total / entries;
    }

    @TableProvider(
            tableColor = Color.GREEN
    )
    public Table currencies() {
        ExcellentEconomyAPI api = getAPI();

        Table.Factory table = Table.builder()
                .columnOne("Currency", Icon.called("coins").build())
                .columnTwo("Total", Icon.called("wallet").build())
                .columnThree("Average", Icon.called("calculator").build());

        for (ExcellentCurrency currency : api.getCurrencies()) {
            table.addRow(
                    currency.getName(),
                    String.valueOf(getTotal(currency)),
                    String.valueOf(getAverage(currency))
            );
        }

        return table.build();
    }

    @DataBuilderProvider
    public ExtensionDataBuilder economy(UUID playerUUID) {
        ExcellentEconomyAPI api = getAPI();
        ExtensionDataBuilder builder = newExtensionDataBuilder();

        for (ExcellentCurrency currency : api.getCurrencies()) {
            String name = currency.getName();

            builder.addValue(
                    Double.class,
                    builder.valueBuilder(name)
                            .description("Player's " + name + " balance")
                            .icon("coins", Family.SOLID, Color.GREEN)
                            .showOnTab("Economy")
                            .buildDouble(() -> api.getCachedUserData(playerUUID).get().getBalance(currency))
            );
        }
        return builder;
    }
}
