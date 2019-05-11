package me.droreo002.chestshopconfirmation.object;

import com.Acrobot.ChestShop.Events.TransactionEvent;
import lombok.Getter;
import me.droreo002.chestshopconfirmation.ChestShopConfirmation;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class Shop {

    @Getter
    private final Sign sign;
    @Getter
    private final String owner;
    @Getter
    private final int amount;
    @Getter
    private final ItemStack item;
    @Getter
    private final double price;
    @Getter
    private final TransactionEvent.TransactionType shopType;

    public Shop(Sign sign, String owner, int amount, ItemStack item, double price, TransactionEvent.TransactionType shopType) {
        this.sign = sign;
        this.owner = owner;
        this.amount = amount;
        this.item = item;
        this.price = price;
        this.shopType = shopType;
    }

    public String getShopTypeAsString() {
        final ConfigurationSection cs = ChestShopConfirmation.getInstance().getConfigManager().getMemory().getTransactionTypeTranslation();
        switch (shopType) {
            case BUY:
                return cs.getString(TransactionEvent.TransactionType.BUY.name());
            case SELL:
                return cs.getString(TransactionEvent.TransactionType.SELL.name());
            default:
                return "INVALID STRING, PLEASE CHECK CONFIG";
        }
    }
}
