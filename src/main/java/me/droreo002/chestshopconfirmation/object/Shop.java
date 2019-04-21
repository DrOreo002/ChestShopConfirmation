package me.droreo002.chestshopconfirmation.object;

import com.Acrobot.ChestShop.Events.TransactionEvent;
import lombok.Getter;
import org.bukkit.block.Sign;
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
}
