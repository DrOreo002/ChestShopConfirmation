package me.droreo002.chestshopconfirmation.database;

import lombok.Getter;
import me.droreo002.chestshopconfirmation.ChestShopConfirmation;
import me.droreo002.oreocore.database.object.DatabaseFlatFile;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PlayerDatabase extends DatabaseFlatFile {

    @Getter
    private final Set<PlayerData> playerData;
    @Getter
    private final ChestShopConfirmation plugin;

    public PlayerDatabase(ChestShopConfirmation plugin) {
        super(plugin, new File(plugin.getDataFolder(), "database"), false);
        this.plugin = plugin;
        this.playerData = new HashSet<>();
    }

    @Override
    public void loadData() {
        // Do nothing
    }

    @Override
    public void addDefaults(FileConfiguration fileConfiguration) {
        fileConfiguration.set("Data.confirmationDisabled", false);
        fileConfiguration.set("Data.disabledShops", "{}");
    }

    /**
     * Get player data
     *
     * @param uuid The player UUID
     * @return the PlayerData object if available, null otherwise
     */
    public PlayerData getPlayerData(UUID uuid) {
        return playerData.stream().filter(data -> data.getPlayerUuid().equals(uuid)).findAny().orElse(null);
    }

    /**
     * Register the player data into the database
     *
     * @param uuid Player uuid
     */
    public void registerPlayerData(UUID uuid) {
        if (getPlayerData(uuid) != null) return;
        setup(uuid.toString(), true);
        final Data data = getDataClass(uuid.toString());
        playerData.add(new PlayerData(uuid, data));
    }

    /**
     * Unregister the player data
     *
     * @param uuid The player uuid
     * @param delete Should we delete the file?
     */
    public void unregisterPlayerData(UUID uuid, boolean delete) {
        final PlayerData data = getPlayerData(uuid);
        if (data == null) return;
        if (delete) removeData(data.getData(), true);
        playerData.removeIf(pData -> pData.getPlayerUuid().equals(uuid));
    }
}
