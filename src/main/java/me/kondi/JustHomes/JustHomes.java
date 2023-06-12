package me.kondi.JustHomes;

import me.kondi.JustHomes.Commands.*;
import me.kondi.JustHomes.Data.Database;
import me.kondi.JustHomes.Data.PlayerData;
import me.kondi.JustHomes.Home.HomeNames;
import me.kondi.JustHomes.Listeners.Events;
import me.kondi.JustHomes.Permissions.PermissionChecker;
import me.kondi.JustHomes.Teleportation.TeleportPlayer;
import me.kondi.JustHomes.Utils.ConfigManager;
import me.kondi.JustHomes.Utils.Messages;
import me.kondi.JustHomes.Utils.Metrics;
import me.kondi.JustHomes.Utils.Placeholder;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class JustHomes extends JavaPlugin {

    public FileConfiguration config;

    public String prefix = "JHomes >> ";
    public ConfigManager cfgManager = new ConfigManager(this);

    public boolean simpleProtection;
    public int homesMaxAmount;
    public int teleportationDelay;
    public int teleportationCooldown;

    public boolean soundsEnabled;

    public String sound;
    //Classes
    public Events events;
    public Database db;
    public PlayerData playerData;
    public Commands commands;
    public TeleportPlayer teleportPlayer;
    public PermissionChecker permissionChecker;
    public HomeNames homeNames;
    //Home commands
    public SetHomeCommand setHome;
    public HomeCommand homeCommand;
    public ListHomeCommand listHome;
    public DeleteHomeCommand deleteHome;

    private Metrics metrics;


    private static JustHomes instance;

    public static JustHomes getInstance() {
        return instance;
    }


    /**
     * Method initialized when the server turns on.
     */
    @Override
    public void onEnable() {
        instance = this;
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") == null) {
            getServer().getConsoleSender().sendMessage(String.format("[%s] Disabled due to no PlaceholderAPI dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }


        loadConfig();
        loadClasses();
        loadCommands();
        getServer().getPluginManager().registerEvents(events, this);
        Player[] players = getServer().getOnlinePlayers().toArray(new Player[0]);
        if(getServer().getOnlinePlayers().size() >0)
            for (Player player : players)
                events.addPlayer(player);


    }

    /**
     * Method initialized when the server turns off.
     */
    @Override
    public void onDisable() {
        loadConfig();
        if (db != null) {
            String message = Messages.get("SavingAllPlayersData");
            if(message != null)
                getServer().getConsoleSender().sendMessage(prefix + message);
            else getServer().getConsoleSender().sendMessage(prefix + "Saving all players data!");
            db.saveAllHomes();
            db.saveTeleportationCooldowns();
            db.stopDatabaseConnection();
        }
    }

    /**
     * Creates instance for every class.
     */
    public void loadClasses() {
        metrics = new Metrics(this, 15508);
        db = new Database(this);
        playerData = new PlayerData(this);
        events = new Events(this);
        commands = new Commands(this);
        teleportPlayer = new TeleportPlayer(this);
        setHome = new SetHomeCommand(this);
        homeCommand = new HomeCommand(this);
        listHome = new ListHomeCommand(this);
        deleteHome = new DeleteHomeCommand(this);
        permissionChecker = new PermissionChecker(this);
        homeNames = new HomeNames();
        new Placeholder().register();
    }

    /**
     * Register all commands.
     */
    public void loadCommands() {

        getCommand("sethome").setExecutor(commands);
        getCommand("home").setExecutor(commands);
        getCommand("home").setTabCompleter(commands);
        getCommand("listhome").setExecutor(commands);
        getCommand("delhome").setExecutor(commands);
        getCommand("delhome").setTabCompleter(commands);
        getCommand("reloadlanguage").setExecutor(commands);
    }

    /***
     *Loads all the variables from the Config.
     */
    public void loadConfig() {
        saveDefaultConfig();
        cfgManager.setup();
        cfgManager.updateConfig();
        prefix = config.getString(ChatColor.translateAlternateColorCodes('&', "Prefix"));
        simpleProtection = config.getBoolean("SimpleProtection");
        homesMaxAmount = config.getInt("HomesMaxAmount");
        teleportationDelay = config.getInt("DelayInTeleport");
        soundsEnabled = config.getBoolean("SoundsEnabled");
        teleportationCooldown = config.getInt("TeleportationCooldown");
        if (soundsEnabled) {
            sound = config.getString("Sound");
        }

        Messages.reload();


    }
}
