package me.droreo002.chestshopconfirmation.listener;

import com.Acrobot.ChestShop.Events.PreTransactionEvent;
import com.Acrobot.ChestShop.Events.TransactionEvent;
import me.droreo002.chestshopconfirmation.ChestShopConfirmation;
import me.droreo002.chestshopconfirmation.config.ConfigManager;
import me.droreo002.chestshopconfirmation.inventory.ConfirmationInventory;
import me.droreo002.chestshopconfirmation.inventory.InformationInventory;
import me.droreo002.chestshopconfirmation.object.Shop;
import me.droreo002.oreocore.enums.XMaterial;
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
        final int amount = Integer.valueOf(sign.getLine(1));
        final double price = event.getPrice();
        final ItemStack item = event.getStock()[0];
        final TransactionEvent.TransactionType type = event.getTransactionType();
        final Player client = event.getClient();

        /*
        TODO : Ability to not use confirmation. Next update
         */

        event.setCancelled(PreTransactionEvent.TransactionOutcome.OTHER);

        if (price < 0) {
            new InformationInventory(memory, type).openAsync(client);  // Just in case if there's a textured head
            return;
        }
        final Shop shop = new Shop(sign, owner, amount, item, price, type);
        new ConfirmationInventory(client, memory, shop, event).openAsync(client); // Just in case if there's a textured head
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        final ConfigManager.Memory memory = plugin.getConfigManager().getMemory();
        if (!memory.isEnableConfirmation()) return;
        if (event.getHand() == EquipmentSlot.OFF_HAND) return;
        if (event.getClickedBlock() == null) return;
        final Material clickType = event.getClickedBlock().getType();
        if (!clickType.equals(XMaterial.CHEST.parseMaterial())
                && !clickType.equals(XMaterial.SIGN.parseMaterial())
                && !clickType.equals(XMaterial.WALL_SIGN.parseMaterial())) return;
        final Player player = event.getPlayer();
        final Block block = event.getClickedBlock();

        if (plugin.getShopOnUse().contains(block.getLocation())) {
            plugin.sendMessage(player, memory.getMsgCantOpenChest());
            event.setCancelled(true);
        }
    }
}
