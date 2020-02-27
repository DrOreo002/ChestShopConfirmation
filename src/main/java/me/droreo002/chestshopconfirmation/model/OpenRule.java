package me.droreo002.chestshopconfirmation.model;

import lombok.Getter;
import lombok.Setter;
import me.droreo002.chestshopconfirmation.ChestShopConfirmation;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

public class OpenRule {

    @Getter @Setter
    private String world;
    @Getter @Setter
    private List<TransactionType> transactionTypes;

    public OpenRule(String syntax) {
        if (!syntax.contains(":")) throw new IllegalStateException("Invalid OpenRule syntax! (" + syntax + ")");
        String[] sp = syntax.split(":");
        if (sp.length != 2) throw new IllegalStateException("Invalid OpenRule syntax! (" + syntax + ")");
        this.world = sp[0];
        this.transactionTypes = new ArrayList<>();
        for (String s : sp[1].split(",")) {
            this.transactionTypes.add(TransactionType.tryGet(s));
        }
    }

    /**
     * Check if this is a universal transaction type
     * or contains ALL
     *
     * @return True if universal, false otherwise
     */
    public boolean isUniversal() {
        return transactionTypes.contains(TransactionType.ALL);
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
