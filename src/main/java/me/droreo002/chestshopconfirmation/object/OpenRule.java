package me.droreo002.chestshopconfirmation.object;

import lombok.Getter;
import lombok.Setter;
import me.droreo002.chestshopconfirmation.ChestShopConfirmation;
import org.bukkit.configuration.ConfigurationSection;

public class OpenRule {

    @Getter @Setter
    private String world;
    @Getter @Setter
    private TransactionType transactionType;

    public OpenRule(String syntax) {
        if (!syntax.contains(":")) throw new IllegalStateException("Invalid OpenRule syntax! (" + syntax + ")");
        String[] sp = syntax.split(":");
        if (sp.length != 2) throw new IllegalStateException("Invalid OpenRule syntax! (" + syntax + ")");
        this.world = sp[0];
        this.transactionType = TransactionType.tryGet(sp[1]);
    }

    public enum TransactionType {
        BUY,
        SELL,
        SELL_STACK,
        BUY_STACK,
        ALL;

        public static TransactionType tryGet(String s) {
            try {
                return valueOf(s);
            } catch (Exception e) {
                throw new NullPointerException("Failed to get TransactionType with the ID of " + s);
            }
        }

        public String asTranslatedString() {
            final ConfigurationSection cs = ChestShopConfirmation.getInstance().getPluginConfig().getTransactionTypeTranslation();
            if (cs.getString(name()) == null) throw new NullPointerException("Failed to get shop type's translation (" + name() + "). Please check the config!");
            return cs.getString(name());
        }
    }
}
