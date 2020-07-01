package me.droreo002.chestshopconfirmation;

import com.Acrobot.ChestShop.Configuration.Properties;
import lombok.Getter;
import me.droreo002.chestshopconfirmation.bstats.Metrics;
import me.droreo002.chestshopconfirmation.commands.CShopConfirmationCommand;
import me.droreo002.chestshopconfirmation.config.PluginConfig;
import me.droreo002.chestshopconfirmation.database.PlayerDatabase;
import me.droreo002.chestshopconfirmation.debug.Debug;
import me.droreo002.chestshopconfirmation.enums.ClickRequestType;
import me.droreo002.chestshopconfirmation.listener.CoreListener;
import me.droreo002.chestshopconfirmation.model.ShopDelayer;
import me.droreo002.oreocore.DependedPluginProperties;
import me.droreo002.oreocore.OreoCore;
import me.droreo002.oreocore.utils.bridge.ServerUtils;
import me.droreo002.oreocore.utils.strings.StringUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import static java.lang.System.*;

public class ChestShopConfirmation extends JavaPlugin {

    @Getter
    private static ChestShopConfirmation instance;
    @Getter
    private PluginConfig pluginConfig;
    @Getter
    private Debug debug;
    @Getter
    private Metrics metrics;
    @Getter
    private Set<Location> shopOnUse;
    @Getter
    private PlayerDatabase playerDatabase;
    @Getter
    private CShopConfirmationCommand mainCommand;
    @Getter
    private ShopDelayer shopDelayer;
    @Getter
    private final Map<UUID, ClickRequestType> onClickRequest = new HashMap<>();

    @Override
    public void onEnable() {
        // Plugin startup logic
        if (ServerUtils.getPlugin("OreoCore") == null) {
            throw new NullPointerException("Plugin need OreoCore to run! (Install guide https://github.com/DrOreo002/ChestShopConfirmation/wiki/Installing)");
        }
        if (ServerUtils.getPlugin("ChestShop") == null) {
            throw new NullPointerException("Plugin need ChestShop to run! (Install guide https://github.com/DrOreo002/ChestShopConfirmation/wiki/Installing)");
        }
        instance = this;
        debug = new Debug(this);
        pluginConfig = new PluginConfig(this);
        debug.setupLogFile();
        shopDelayer = new ShopDelayer(this);

        metrics = new Metrics(this);
        shopOnUse = new HashSet<>();
        playerDatabase = new PlayerDatabase(this);

        debug.log("&8&m+----------------------------------------------------+", Level.INFO, false, true);
        out.println(" ");
        debug.log("                 &aChestShopConfirmation", Level.INFO, false, true);
        debug.log("&7> &fEnabling core features...", Level.INFO, false, true);
        debug.log("&7> &fSetting up config.yml....", Level.INFO, false, true);
        debug.log("&7> &fRegistering commands...", Level.INFO, false, true);
        debug.log("&7> &fFinish!", Level.INFO, false, true);
        if (Properties.TURN_OFF_HOPPER_PROTECTION) debug.log("&7> &cHopper protection is disabled!. Please make sure to enable it to prevent duplication issue by setting it to &cfalse", Level.INFO, false, true);
        out.println(" ");
        debug.log("&8&m+----------------------------------------------------+", Level.INFO, false, true);
        mainCommand = new CShopConfirmationCommand(this);
        ServerUtils.registerListener(this, new CoreListener(this));

        OreoCore.getInstance().dependPlugin(this, DependedPluginProperties.builder()
                .enableLogging(false)
                .premiumPlugin(false)
                .privatePlugin(false)
                .build());
    }

    /**
     * Send a message to player. Color code is supported
     *
     * @param player : The player
     * @param msg : The message to send
     */
    public void sendMessage(Player player, String msg) {
        player.sendMessage(StringUtils.color(getPluginConfig().getPrefix() + msg));
    }
}
