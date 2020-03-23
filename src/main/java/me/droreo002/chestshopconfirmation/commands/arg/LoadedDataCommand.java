package me.droreo002.chestshopconfirmation.commands.arg;

import me.droreo002.chestshopconfirmation.ChestShopConfirmation;
import me.droreo002.chestshopconfirmation.config.PluginConfig;
import me.droreo002.oreocore.commands.CommandArg;
import me.droreo002.oreocore.commands.CustomCommand;
import me.droreo002.oreocore.database.object.DatabaseFlatFile;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Set;

import static me.droreo002.oreocore.utils.strings.StringUtils.color;

public class LoadedDataCommand extends CommandArg {

    private final ChestShopConfirmation plugin;

    public LoadedDataCommand(CustomCommand parent, PluginConfig memory, ChestShopConfirmation plugin) {
        super("loaded-data", parent);
        this.plugin = plugin;

        setPermission("csc.admin", memory.getMsgNoPermission());
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        sendMessage(commandSender, "Loaded datas: ");
        // Only take the previews
        List<DatabaseFlatFile.DataCache> values = plugin.getPlayerDatabase().getDataCaches();
        if (values.isEmpty()) {
            commandSender.sendMessage(color("    &7> &fNo loaded data found"));
            return;
        }
        for (int i = 0; i < 15; i++) {
            if (i >= values.size()) break;
            DatabaseFlatFile.DataCache data = (DatabaseFlatFile.DataCache) values.toArray()[i];
            commandSender.sendMessage(color("     &7> &f" + data.getDataFile().getName()));
        }
    }
}
