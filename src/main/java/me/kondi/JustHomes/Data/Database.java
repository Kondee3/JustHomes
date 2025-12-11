package me.kondi.JustHomes.Data;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.db.DatabaseType;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.jdbc.db.DatabaseTypeUtils;
import com.j256.ormlite.jdbc.db.MysqlDatabaseType;
import com.j256.ormlite.jdbc.db.SqliteDatabaseType;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import me.kondi.JustHomes.Home.Home;
import me.kondi.JustHomes.JustHomes;
import me.kondi.JustHomes.PlayerData.PlayerDataAdditional;
import me.kondi.JustHomes.Teleportation.TeleportPlayer;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Database {

    private Dao<Home, String> homesDao;
    private Dao<PlayerDataAdditional, String> playerDataAdditionalDao;

    //Database objects and connection data
    private ConnectionSource connectionSource;
    private DatabaseConfig databaseConfig;

    //Config and plugin objects
    private FileConfiguration config;
    private String prefix;
    private ConsoleCommandSender console;


    //Create database
    public Database(JustHomes plugin) {
        this.prefix = plugin.prefix;
        this.config = plugin.config;
        databaseConfig = new DatabaseConfig(config.getString("DatabaseType"),
                config.getString("Host"),
                config.getString("DatabaseName"),
                config.getString("Username"),
                config.getString("Password"));
        this.console = plugin
                .getServer()
                .getConsoleSender();

        try {
            if (databaseConfig
                    .getDatabaseType()
                    .equalsIgnoreCase("MYSQL")) {

                connectionSource = new JdbcConnectionSource(databaseConfig.getHost() +
                        "/" + databaseConfig.getDatabase(), databaseConfig.getUsername(), databaseConfig.getPassword(), new MysqlDatabaseType());

            } else if (databaseConfig
                    .getDatabaseType()
                    .equalsIgnoreCase("SQLITE")) {

                connectionSource = new JdbcConnectionSource("jdbc:sqlite:" + plugin.getDataFolder() + "/playerdata/homeData.db", new SqliteDatabaseType());

            }

            createHomeTable();
            createPlayerDataTable();
        } catch (Exception ex) {

            console.sendMessage(prefix + "ERROR: " + ex);

        }

    }

    /**
     * Used to save all player teleportation cooldown.
     */
    public void saveTeleportationCooldowns() {
        TeleportPlayer.tpCooldownBetweenTeleportation.forEach((key, value) -> {
            if (value > System.currentTimeMillis()) {
                try {
                    saveCooldown(key);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    /**
     * Used to know if the cooldown existed.
     *
     * @param uuid Player's uuid
     * @return Returns true if the cooldown entry in database exists.
     */
    private boolean cooldownExistence(String uuid) throws SQLException {
        return playerDataAdditionalDao.idExists(uuid);
    }

    /**
     * Used to save the player teleportation cooldown, when value is larger than current time in ms.
     * Used when one player leaves the server.
     *
     * @param uuid Player uuid.
     */
    public void saveCooldown(String uuid) throws SQLException {
        playerDataAdditionalDao.createOrUpdate(new PlayerDataAdditional(uuid, TeleportPlayer.tpCooldownBetweenTeleportation.get(uuid)));
    }

    /**
     * Used to prepare queries and execute queries to save home object in database.
     *
     * @param home Player's home object.
     */
    public void setHome(Home home) throws SQLException {
        homesDao.createOrUpdate(home);
    }


    /**
     * Used to delete player's home.
     *
     * @param home Player's home to be deleted.
     */
    public void deleteHome(Home home) throws SQLException {
        homesDao.delete(home);
    }

    /**
     * Query to create a Homes table.
     */
    public void createHomeTable() throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, Home.class);
        homesDao = DaoManager.createDao(connectionSource, Home.class);
    }

    /**
     * Query to create a Player Data table.
     */
    public void createPlayerDataTable() throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, PlayerDataAdditional.class);
        playerDataAdditionalDao = DaoManager.createDao(connectionSource, PlayerDataAdditional.class);
    }

    /**
     * Stops database connection.
     */

    public void stopDatabaseConnection() throws Exception {
        connectionSource.close();
    }

    /**
     * Loads user data to cached list.
     *
     * @param uuid Player's uuid.
     */

    public void loadHomesData(String uuid) throws SQLException {
        List<Home> listOfHomes = homesDao.query(homesDao
                .queryBuilder()
                .where()
                .like("owner", uuid)
                .prepare());
        Cache.setCachedListOfHomes(uuid, listOfHomes);
    }

    /**
     * Loads player teleportation cooldowns.
     *
     * @param uuid Player's uuid.
     */
    public void loadPlayerData(String uuid) throws SQLException {

        PlayerDataAdditional playerDataAdditional = playerDataAdditionalDao.queryForId(uuid);
        if (playerDataAdditional != null) {
            long cooldown = playerDataAdditional.getCooldown();
            if (cooldown > System.currentTimeMillis())
                TeleportPlayer.tpCooldownBetweenTeleportation.put(uuid, playerDataAdditional.getCooldown());
        }
    }


}
