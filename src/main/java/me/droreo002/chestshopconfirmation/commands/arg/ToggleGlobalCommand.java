package me.droreo002.chestshopconfirmation.commands.arg;

import me.droreo002.chestshopconfirmation.config.ConfigManager;
import me.droreo002.oreocore.commands.CommandArg;
import me.droreo002.oreocore.commands.CustomCommand;
import org.bukkit.command.CommandSender;

public class ToggleGlobalCommand extends CommandArg {

    private final ConfigManager.Memory memory;

    public ToggleGlobalCommand(CustomCommand parent, ConfigManager.Memory memory) {
        super("toggle-global", parent);
        this.memory = memory;

        setPermission("csc.admin", memory.getPrefix() + memory.getMsgNoPermission());
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        if (memory.isEnableConfirmation()) {
            memory.setEnableConfirmation(false);
            sendMessage(commandSender, memory.getMsgConfirmationDisabledGlobal());
        } else {
            memory.setEnableConfirmation(true);
            sendMessage(commandSender, memory.getMsgConfirmationEnabledGlobal());
        }
        success(commandSender);
    }
}
