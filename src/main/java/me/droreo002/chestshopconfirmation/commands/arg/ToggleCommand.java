package me.droreo002.chestshopconfirmation.commands.arg;

import me.droreo002.chestshopconfirmation.config.ConfigManager;
import me.droreo002.oreocore.commands.CommandArg;
import me.droreo002.oreocore.commands.CustomCommand;
import org.bukkit.command.CommandSender;

public class ToggleCommand extends CommandArg {

    private final ConfigManager.Memory memory;

    public ToggleCommand(CustomCommand parent, ConfigManager.Memory memory) {
        super("toggle", parent);
        this.memory = memory;

        setPermission("csc.admin", memory.getMsgNoPermission());
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        if (memory.isEnableConfirmation()) {
            memory.setEnableConfirmation(false);
            sendMessage(commandSender, memory.getMsgConfirmationDisabled());
        } else {
            memory.setEnableConfirmation(true);
            sendMessage(commandSender, memory.getMsgConfirmationEnabled());
        }
        success(commandSender);
    }
}
