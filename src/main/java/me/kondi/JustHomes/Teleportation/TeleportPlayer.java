package me.kondi.JustHomes.Teleportation;

import me.clip.placeholderapi.PlaceholderAPI;
import me.kondi.JustHomes.Home.Home;
import me.kondi.JustHomes.Home.HomeNames;
import me.kondi.JustHomes.JustHomes;
import me.kondi.JustHomes.Permissions.PermissionChecker;
import me.kondi.JustHomes.Utils.Messages;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class TeleportPlayer {
    public static HashMap<String, Integer> tpDelay = new HashMap<>();
    public static HashMap<String, BukkitRunnable> tpDelayTask = new HashMap<>();

    public static HashMap<String, Long> tpCooldownBetweenTeleportation = new HashMap<>();
    private final JustHomes plugin;
    private String prefix;

    public TeleportPlayer(JustHomes plugin) {
        this.plugin = plugin;
        this.prefix = plugin.prefix;

    }

    /**
     *
     * @param p Player to be teleported.
     * @param home Home object used for teleportation.
     * @param delay Delay in seconds before teleportation.
     */
    public void teleportPlayer(Player p, Home home, int delay) {
        String uuid = home.getOwner();
        if(tpDelay.containsKey(uuid)){
            p.sendMessage(prefix + PlaceholderAPI.setPlaceholders(p, Messages.get("PendingAnotherTeleportation")));
            return;
        }

        if(tpCooldownBetweenTeleportation.containsKey(uuid) && tpCooldownBetweenTeleportation.get(uuid) > System.currentTimeMillis()){
            p.sendMessage(prefix + PlaceholderAPI.setPlaceholders(p, Messages.get("TeleportationOnCooldown")));
            return;
        }


        tpDelay.put(uuid, delay);
        HomeNames.addHomeName(uuid, home.getHomeName());
        p.sendMessage(prefix + PlaceholderAPI.setPlaceholders(p, Messages.get("Teleporting")));


        tpDelayTask.put(uuid, new BukkitRunnable() {
            public void run() {
                if (tpDelay.get(uuid) > 0) {
                    tpDelay.put(uuid, tpDelay.get(uuid) - 1);
                }
                if (tpDelay.get(uuid) == 0) {
                    p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(Messages.get("ActionBarNameWhileTeleporting")));
                    p.teleport(home.getLocation());
                    p.sendMessage(prefix + PlaceholderAPI.setPlaceholders(p, Messages.get("SuccessfulTeleportation")));
                    if (plugin.soundsEnabled)
                        p.playSound(p.getLocation(), Sound.valueOf(PermissionChecker.checkTeleportationSound(p)), 1f, 1f);
                    tpCooldownBetweenTeleportation.put(uuid, System.currentTimeMillis() + PermissionChecker.checkCooldown(p)* 1000L);
                    tpDelayTask.get(uuid).cancel();
                    tpDelay.remove(uuid);
                    tpDelayTask.remove(uuid);

                } else {
                    p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(tpDelay.get(uuid).toString()));
                }
            }


        });
        tpDelayTask.get(uuid).runTaskTimer(plugin, 0, 20);
    }
}


