package me.droreo002.chestshopconfirmation.commands.arg;

import me.droreo002.chestshopconfirmation.ChestShopConfirmation;
import me.droreo002.chestshopconfirmation.config.ConfigManager;
import me.droreo002.chestshopconfirmation.enums.ClickRequestType;
import me.droreo002.oreocore.commands.CommandArg;
import me.droreo002.oreocore.commands.CustomCommand;
import me.droreo002.oreocore.utils.item.helper.TextPlaceholder;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DisableShopCommand extends CommandArg {

    private final ChestShopConfirmation plugin;
    private final ConfigManager.Memory memory;

    public DisableShopCommand(CustomCommand parent, ConfigManager.Memory memory, ChestShopConfirmation plugin) {
        super("disable-shop", parent);
        this.plugin = plugin;
        this.memory = memory;

        setPlayerOnly(true, "Player only command!");
        setPermission("csc.enable-disable-shop", memory.getMsgNoPermission());
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        final Player player = (Player) commandSender;
        plugin.getOnClickRequest().remove(player.getUniqueId()); // Remove if already added. No need if check here
        plugin.getOnClickRequest().put(player.getUniqueId(), ClickRequestType.DISABLE_SHOP);

        int timeOut = memory.getRightClickTimeout();
        final TextPlaceholder placeholder = new TextPlaceholder("%time", String.valueOf(timeOut));
        sendMessage(commandSender, placeholder.format(memory.getMsgRClickToDisable()));
        success(commandSender);

        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            if (plugin.getOnClickRequest().containsKey(player.getUniqueId())) {
                plugin.getOnClickRequest().remove(player.getUniqueId());
                sendMessage(commandSender, memory.getMsgTimeOut());
                success(commandSender);
            }
        }, 20L * timeOut);
    }
}
