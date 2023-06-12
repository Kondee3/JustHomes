package me.kondi.JustHomes.Data;

import me.kondi.JustHomes.Home.Home;
import me.kondi.JustHomes.JustHomes;
import me.kondi.JustHomes.Teleportation.TeleportPlayer;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.*;
import java.util.*;

public class Database {


    //Cache
    private HashMap<String, List<Home>> cachedHomes = new HashMap<>();

    //Database objects and connection data
    private Connection con;
    private String host;
    private String database;
    private String username;
    private String password;
    private String databaseType;

    //Config and plugin objects
    private FileConfiguration config;
    private JustHomes plugin;
    private String prefix;
    private ConsoleCommandSender console;


    //Create database
    public Database(JustHomes plugin) {
        this.plugin = plugin;
        this.prefix = plugin.prefix;
        this.config = plugin.config;
        this.databaseType = config.getString("DatabaseType");
        this.console = plugin.getServer().getConsoleSender();

        try {
            if (databaseType.equalsIgnoreCase("MYSQL")) {
                this.host = config.getString("Host");
                this.database = config.getString("DatabaseName");
                this.username = config.getString("Username");
                this.password = config.getString("Password");
                con = DriverManager.getConnection("jdbc:mysql://" + host + "/" + database + "", username, password);
            } else if (databaseType.equalsIgnoreCase("SQLITE")) {
                con = DriverManager.getConnection("jdbc:sqlite:" + plugin.getDataFolder() + "/playerdata/homeData.db");
            }

            createHomeTable();
            createPlayerDataTable();
        } catch (Exception ex) {

            console.sendMessage(prefix + "ERROR: " + ex);

        }

    }

    /**
     * Used to save all player homes on PlayerQuitEvent.
     * @param uuid Player's uuid.
     */

    public void saveHomes(String uuid){
        cachedHomes.get(uuid).forEach(home -> {
            try {
                setHome(home);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Used to save all homes of all players when the server closes.
     */
    public void saveAllHomes(){
        cachedHomes.entrySet().forEach(entry ->{
            for(Home home : entry.getValue()) {
                try {
                    setHome(home);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    /**
     * Used to save all player teleportation cooldown.
     */
    public void saveTeleportationCooldowns(){
        TeleportPlayer.tpCooldownBetweenTeleportation.entrySet().forEach(delayEntry -> {
            if(delayEntry.getValue()>System.currentTimeMillis())
                saveCooldown(delayEntry.getKey());
        });
    }

    /**
     * Used to know if the cooldown existed.
     * @param uuid Player's uuid
     * @return Returns true if the cooldown entry in database exists.
     */
    private boolean cooldownExistence(String uuid){

        try {
            String exist = "SELECT COOLDOWN FROM PLAYERDATA WHERE UUID = ?";
            PreparedStatement preparedStmt = con.prepareStatement(exist);
            preparedStmt.setString(1, uuid);
            return preparedStmt.executeQuery().next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Used to save the player teleportation cooldown, when value is larger than current time in ms.
     * Used when one player leaves the server.
     * @param uuid Player uuid.
     */
    public void saveCooldown(String uuid) {
        try {
            if(!cooldownExistence(uuid)){
                String query = "INSERT INTO PLAYERDATA (UUID, COOLDOWN)" + "VALUES(?, ?)";
                PreparedStatement preparedStmtInsert = con.prepareStatement(query);
                preparedStmtInsert.setString(1, uuid);
                preparedStmtInsert.setLong(2, TeleportPlayer.tpCooldownBetweenTeleportation.get(uuid));
                preparedStmtInsert.execute();
            }else{
                String query = "UPDATE PLAYERDATA SET COOLDOWN=? WHERE UUID = ?";
                PreparedStatement preparedStmtInsert = con.prepareStatement(query);
                preparedStmtInsert.setLong(1, TeleportPlayer.tpCooldownBetweenTeleportation.get(uuid));
                preparedStmtInsert.setString(2, uuid);
                preparedStmtInsert.execute();
            }


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
        /**
     * Used to prepare queries and execute queries to save home object in database.
     * @param home Player's home object.
     * @throws SQLException
     */
    public void setHome(Home home) throws SQLException {
        try {
            String exist = "SELECT * FROM HOMES WHERE UUID = ? AND HomeName = ?";
            PreparedStatement preparedStmt = con.prepareStatement(exist);
            preparedStmt.setString(1, home.getOwner());
            preparedStmt.setString(2, home.getHomeName());

            ResultSet results = preparedStmt.executeQuery();
            if (!results.next()) {
                String query = "INSERT INTO HOMES (UUID, WorldName, HomeName, X, Y, Z, Pitch, Yaw)" + "VALUES(?, ?, ?,?, ?, ?, ?, ?)";
                PreparedStatement preparedStmtInsert = con.prepareStatement(query);
                preparedStmtInsert.setString(1, home.getOwner());
                preparedStmtInsert.setString(2, home.getWorldName());
                preparedStmtInsert.setString(3, home.getHomeName());
                preparedStmtInsert.setDouble(4, home.getX());
                preparedStmtInsert.setDouble(5, home.getY());
                preparedStmtInsert.setDouble(6, home.getZ());
                preparedStmtInsert.setFloat(7, home.getPitch());
                preparedStmtInsert.setFloat(8, home.getYaw());

                preparedStmtInsert.execute();
            } else {
                String query = "UPDATE HOMES SET WorldName=?, X=?, Y=?, Z=?, Pitch=?, Yaw=? WHERE UUID = ? AND HomeName=?";
                PreparedStatement preparedStmtUpdate = con.prepareStatement(query);
                preparedStmtUpdate.setString(1, home.getWorldName());
                preparedStmtUpdate.setDouble(2, home.getX());
                preparedStmtUpdate.setDouble(3, home.getY());
                preparedStmtUpdate.setDouble(4, home.getZ());
                preparedStmtUpdate.setFloat(5, home.getPitch());
                preparedStmtUpdate.setFloat(6, home.getYaw());
                preparedStmtUpdate.setString(7, home.getOwner());
                preparedStmtUpdate.setString(8, home.getHomeName());
                preparedStmtUpdate.execute();
            }


        } catch (Exception ex) {

            console.sendMessage(prefix + "ERROR:" + ex);

        }

    }

    /**
     * Used to get player's saved homes amount.
     * @param uuid Player's uuid.
     * @return Amount of homes.
     * @throws SQLException
     */
    public int getHomesAmount(String uuid) throws SQLException {
        return cachedHomes.get(uuid).size();
    }

    /**
     * Used to get List of cached player's homes.
     * @param uuid Player's uuid.
     * @return List of cached homes.
     */
    public List<Home> getCachedListOfHomes(String uuid) {
        return cachedHomes.get(uuid);
    }

    /**
     * Used to get player's home.
     * @param uuid Player's uuid.
     * @param homeName Player's home name.
     * @return Home object from cached home list.
     * @throws SQLException
     */
    public Home getHome(String uuid, String homeName) throws SQLException {
        Optional<Home> home = cachedHomes.get(uuid).stream().filter(h -> h.getHomeName().equalsIgnoreCase(homeName)).findFirst();

        return home.orElse(null);
    }


    /**
     * Used to delete player's home.
     * @param home Player's home to be deleted.
     * @throws SQLException
     */
    public void deleteHome(Home home) throws SQLException {
        try {
            cachedHomes.get(home.getOwner()).remove(home);

            String query = "DELETE FROM HOMES WHERE UUID = ? AND HomeName = ?";
            PreparedStatement preparedStmtInsert = con.prepareStatement(query);
            preparedStmtInsert.setString(1, home.getOwner());
            preparedStmtInsert.setString(2, home.getHomeName());
            preparedStmtInsert.execute();

        } catch (Exception ex) {
            console.sendMessage(prefix + "ERROR: " + ex);

        }
    }


    /**
     * Query to create a Homes table.
     */
    public void createHomeTable() {
        try {
            DatabaseMetaData meta = con.getMetaData();
            ResultSet tables = meta.getTables(null, null, "HOMES", null);

            while (!tables.next()) {
                String homeDataTable = "CREATE TABLE HOMES" +
                        "(UUID VARCHAR(255) NOT NULL, " +
                        "HomeName VARCHAR(255) NOT NULL, " +
                        "WorldName VARCHAR(255), " +
                        "X DOUBLE, " +
                        "Y DOUBLE, " +
                        "Z DOUBLE, " +
                        "Pitch FLOAT, " +
                        "Yaw FLOAT," +
                        "PRIMARY KEY (UUID, HomeName))";
                PreparedStatement preparedStatement = con.prepareStatement(homeDataTable);
                preparedStatement.execute();
                break;
            }


        } catch (Exception ex) {

            console.sendMessage(prefix + "ERROR: " + ex);
        }
    }

    /**
     * Query to create a Player Data table.
     */
    public void createPlayerDataTable() {
        try {
            DatabaseMetaData meta = con.getMetaData();
            ResultSet tables = meta.getTables(null, null, "PLAYERDATA", null);

            while (!tables.next()) {
                String playerDataTable = "CREATE TABLE PLAYERDATA" +
                        "(UUID VARCHAR(255) NOT NULL, " +
                        "COOLDOWN INTEGER," +
                        "PRIMARY KEY (UUID))";
                PreparedStatement preparedStatement = con.prepareStatement(playerDataTable);
                preparedStatement.execute();
                break;
            }


        } catch (Exception ex) {

            console.sendMessage(prefix + "ERROR: " + ex);
        }
    }

    /**
     * Stops database connection.
     */

    public void stopDatabaseConnection() {
        try {
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads user data to cached list.
     * @param uuid Player's uuid.
     */

    public void loadHomesData(String uuid) {
        try {


            String query = "SELECT HomeName, WorldName, X, Y, Z, Pitch, Yaw FROM HOMES WHERE UUID=?";
            PreparedStatement preparedStmt = con.prepareStatement(query);
            preparedStmt.setString(1, uuid);
            ResultSet results = preparedStmt.executeQuery();
            List<Home> listOfHomes = new ArrayList<>();

            while (results.next()) {
                Home home = new Home(uuid,
                        results.getString("HomeName"),
                        results.getString("WorldName"),
                        results.getDouble("X"),
                        results.getDouble("Y"),
                        results.getDouble("Z"),
                        results.getFloat("Pitch"),
                        results.getFloat("Yaw"));
                listOfHomes.add(home);
            }

            cachedHomes.put(uuid, listOfHomes);

        } catch (Exception ex) {

            console.sendMessage(prefix + "ERROR: " + ex);

        }

    }
    public void loadPlayerData(String uuid) {
        try {


            String query = "SELECT COOLDOWN FROM PLAYERDATA WHERE UUID=?";
            PreparedStatement preparedStmt = con.prepareStatement(query);
            preparedStmt.setString(1, uuid);
            ResultSet results = preparedStmt.executeQuery();
            while (results.next()) {
                long cooldown = results.getLong("COOLDOWN");
                if(cooldown>System.currentTimeMillis())
                    TeleportPlayer.tpCooldownBetweenTeleportation.put(uuid, results.getLong("COOLDOWN"));
            }

        } catch (Exception ex) {

            console.sendMessage(prefix + "ERROR: " + ex);

        }
    }

    /**
     * Adds home to cache list.
     * @param home Player's home.
     */
    public void addHomeToCache(Home home) {

        cachedHomes.get(home.getOwner()).add(home);

    }

    /**
     * Used to edit home location.
     * @param home Player's home
     * @param newHome Player's new home.
     */
    public void replaceHomeInCache(Home home, Home newHome){
        List<Home> homeList = cachedHomes.get(home.getOwner());
        homeList.set(homeList.indexOf(home), newHome);
        cachedHomes.replace(home.getOwner(), homeList );
    }

}
