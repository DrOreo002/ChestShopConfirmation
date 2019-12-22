package me.droreo002.chestshopconfirmation.commands.arg;

import me.droreo002.chestshopconfirmation.ChestShopConfirmation;
import me.droreo002.chestshopconfirmation.config.PluginConfig;
import me.droreo002.chestshopconfirmation.database.PlayerData;
import me.droreo002.oreocore.commands.CommandArg;
import me.droreo002.oreocore.commands.CustomCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ToggleCommand extends CommandArg {

    private final PluginConfig memory;
    private final ChestShopConfirmation plugin;

    public ToggleCommand(CustomCommand parent, PluginConfig memory, ChestShopConfirmation plugin) {
        super("toggle", parent);
        this.memory = memory;
        this.plugin = plugin;

        setPlayerOnly(true, memory.getMsgPlayerOnly());
        setPermission("csc.toggle", memory.getMsgNoPermission());
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        final Player player = (Player) commandSender;
        final PlayerData playerData = plugin.getPlayerDatabase().getPlayerData(player.getUniqueId());

        if (playerData.isConfirmationDisabled()) {
            playerData.setConfirmationDisabled(false);
            sendMessage(commandSender, memory.getMsgConfirmationEnabledSelf());
        } else {
            playerData.setConfirmationDisabled(true);
            sendMessage(commandSender, memory.getMsgConfirmationDisabledSelf());
        }
        success(commandSender);
        playerData.update();
    }
}
