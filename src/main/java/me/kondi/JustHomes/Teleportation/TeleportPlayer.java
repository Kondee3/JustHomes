package me.kondi.JustHomes.Teleportation;

import me.clip.placeholderapi.PlaceholderAPI;
import me.kondi.JustHomes.JustHomes;
import me.kondi.JustHomes.Utils.Messages;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class TeleportPlayer {
    public static HashMap<String, Integer> tpCooldown = new HashMap<>();
    public static HashMap<String, BukkitRunnable> tpCooldownTask = new HashMap<>();
    private final JustHomes plugin;
    private String prefix;

    public TeleportPlayer(JustHomes plugin) {
        this.plugin = plugin;
        this.prefix = plugin.prefix;

    }


    public void teleportPlayer(Player p, Location loc, int duration) {
        String uuid = p.getUniqueId().toString();


        tpCooldown.put(uuid, duration);
        p.sendMessage(prefix + PlaceholderAPI.setPlaceholders(p, Messages.get("Teleporting")));


        tpCooldownTask.put(uuid, new BukkitRunnable() {
            public void run() {
                if (tpCooldown.get(uuid) > 0) {
                    tpCooldown.put(uuid, tpCooldown.get(uuid) - 1);
                }
                if (tpCooldown.get(uuid) == 0) {
                    p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(Messages.get("ActionBarNameWhileTeleporting")));
                    p.teleport(loc);
                    p.sendMessage(prefix + PlaceholderAPI.setPlaceholders(p, Messages.get("SuccesfullTeleportation")));
                    tpCooldownTask.get(uuid).cancel();
                    tpCooldown.remove(uuid);
                    tpCooldownTask.remove(uuid);
                }
                else{
                    p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(tpCooldown.get(uuid).toString()));
                }
            }

        });
        tpCooldownTask.get(uuid).runTaskTimer(plugin, 0, 20);
    }
}


