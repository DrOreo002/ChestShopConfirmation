package me.droreo002.chestshopconfirmation.inventory;

import com.Acrobot.ChestShop.Events.PreTransactionEvent;
import com.Acrobot.ChestShop.Events.TransactionEvent;
import com.sun.org.apache.bcel.internal.generic.LOR;
import me.droreo002.chestshopconfirmation.ChestShopConfirmation;
import me.droreo002.chestshopconfirmation.config.ConfigManager;
import me.droreo002.chestshopconfirmation.object.Shop;
import me.droreo002.oreocore.inventory.api.CustomInventory;
import me.droreo002.oreocore.inventory.api.GUIButton;
import me.droreo002.oreocore.utils.bridge.ServerUtils;
import me.droreo002.oreocore.utils.item.CustomItem;
import me.droreo002.oreocore.utils.item.ItemUtils;
import me.droreo002.oreocore.utils.item.helper.ItemMetaType;
import me.droreo002.oreocore.utils.item.helper.TextPlaceholder;
import me.droreo002.oreocore.utils.list.ListUtils;
import me.droreo002.oreocore.utils.strings.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Sign;

import static me.droreo002.oreocore.utils.item.CustomItem.fromSection;
import static me.droreo002.oreocore.utils.strings.StringUtils.color;

public class ConfirmationInventory extends CustomInventory {

    private final PreTransactionEvent preTransactionEvent;
    private final ChestShopConfirmation plugin;

    public ConfirmationInventory(final Player player, final ConfigManager.Memory memory, final Shop shop, final PreTransactionEvent event) {
        super(27, memory.getIConfirmTitle());
        this.plugin = ChestShopConfirmation.getInstance();
        this.preTransactionEvent = event;

        final ItemStack shopItem = shop.getItem().clone();

        setSoundOnClick(memory.getConfirmClickSound());
        setSoundOnOpen(memory.getConfirmOpenSound());
        setSoundOnClose(memory.getConfirmCloseSound());

        final TextPlaceholder placeholder = new TextPlaceholder(ItemMetaType.LORE, "%shop_owner%", shop.getOwner())
                .add(ItemMetaType.LORE, "%item_amount%", String.valueOf(shop.getAmount()))
                .add(ItemMetaType.LORE, "%currency_symbol%", memory.getCurrencySymbol())
                .add(ItemMetaType.LORE, "%item%", shop.getItem().getType().toString())
                .add(ItemMetaType.LORE, "%transaction_type%", color(shop.getShopTypeAsString()));
        if (memory.isEnablePriceFormat()) {
            placeholder.add(ItemMetaType.LORE, "%price%", StringUtils.formatToReadable(new Double(shop.getPrice()).longValue(), memory.getPriceFormat()));
        } else {
            placeholder.add(ItemMetaType.LORE, "%price%", Double.toString(shop.getPrice()));
        }

        final ItemStack fillItem = fromSection(memory.getIConfirmFillItem(), null);
        final ItemStack acceptButton = fromSection(memory.getIConfirmAcceptButton(), placeholder);
        final ItemStack declineButton = fromSection(memory.getIConfirmDeclineButton(), placeholder);

        if (memory.isIConfirmFillEmpty()) addBorder(new int[] { 0, 1, 2 }, fillItem, false);

        if (memory.isIConfirmEnablePreview()) {
            final TextPlaceholder previewPlaceholder = new TextPlaceholder(ItemMetaType.DISPLAY_NAME, "%item_name%", ItemUtils.getName(shopItem, false));
            if (shopItem.hasItemMeta()) {
                if (shopItem.getItemMeta().hasLore()) {
                    previewPlaceholder.add(ItemMetaType.LORE, "%item_lore%", shopItem.getItemMeta().getLore());
                } else {
                    previewPlaceholder.add(ItemMetaType.LORE, "%item_lore%", "");
                }
            } else {
                previewPlaceholder.add(ItemMetaType.LORE, "%item_lore%", "");
            }
            previewPlaceholder.addAll(placeholder);
            ItemStack previewButton = fromSection(memory.getIConfirmPreviewButton(), previewPlaceholder);
            previewButton.setType(shopItem.getType()); // Because the default is AIR
            addButton(memory.getIConfirmPreviewButtonSlot(), new GUIButton(previewButton).setListener(GUIButton.CLOSE_LISTENER), true);
        }

        addButton(memory.getIConfirmAcceptButtonSlot(), new GUIButton(acceptButton).setListener(inventoryClickEvent -> {
            close(player);
            TransactionEvent toCall = new TransactionEvent(event, shop.getSign());
            ServerUtils.callEvent(toCall);

            // Remove chest on use
            Sign sign = (Sign) event.getSign().getData();
            Block attached = event.getSign().getBlock().getRelative(sign.getAttachedFace());

            // Remove sign and block
            plugin.getShopOnUse().remove(attached.getLocation());
            plugin.getShopOnUse().remove(preTransactionEvent.getSign().getLocation());
        }), true);
        addButton(memory.getIConfirmDeclineButtonSlot(), new GUIButton(declineButton).setListener(GUIButton.CLOSE_LISTENER), true);
    }

    @Override
    public void onClick(InventoryClickEvent inventoryClickEvent) {

    }

    @Override
    public void onClose(InventoryCloseEvent inventoryCloseEvent) {
        // Remove chest on use
        Sign sign = (Sign) preTransactionEvent.getSign().getData();
        Block attached = preTransactionEvent.getSign().getBlock().getRelative(sign.getAttachedFace());

        // Remove sign and block
        plugin.getShopOnUse().remove(attached.getLocation());
        plugin.getShopOnUse().remove(preTransactionEvent.getSign().getLocation());
    }

    @Override
    public void onOpen(InventoryOpenEvent inventoryOpenEvent) {
        // Add to chest on use
        Sign sign = (Sign) preTransactionEvent.getSign().getData();
        Block attached = preTransactionEvent.getSign().getBlock().getRelative(sign.getAttachedFace());
        if (plugin.getShopOnUse().contains(attached.getLocation())) return;
        if (plugin.getShopOnUse().contains(preTransactionEvent.getSign().getLocation())) return;

        // Add sign, and block
        plugin.getShopOnUse().add(attached.getLocation());
        plugin.getShopOnUse().add(preTransactionEvent.getSign().getLocation());
    }
}
