package me.droreo002.chestshopconfirmation.commands.arg;

import me.droreo002.chestshopconfirmation.ChestShopConfirmation;
import me.droreo002.chestshopconfirmation.config.PluginConfig;
import me.droreo002.oreocore.commands.CommandArg;
import me.droreo002.oreocore.commands.CustomCommand;
import org.bukkit.command.CommandSender;

public class ReloadCommand extends CommandArg {

    private final ChestShopConfirmation plugin;
    private final PluginConfig config;

    public ReloadCommand(CustomCommand parent, PluginConfig config, ChestShopConfirmation plugin) {
        super("reload", parent);
        this.plugin = plugin;
        this.config = config;

        setPermission("csc.admin", config.getMsgNoPermission());
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        plugin.getPluginConfig().reloadConfig();
        plugin.getMainCommand().reload();
        sendMessage(commandSender, config.getMsgConfigReloaded());
        success(commandSender);
    }
}
