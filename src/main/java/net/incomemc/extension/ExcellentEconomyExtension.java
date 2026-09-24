package net.incomemc.extension;

import com.djrapitops.plan.extension.*;
import com.djrapitops.plan.extension.annotation.*;
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

import java.text.NumberFormat;
import java.util.Locale;
import java.util.UUID;

@PluginInfo(
        name = "ExcellentEconomy",
        iconName = "money-bill-1",
        iconFamily = Family.SOLID,
        color = Color.GREEN
)
/*
@TabInfo(
        tab = "Economy",
        iconName = "coins",
        iconFamily = Family.SOLID,
        elementOrder = {
                ElementOrder.VALUES,
                ElementOrder.TABLE
        }
)
@TabOrder({"Economy"})
 */
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

    public CallEvents[] callExtensionMethodsOn() {
        return new CallEvents[]{
                CallEvents.SERVER_EXTENSION_REGISTER,
                CallEvents.SERVER_PERIODICAL,
                CallEvents.PLAYER_PERIODICAL,
                CallEvents.PLAYER_LEAVE,
                CallEvents.PLAYER_JOIN
        };
    }

    private double getTotal(ExcellentCurrency currency) {
        return getTopManager().getTotalBalance(currency);
    }

    private double getAverage(ExcellentCurrency currency) {
        double total = getTopManager().getTotalBalance(currency);
        int entries = getTopManager().getTopEntries(currency).size();

        return entries == 0 ? 0 : total / entries;
    }

    private String formatBalance(double balance) {
        NumberFormat format = NumberFormat.getCompactNumberInstance(
                Locale.US,
                NumberFormat.Style.SHORT
        );

        format.setMaximumFractionDigits(2);

        return format.format(balance);
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
                    formatBalance(getTotal(currency)),
                    formatBalance(getAverage(currency))
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
                    String.class,
                    builder.valueBuilder(name)
                            .description("Player's " + name + " balance")
                            .icon("coins", Family.SOLID, Color.GREEN)
                            .buildString(() -> formatBalance(api.getCachedUserData(playerUUID).get().getBalance(currency)))
            );
        }
        return builder;
    }
}
