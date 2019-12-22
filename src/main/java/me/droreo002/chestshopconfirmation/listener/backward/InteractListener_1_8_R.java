package me.droreo002.chestshopconfirmation.listener.backward;

import me.droreo002.chestshopconfirmation.ChestShopConfirmation;
import me.droreo002.chestshopconfirmation.config.PluginConfig;
import me.droreo002.oreocore.utils.item.complex.UMaterial;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

public class InteractListener_1_8_R implements OnInteractHandler {

    @Override
    public void onInteract(PlayerInteractEvent event) {
        final ChestShopConfirmation plugin = ChestShopConfirmation.getInstance();
        final PluginConfig memory = plugin.getPluginConfig();
        if (!memory.isEnableConfirmation()) return;
        if (event.getClickedBlock() == null) return;
        final Material clickType = event.getClickedBlock().getType();
        if (!clickType.equals(UMaterial.CHEST.getMaterial())) return;
        final Player player = event.getPlayer();
        final Block block = event.getClickedBlock();

        if (plugin.getShopOnUse().contains(block.getLocation())) {
            plugin.sendMessage(player, memory.getMsgCantOpenChest());
            event.setCancelled(true);
        }
    }
}
