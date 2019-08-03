package me.droreo002.chestshopconfirmation.listener;

import com.Acrobot.ChestShop.Events.PreTransactionEvent;
import com.Acrobot.ChestShop.Events.TransactionEvent;
import com.Acrobot.ChestShop.Signs.ChestShopSign;
import me.droreo002.chestshopconfirmation.ChestShopConfirmation;
import me.droreo002.chestshopconfirmation.config.ConfigManager;
import me.droreo002.chestshopconfirmation.database.PlayerData;
import me.droreo002.chestshopconfirmation.enums.ClickRequestType;
import me.droreo002.chestshopconfirmation.inventory.ConfirmationInventory;
import me.droreo002.chestshopconfirmation.inventory.InformationInventory;
import me.droreo002.chestshopconfirmation.listener.backward.InteractListener;
import me.droreo002.chestshopconfirmation.listener.backward.InteractListener_1_8_R;
import me.droreo002.chestshopconfirmation.listener.backward.OnInteractHandler;
import me.droreo002.chestshopconfirmation.object.OpenRule;
import me.droreo002.chestshopconfirmation.object.Shop;
import me.droreo002.oreocore.enums.MinecraftVersion;
import me.droreo002.oreocore.enums.XMaterial;
import me.droreo002.oreocore.utils.bridge.ServerUtils;
import me.droreo002.oreocore.utils.item.complex.UMaterial;
import me.droreo002.oreocore.utils.misc.ThreadingUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.List;

import static me.droreo002.oreocore.utils.strings.StringUtils.color;

public class CoreListener implements Listener {

    private final ChestShopConfirmation plugin;

    public CoreListener(ChestShopConfirmation plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPre(PreTransactionEvent event) {
        final ConfigManager.Memory memory = plugin.getConfigManager().getMemory();
        if (!memory.isEnableConfirmation()) return;

        final Player client = event.getClient();
        final Sign sign = event.getSign();
        final PlayerData playerData = plugin.getPlayerDatabase().getPlayerData(client.getUniqueId());
        if (playerData == null) return;

        // Cant put this on Interact listener, since ChestShop need to process the interact listener first.
        if (plugin.getOnClickRequest().containsKey(client.getUniqueId())) {
            final ClickRequestType type = plugin.getOnClickRequest().get(client.getUniqueId());
            final Location location = sign.getLocation();

            if (type == ClickRequestType.ENABLE_SHOP) {
                final List<Location> disabledShop = playerData.getDisabledShops();
                disabledShop.removeIf(loc -> loc.equals(location));
                client.sendMessage(color(memory.getPrefix() + memory.getMsgShopEnabled()));
            }
            if (type == ClickRequestType.DISABLE_SHOP) {
                final List<Location> disabledShop = playerData.getDisabledShops();
                if (disabledShop.contains(location)) return;
                disabledShop.add(location);
                client.sendMessage(color(memory.getPrefix() + memory.getMsgShopDisabled()));
            }
            plugin.getOnClickRequest().remove(client.getUniqueId());
            playerData.update();
            event.setCancelled(PreTransactionEvent.TransactionOutcome.OTHER);
            return;
        }

        if (playerData.isConfirmationDisabled()) return;
        if (playerData.getDisabledShops().contains(event.getSign().getLocation())) return;
        if (event.isCancelled()) return;

        final String owner = sign.getLine(0);
        final double price = event.getPrice();
        final ItemStack item = event.getStock()[0];
        OpenRule.TransactionType transactionType = OpenRule.TransactionType.tryGet(event.getTransactionType().name());
        if (transactionType == null) throw new NullPointerException("Failed  to get Transaction type!. Please report this to devs!");
        if (transactionType == OpenRule.TransactionType.SELL && item.getAmount() == item.getMaxStackSize()) transactionType = OpenRule.TransactionType.SELL_STACK;
        if (transactionType == OpenRule.TransactionType.BUY && item.getAmount() == item.getMaxStackSize()) transactionType = OpenRule.TransactionType.BUY_STACK;
        int amount = 0;
        for (ItemStack i : event.getStock()) {
            amount += i.getAmount();
        }
        final List<OpenRule> openRules = memory.getOpenRule();

        boolean cancel = false;
        // Check for 'all' first
        for (OpenRule rule : openRules) {
            if (rule.getWorld().equals("all")) {
                if (rule.getTransactionType() == OpenRule.TransactionType.ALL) break;
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
                if (rule.getTransactionType() == OpenRule.TransactionType.ALL) continue;
                if (!rule.getTransactionType().equals(transactionType)) cancel = true;
            }
        }

        // Change if the transaction type is ALL
        if (transactionType == OpenRule.TransactionType.ALL) {
            switch (event.getTransactionType()) {
                case BUY:
                    transactionType = OpenRule.TransactionType.BUY;
                    break;
                case SELL:
                    transactionType = OpenRule.TransactionType.SELL;
                    break;
            }
        }

        if (cancel) {
            // Make ChestShop handle the transaction
            return;
        } else {
            // Continue confirmation and cancel the main event
            event.setCancelled(PreTransactionEvent.TransactionOutcome.OTHER);
        }

        if (price < 0) {
            final OpenRule.TransactionType finalTransactionType = transactionType;
            ThreadingUtils.makeChain().asyncFirst(() -> new InformationInventory(memory, finalTransactionType)).asyncLast(input -> input.open(client)).execute();
            return;
        }

        final Shop shop = new Shop(sign, owner, amount, item, price, transactionType);
        ThreadingUtils.makeChain().asyncFirst(() -> new ConfirmationInventory(client, memory, shop, event)).asyncLast(input -> input.open(client)).execute();
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onInteract(PlayerInteractEvent event) {
        final MinecraftVersion version = ServerUtils.getServerVersion();
        OnInteractHandler handler;
        if (version.name().contains("1_8")) {
            handler = new InteractListener_1_8_R();
        } else {
            handler = new InteractListener();
        }
        handler.onInteract(event);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        plugin.getPlayerDatabase().registerPlayerData(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        plugin.getPlayerDatabase().unregisterPlayerData(e.getPlayer().getUniqueId(), false);
    }
}
