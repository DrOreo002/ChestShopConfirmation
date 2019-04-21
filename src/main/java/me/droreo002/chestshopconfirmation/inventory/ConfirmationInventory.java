package me.droreo002.chestshopconfirmation.inventory;

import com.Acrobot.ChestShop.Events.PreTransactionEvent;
import com.Acrobot.ChestShop.Events.TransactionEvent;
import me.droreo002.chestshopconfirmation.config.ConfigManager;
import me.droreo002.chestshopconfirmation.object.Shop;
import me.droreo002.oreocore.inventory.api.CustomInventory;
import me.droreo002.oreocore.inventory.api.GUIButton;
import me.droreo002.oreocore.utils.bridge.ServerUtils;
import me.droreo002.oreocore.utils.item.CustomItem;
import me.droreo002.oreocore.utils.item.helper.ItemMetaType;
import me.droreo002.oreocore.utils.item.helper.TextPlaceholder;
import me.droreo002.oreocore.utils.strings.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

import static me.droreo002.oreocore.utils.item.CustomItem.fromSection;

public class ConfirmationInventory extends CustomInventory {

    public ConfirmationInventory(final Player player, final ConfigManager.Memory memory, final Shop shop, final PreTransactionEvent event) {
        super(27, memory.getIConfirmTitle());

        setSoundOnClick(memory.getConfirmClickSound());
        setSoundOnOpen(memory.getConfirmOpenSound());
        setSoundOnClose(memory.getConfirmCloseSound());

        final TextPlaceholder placeholder = new TextPlaceholder(ItemMetaType.LORE, "%shop_owner%", shop.getOwner())
                .add(ItemMetaType.LORE, "%item_amount%", String.valueOf(shop.getAmount()))
                .add(ItemMetaType.LORE, "%currency_symbol%", memory.getCurrencySymbol())
                .add(ItemMetaType.LORE, "%price%", Double.toString(shop.getPrice()))
                .add(ItemMetaType.LORE, "%item%", shop.getItem().getType().toString())
                .add(ItemMetaType.LORE, "%transaction_type%", StringUtils.upperCaseFirstLetter(shop.getShopType().toString().toLowerCase()));

        final ItemStack fillItem = fromSection(memory.getIConfirmFillItem(), null);
        final ItemStack acceptButton = fromSection(memory.getIConfirmAcceptButton(), placeholder);
        final ItemStack declineButton = fromSection(memory.getIConfirmDeclineButton(), null);

        if (memory.isIConfirmFillEmpty()) addBorder(new int[] { 0, 1, 2 }, fillItem, false);


        addButton(memory.getIConfirmAcceptButtonSlot(), new GUIButton(acceptButton).setListener(inventoryClickEvent -> {
            close(player);
            TransactionEvent toCall = new TransactionEvent(event, shop.getSign());
            ServerUtils.callEvent(toCall);
        }), true);
        addButton(memory.getIConfirmDeclineButtonSlot(), new GUIButton(declineButton).setListener(inventoryClickEvent -> close(player)), true);
    }

    @Override
    public void onClick(InventoryClickEvent inventoryClickEvent) {

    }

    @Override
    public void onClose(InventoryCloseEvent inventoryCloseEvent) {

    }

    @Override
    public void onOpen(InventoryOpenEvent inventoryOpenEvent) {

    }
}
