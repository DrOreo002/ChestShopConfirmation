package me.droreo002.chestshopconfirmation.inventory;

import com.Acrobot.Breeze.Utils.BlockUtil;
import com.Acrobot.ChestShop.Events.PreTransactionEvent;
import com.Acrobot.ChestShop.Events.TransactionEvent;
import me.droreo002.chestshopconfirmation.ChestShopConfirmation;
import me.droreo002.chestshopconfirmation.config.PluginConfig;
import me.droreo002.chestshopconfirmation.model.Shop;
import me.droreo002.oreocore.enums.MinecraftVersion;
import me.droreo002.oreocore.inventory.OreoInventory;
import me.droreo002.oreocore.inventory.button.GUIButton;
import me.droreo002.oreocore.utils.bridge.ServerUtils;
import me.droreo002.oreocore.utils.item.ItemUtils;
import me.droreo002.oreocore.utils.item.helper.ItemMetaType;
import me.droreo002.oreocore.utils.item.helper.TextPlaceholder;
import me.droreo002.oreocore.utils.multisupport.MinecraftFeature;
import me.droreo002.oreocore.utils.strings.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Sign;

import java.util.List;

import static me.droreo002.oreocore.utils.item.ItemStackBuilder.deserialize;
import static me.droreo002.oreocore.utils.strings.StringUtils.color;

public class ConfirmationInventory extends OreoInventory {

    private final PreTransactionEvent preTransactionEvent;
    private final ChestShopConfirmation plugin;

    @SuppressWarnings("deprecation")
    public ConfirmationInventory(final Player player, final PluginConfig memory, final Shop shop, final PreTransactionEvent event) {
        super(27);
        this.plugin = ChestShopConfirmation.getInstance();
        this.preTransactionEvent = event;

        final ItemStack shopItem = shop.getItem();

        // For some reason, it can be null...
        if (shopItem == null) {
            player.sendMessage(ChatColor.RED + "Cannot find the value for shopItem variable!. Please check your log and report to owner!");
            throw new NullPointerException("Cannot find the value for shopItem variable!. Please check your log and report to owner!");
        }

        setSoundOnClick(memory.getConfirmClickSound());
        setSoundOnOpen(memory.getConfirmOpenSound());
        setSoundOnClose(memory.getConfirmCloseSound());

        String emptyMsg = plugin.getPluginConfig().getMsgEmpty();
        List<String> lore = ItemUtils.getLore(shopItem, false);
        List<String> enchants = ItemUtils.getEnchantAsString(shopItem, true);
        List<String> flags = ItemUtils.getItemFlagAsString(shopItem);

        enchants.replaceAll(m -> {
            String[] ecData = m.split("\\|");
            return "   &7- " + ecData[0] + " &e[" + ecData[1] + "]";
        });
        flags.replaceAll(m -> "   &7- " + m);

        final TextPlaceholder placeholder = new TextPlaceholder(ItemMetaType.DISPLAY_AND_LORE, "%shop_owner%", shop.getOwner())
                .add(ItemMetaType.DISPLAY_AND_LORE,"%item_amount%", String.valueOf(shop.getAmount()))
                .add(ItemMetaType.DISPLAY_AND_LORE, "%currency_symbol%", memory.getCurrencySymbol())
                .add(ItemMetaType.DISPLAY_AND_LORE, "%item%", shop.getItem().getType().toString())
                .add(ItemMetaType.DISPLAY_AND_LORE, "%transaction_type%", color(shop.getShopType().asTranslatedString()))
                .add(ItemMetaType.DISPLAY_AND_LORE, "%item_name%", color(ItemUtils.getName(shopItem, true)))
                .add(ItemMetaType.DISPLAY_AND_LORE, "%item_lore%", (lore.isEmpty()) ? emptyMsg : lore)
                .add(ItemMetaType.DISPLAY_AND_LORE, "%item_enchants%", (enchants.isEmpty()) ? emptyMsg : enchants)
                .add(ItemMetaType.DISPLAY_AND_LORE, "%item_flags%", (flags.isEmpty()) ? emptyMsg : flags);

        if (memory.isEnablePriceFormat()) {
            placeholder.add(ItemMetaType.DISPLAY_AND_LORE, "%price%", StringUtils.formatToReadable(new Double(shop.getPrice()).longValue(), memory.getPriceFormat()));
        } else {
            placeholder.add(ItemMetaType.DISPLAY_AND_LORE, "%price%", Double.toString(shop.getPrice()));
        }

        setTitle(placeholder.format(memory.getIConfirmTitle()));
        final ItemStack fillItem = deserialize(memory.getIConfirmFillItem()).build();
        final ItemStack acceptButton = deserialize(memory.getIConfirmAcceptButton()).applyTextPlaceholder(placeholder).build();
        final ItemStack declineButton = deserialize(memory.getIConfirmDeclineButton()).applyTextPlaceholder(placeholder).build();

        if (memory.isIConfirmFillEmpty()) addBorder(fillItem, false, 0, 1, 2);

        if (memory.isIConfirmEnablePreview()) {
            ItemStack previewButton = deserialize(memory.getIConfirmPreviewButton()).applyTextPlaceholder(placeholder).build();
            previewButton.setType(shopItem.getType()); // Because the default is AIR
            previewButton.setAmount(shop.getAmount());

            /*
            Set custom model data
             */
            ItemMeta shopItemMeta = shopItem.getItemMeta();
            if (!ServerUtils.isVersionHas(MinecraftFeature.CUSTOM_MODEL_DATA)) {
                previewButton.setDurability(shopItem.getDurability());
            } else {
                ItemMeta previewButtonMeta = previewButton.getItemMeta();
                if (shopItemMeta.hasCustomModelData()) {
                    previewButtonMeta.setCustomModelData(shopItemMeta.getCustomModelData());
                }
                previewButton.setItemMeta(previewButtonMeta);
            }
            addButton(new GUIButton(previewButton, memory.getIConfirmPreviewButtonSlot()).addListener(GUIButton.CLOSE_LISTENER), true);
        }

        addButton(new GUIButton(acceptButton, memory.getIConfirmAcceptButtonSlot()).addListener(inventoryClickEvent -> {
            closeInventory(player);
            plugin.getShopDelayer().add(player);

            TransactionEvent toCall = new TransactionEvent(event, shop.getSign());
            ServerUtils.callEvent(toCall);

            Block attached = getAttached();
            if (attached == null) return;

            // Remove sign and block
            plugin.getShopOnUse().remove(attached.getLocation());
            plugin.getShopOnUse().remove(preTransactionEvent.getSign().getLocation());
        }), true);
        addButton(new GUIButton(declineButton, memory.getIConfirmDeclineButtonSlot()).addListener(GUIButton.CLOSE_LISTENER), true);
    }

    @Override
    public void onClose(InventoryCloseEvent inventoryCloseEvent) {
        // Add to chest on use
        Block attached = getAttached();
        if (attached == null) return;

        // Remove sign and block
        plugin.getShopOnUse().remove(attached.getLocation());
        plugin.getShopOnUse().remove(preTransactionEvent.getSign().getLocation());
    }

    @Override
    public void onOpen(InventoryOpenEvent inventoryOpenEvent) {
        // Add to chest on use
        Block attached = getAttached();
        if (attached == null) return; // FIXME: 03/08/2019 This might create duplication glitch
        if (plugin.getShopOnUse().contains(attached.getLocation())) return;
        if (plugin.getShopOnUse().contains(preTransactionEvent.getSign().getLocation())) return;

        // Add sign, and block
        plugin.getShopOnUse().add(attached.getLocation());
        plugin.getShopOnUse().add(preTransactionEvent.getSign().getLocation());
    }

    /**
     * Get attached block on sign (PreTransactionEvent)
     *
     * @return The attached block
     */
    private Block getAttached() {
        // Add to chest on use
        Block attached;
        if (!ServerUtils.isLegacyVersion()) {
            attached = BlockUtil.getAttachedBlock(preTransactionEvent.getSign());
        } else {
            Sign s = (Sign) preTransactionEvent.getSign().getData();
            attached = preTransactionEvent.getSign().getBlock().getRelative(s.getAttachedFace());
        }
        return attached;
    }
}
