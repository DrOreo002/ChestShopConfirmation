package me.droreo002.chestshopconfirmation.database;

import lombok.Getter;
import me.droreo002.chestshopconfirmation.ChestShopConfirmation;
import me.droreo002.oreocore.database.FlatFileDatabase;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PlayerDatabase extends FlatFileDatabase {

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
        PlayerData loaded = playerData.stream().filter(data -> data.getPlayerUuid().equals(uuid)).findAny().orElse(null);
        if (loaded == null) {
            registerPlayerData(uuid);
            return getPlayerData(uuid);
        }
        return loaded;
    }

    /**
     * Check if the player data is loaded or not
     *
     * @param uuid The uuid
     * @return True if loaded, false otherwise
     */
    private boolean isPlayerDataLoaded(UUID uuid) {
        return  playerData.stream().filter(data -> data.getPlayerUuid().equals(uuid)).findAny().orElse(null) != null;
    }

    /**
     * Register the player data into the database
     *
     * @param uuid Player uuid
     */
    private void registerPlayerData(UUID uuid) {
        createData(uuid.toString());
        DataCache data = getDataCache(uuid.toString());
        playerData.add(new PlayerData(uuid, data));
    }

    /**
     * Unregister the player data
     *
     * @param uuid The player uuid
     * @param delete Should we delete the file?
     */
    public void unregisterPlayerData(UUID uuid, boolean delete) {
        if (!isPlayerDataLoaded(uuid)) return;
        final PlayerData data = getPlayerData(uuid);
        if (data == null) return;
        removeData(data.getData(), delete);
        playerData.removeIf(pData -> pData.getPlayerUuid().equals(uuid));
    }
}
