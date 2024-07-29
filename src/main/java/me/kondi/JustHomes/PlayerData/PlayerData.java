package me.kondi.JustHomes.PlayerData;

import me.kondi.JustHomes.Data.Cache;
import me.kondi.JustHomes.Data.Database;
import me.kondi.JustHomes.Home.Home;
import me.kondi.JustHomes.JustHomes;
import org.bukkit.Location;
import org.bukkit.command.ConsoleCommandSender;

import java.sql.SQLException;
import java.util.List;

public class PlayerData {

    private final JustHomes plugin;
    private final ConsoleCommandSender console;
    private final String prefix;
    private final Database db;

    /**
     * Used to bridge the Command layer and Database layer.
     * @param plugin JustHomes object.
     */
    public PlayerData(JustHomes plugin) {
        this.plugin = plugin;
        this.console = plugin.getServer().getConsoleSender();
        this.prefix = plugin.prefix;
        this.db = plugin.db;
    }

    /**
     * Used to get amount of player's saved homes.
     * @param uuid Player's uuid.
     * @return Amount of player's saved homes.
     */
    public int getHomesAmount(String uuid) {
        try {
            return Cache.getHomesAmount(uuid);
        } catch (SQLException ex) {
            console.sendMessage(prefix + "ERROR: " + ex);
        }
        return 0;
    }


    /**
     * Used to get list of player's saved homes.
     * @param uuid Player's uuid.
     * @return List of player's saved homes.
     */
    public List<Home> getListOfHomes(String uuid) {
        return Cache.getCachedListOfHomes(uuid);
    }

    /**
     * Used to get home object.
     * @param uuid Player's uuid.
     * @param homeName Home name.
     * @return Home object.
     */
    public Home getHome(String uuid, String homeName){
        try {
            return  Cache.getHome(uuid, homeName);
        }
        catch (SQLException ex){
            console.sendMessage(prefix + "ERROR: " + ex);
        }
        return null;
    }

    /**
     * Used to save homes.
     * @param uuid Player's uuid.
     */
    public void saveHomes(String uuid) {
        Cache.saveHomes(uuid);
    }

    /**
     * Used to save cooldown between teleportation.
     * @param uuid Player's uuid.
     */
    public void saveCooldown(String uuid) {
        try {
            db.saveCooldown(uuid);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteHome(Home home) {
        Cache.removeHome(home);
    }

    /**
     * Used to load player data on join.
     * @param uuid Player's uuid.
     */
    public void loadPlayerData(String uuid) {

        try {
            db.loadHomesData(uuid);
            db.loadPlayerData(uuid);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Used to add home to the cached list of homes.
     * @param home Player's home object.
     */
    public void addHome(Home home) {
        Cache.addHomeToCache(home);
    }

    /**
     * Used to replace home with new modified home.
     * @param home Player's home object.
     * @param newHome Player's new home object.
     */
    public void editHomeLocation(Home home, Location loc) {
        Cache.editHomeLocationInCache(home, loc);
    }

    public void renameHome(Home home, String newName) {
        Cache.renameHomeInCache(home, newName);
    }

}
