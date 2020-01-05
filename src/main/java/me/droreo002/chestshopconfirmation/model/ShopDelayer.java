package me.droreo002.chestshopconfirmation.model;

import lombok.Getter;
import me.droreo002.chestshopconfirmation.ChestShopConfirmation;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ShopDelayer {

    @Getter
    private final List<UUID> onDelay;
    @Getter
    private ChestShopConfirmation plugin;

    public ShopDelayer(ChestShopConfirmation plugin) {
        this.onDelay = new ArrayList<>();
        this.plugin = plugin;
    }

    public void add(Player player) {
        if (isAdded(player)) return;
        onDelay.add(player.getUniqueId());
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> onDelay.remove(player.getUniqueId()), plugin.getPluginConfig().getConfirmationDelay() * 20);
    }

    public boolean isAdded(Player player) {
        return onDelay.contains(player.getUniqueId());
    }
}
