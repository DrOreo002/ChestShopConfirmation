package me.droreo002.chestshopconfirmation.commands.arg;

import me.droreo002.chestshopconfirmation.config.PluginConfig;
import me.droreo002.oreocore.commands.CommandArg;
import me.droreo002.oreocore.commands.CustomCommand;
import org.bukkit.command.CommandSender;

public class HelpCommand extends CommandArg {

    private final PluginConfig memory;

    public HelpCommand(CustomCommand parent, PluginConfig memory) {
        super("help", parent);
        this.memory = memory;

        setPermission("csc.admin", memory.getMsgNoPermission());
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        sendMessage(commandSender, "You can find information about this plugin on &7(&ehttps://github.com/DrOreo002/ChestShopConfirmation/wiki&7)");
    }
}
