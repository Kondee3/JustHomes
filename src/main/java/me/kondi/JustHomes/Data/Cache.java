package me.kondi.JustHomes.Data;

import me.kondi.JustHomes.Home.Home;
import me.kondi.JustHomes.JustHomes;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

public class Cache {
    private JustHomes plugin;
    private static Database database;
    public Cache(JustHomes plugin) {
        this.plugin = plugin;
        this.database = plugin.db;
    }

    private static HashMap<String, List<Home>> cachedHomes = new HashMap<>();

    /**
     * Used to save all player homes on PlayerQuitEvent.
     *
     * @param uuid Player's uuid.
     */

    public static void saveHomes(String uuid) {
        cachedHomes.get(uuid).forEach(home -> {
            try {
                database.setHome(home);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Used to save all homes of all players when the server closes.
     */
    public static void saveAllHomes() {
        cachedHomes.forEach((key, value) -> {
            for (Home home : value) {
                try {
                    database.setHome(home);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });

    }

    /**
     * Used to get player's saved homes amount.
     *
     * @param uuid Player's uuid.
     * @return Amount of homes.
     */
    public  static int getHomesAmount(String uuid) throws SQLException {
        return cachedHomes.get(uuid).size();
    }

    /**
     * Used to get List of cached player's homes.
     *
     * @param uuid Player's uuid.
     * @return List of cached homes.
     */
    public static List<Home> getCachedListOfHomes(String uuid) {
        return cachedHomes.get(uuid);
    }

    public static List<Home> setCachedListOfHomes(String uuid, List<Home> homes) {
        return cachedHomes.put(uuid, homes);
    }

    public static void removeHome(Home home) {
        cachedHomes.get(home.getOwner()).remove(home);
        try {
            database.deleteHome(home);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Used to get player's home.
     *
     * @param uuid     Player's uuid.
     * @param homeName Player's home name.
     * @return Home object from cached home list.
     */
    public static Home getHome(String uuid, String homeName) throws SQLException {
        return cachedHomes.get(uuid).stream()
                .filter(h -> h.getHomeName().equalsIgnoreCase(homeName))
                .findFirst()
                .orElse(null);
    }

    /**
     * Adds home to cache list.
     *
     * @param home Player's home.
     */
    public static void addHomeToCache(Home home) {
        cachedHomes.get(home.getOwner()).add(home);
    }

    /**
     * Helps in getting home index.
     *
     * @param home Player's home.
     */
    private static int getHomeIndex(Home home){
        return cachedHomes.get(home.getOwner()).indexOf(home);
    }

    /**
     * Helps in renaming and editing home location.
     *
     * @param home Player's home.
     */
    private static Home getHome(Home home){
        return  cachedHomes.get(home.getOwner()).get(getHomeIndex(home));
    }

    /**
     * Used to edit home location.
     *
     * @param home    Player's home
     * @param loc Player's location.
     */
    public static void editHomeLocationInCache(Home home, Location loc) {
       getHome(home).setLocation(loc);
    }

    /**
     * Used to rename home.
     *
     * @param home    Player's home
     * @param newName Player's new home name.
     * Current home checked earlier.
     * @see me.kondi.JustHomes.Commands.RenameHomeCommand#execute
     */
    public static void renameHomeInCache(Home home, String newName) {
        getHome(home).setHomeName(newName);
    }
}
