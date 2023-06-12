package me.kondi.JustHomes.Listeners;

import me.kondi.JustHomes.Data.PlayerData;
import me.kondi.JustHomes.JustHomes;
import me.kondi.JustHomes.Teleportation.TeleportPlayer;
import me.kondi.JustHomes.Utils.ConfigManager;
import me.kondi.JustHomes.Utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class Events implements Listener {
    private JustHomes plugin;
    private TeleportPlayer teleportPlayer;
    private ConfigManager cfgManager;

    private PlayerData playerData;

    public Events(JustHomes plugin) {
        this.plugin = plugin;
        this.cfgManager = plugin.cfgManager;
        this.playerData = plugin.playerData;

    }

    /**
     * Used for saving homes when player quit the server.
     * @param e PlayerQuitEvent
     */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        String uuid = e.getPlayer().getUniqueId().toString();
        teleportPlayer = plugin.teleportPlayer;
        if (teleportPlayer.tpDelayTask.containsKey(uuid)) {
            teleportPlayer.tpDelayTask.get(uuid).cancel();
            teleportPlayer.tpDelayTask.remove(uuid);
            teleportPlayer.tpDelay.remove(uuid);

        }
        if(playerData.getHomesAmount(uuid)>0)
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> playerData.saveHomes(uuid));
        if(TeleportPlayer.tpCooldownBetweenTeleportation.containsKey(uuid))
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> playerData.saveCooldown(uuid));
    }

    /**
     * Used for stopping player from moving while trying to teleport.
     * @param e PlayerMoveEvent
     */
    @EventHandler
    public void moveEvent(PlayerMoveEvent e) {
        String uuid = e.getPlayer().getUniqueId().toString();
        teleportPlayer = plugin.teleportPlayer;
        if (teleportPlayer.tpDelayTask.containsKey(uuid)) {
            Location getFrom = e.getFrom().getBlock().getLocation();
            Location getTo = e.getTo().getBlock().getLocation();
            if (getFrom.getX() != getTo.getX() || getFrom.getZ() != getTo.getZ() || getFrom.getY() != getTo.getY()) {
                teleportPlayer.tpDelayTask.get(uuid).cancel();
                teleportPlayer.tpDelayTask.remove(uuid);
                teleportPlayer.tpDelay.remove(uuid);
                e.getPlayer().sendMessage(plugin.prefix + Messages.get("TeleportationCancelled"));
            }

        }
    }


    /**
     * Used for loading player data to cache when player joins server.
     * @param e PlayerJoinEvent
     */
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        addPlayer(e.getPlayer());
    }

    /**
     * Used for loading player data to cache when player joins server or already is on the server.
     * @param p Player that joined the server or already is online.
     */
    public void addPlayer(Player p){
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> playerData.loadPlayerData(p.getUniqueId().toString()));

    }

}
