package me.droreo002.chestshopconfirmation.commands.arg;

import me.droreo002.chestshopconfirmation.ChestShopConfirmation;
import me.droreo002.chestshopconfirmation.config.ConfigManager;
import me.droreo002.oreocore.commands.CommandArg;
import me.droreo002.oreocore.commands.CustomCommand;
import org.bukkit.command.CommandSender;

public class ReloadCommand extends CommandArg {

    private final ChestShopConfirmation plugin;
    private final ConfigManager.Memory memory;

    public ReloadCommand(CustomCommand parent, ConfigManager.Memory memory, ChestShopConfirmation plugin) {
        super("reload", parent);
        this.plugin = plugin;
        this.memory = memory;

        setPermission("csc.admin", memory.getPrefix() + memory.getMsgNoPermission());
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        plugin.getConfigManager().reloadConfig();
        sendMessage(commandSender, memory.getMsgConfigReloaded());
        success(commandSender);
    }
}
