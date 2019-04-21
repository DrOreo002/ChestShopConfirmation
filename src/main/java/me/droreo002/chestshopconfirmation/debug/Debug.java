package me.droreo002.chestshopconfirmation.debug;

import lombok.Getter;
import me.droreo002.chestshopconfirmation.ChestShopConfirmation;
import me.droreo002.oreocore.debugging.Debugger;
import me.droreo002.oreocore.debugging.LogFile;
import me.droreo002.oreocore.utils.strings.StringUtils;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class Debug extends Debugger {

    @Getter
    private final ChestShopConfirmation plugin;
    private final LogFile logFile;

    public Debug(ChestShopConfirmation plugin) {
        this.plugin = plugin;
        if (plugin.getConfigManager().getMemory().isEnableLogFile()) {
            this.logFile = new DebugFile(plugin);
        } else {
            this.logFile = null;
        }
    }

    @Override
    public LogFile getLogFile() {
        return logFile;
    }

    @Override
    public String getPrefix() {
        return "&7[ &bChestShopConfirmation &7]&f ";
    }

    @Override
    public boolean usePrefixLogFile() {
        return false;
    }


    private class DebugFile extends LogFile {

        DebugFile(JavaPlugin owner) {
            super(owner);
        }

        @Override
        public File getLogFolder() {
            return new File(ChestShopConfirmation.getInstance().getDataFolder(), "logs");
        }

        @Override
        public String getTimestampFormat() {
            return "dd/MM/yyyy";
        }

        @Override
        public String getLoggerName() {
            return StringUtils.color("&7[ &bChestShopConfirmation &7]&f ");
        }

        @Override
        public int getLogUpdateTime() {
            return 300;
        }
    }
}
