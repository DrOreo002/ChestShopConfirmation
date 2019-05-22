package me.droreo002.chestshopconfirmation.listener;

import com.Acrobot.ChestShop.Events.PreTransactionEvent;
import com.Acrobot.ChestShop.Events.TransactionEvent;
import me.droreo002.chestshopconfirmation.ChestShopConfirmation;
import me.droreo002.chestshopconfirmation.config.ConfigManager;
import me.droreo002.chestshopconfirmation.inventory.ConfirmationInventory;
import me.droreo002.chestshopconfirmation.inventory.InformationInventory;
import me.droreo002.chestshopconfirmation.object.OpenRule;
import me.droreo002.chestshopconfirmation.object.Shop;
import me.droreo002.oreocore.enums.XMaterial;
import me.droreo002.oreocore.utils.item.complex.UMaterial;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class CoreListener implements Listener {

    private final ChestShopConfirmation plugin;

    public CoreListener(ChestShopConfirmation plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPre(PreTransactionEvent event) {
        final ConfigManager.Memory memory = plugin.getConfigManager().getMemory();
        if (!memory.isEnableConfirmation()) return;
        if (event.isCancelled()) return;

        final Sign sign = event.getSign();
        final String owner = sign.getLine(0);
        final double price = event.getPrice();
        final ItemStack item = event.getStock()[0];
        OpenRule.TransactionType transactionType = OpenRule.TransactionType.tryGet(event.getTransactionType().name());
        if (transactionType == null) throw new NullPointerException("Failed  to get Transaction type!. Please report this to devs!");
        if (transactionType == OpenRule.TransactionType.SELL && item.getAmount() == item.getMaxStackSize()) transactionType = OpenRule.TransactionType.SELL_STACK;
        if (transactionType == OpenRule.TransactionType.BUY && item.getAmount() == item.getMaxStackSize()) transactionType = OpenRule.TransactionType.BUY_STACK;
        final Player client = event.getClient();
        final int amount = item.getAmount();
        final List<OpenRule> openRules = memory.getOpenRule();

        boolean cancel = false;
        // Check for 'all' first
        for (OpenRule rule : openRules) {
            if (rule.getWorld().equals("all")) {
                if (rule.getTransactionType() == OpenRule.TransactionType.DISABLED) {
                    cancel = true;
                    break;
                }
                if (rule.getTransactionType() == OpenRule.TransactionType.BOTH) break;
                if (!rule.getTransactionType().equals(transactionType)) {
                    cancel = true;
                    break;
                }
                break;
            }
        }

        // Try to check on other
        for (OpenRule rule : openRules) {
            if (rule.getWorld().equals(client.getWorld().getName())) {
                if (rule.getTransactionType() == OpenRule.TransactionType.DISABLED) cancel = true;
                if (rule.getTransactionType() == OpenRule.TransactionType.BOTH) continue;
                if (!rule.getTransactionType().equals(transactionType)) cancel = true;
            }
        }

        /*
        TODO : Ability to not use confirmation. Next update
         */

        event.setCancelled(PreTransactionEvent.TransactionOutcome.OTHER);
        if (cancel) {
            plugin.sendMessage(client, memory.getMsgConfirmationWorldDisabled().replace("%shopType", transactionType.asTranslatedString()));
            return;
        }

        if (price < 0) {
            new InformationInventory(memory, transactionType).openAsync(client);  // Just in case if there's a textured head
            return;
        }

        final Shop shop = new Shop(sign, owner, amount, item, price, transactionType);
        new ConfirmationInventory(client, memory, shop, event).openAsync(client); // Just in case if there's a textured head
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        final ConfigManager.Memory memory = plugin.getConfigManager().getMemory();
        if (!memory.isEnableConfirmation()) return;
        if (event.getHand() == EquipmentSlot.OFF_HAND) return;
        if (event.getClickedBlock() == null) return;
        final Material clickType = event.getClickedBlock().getType();
        if (!clickType.equals(UMaterial.CHEST.getMaterial())
                && !clickType.equals(UMaterial.SIGN.getMaterial())
                && !clickType.equals(UMaterial.WALL_SIGN.getMaterial())) return;
        final Player player = event.getPlayer();
        final Block block = event.getClickedBlock();

        if (plugin.getShopOnUse().contains(block.getLocation())) {
            plugin.sendMessage(player, memory.getMsgCantOpenChest());
            event.setCancelled(true);
        }
    }
}
