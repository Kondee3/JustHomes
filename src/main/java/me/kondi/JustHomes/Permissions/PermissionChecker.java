package me.kondi.JustHomes.Permissions;

import me.kondi.JustHomes.JustHomes;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

public class PermissionChecker {
    private JustHomes plugin;
    private static int maxAmount;
    private static int teleportationDelay;

    private static int teleportationCooldown;

    private static String teleportationSound;

    private static final String maxHomesPermission = "justhomes.maxhomes.";
    private static final String teleportationDelayPermission = "justhomes.teleportationdelay.";
    private static final String teleportationSoundPermission = "justhomes.teleportationsound.";
    private static final String teleportationCooldownPermission = "justhomes.teleportationcooldown.";

    public PermissionChecker(JustHomes plugin) {
        this.plugin = plugin;
        this.maxAmount = plugin.homesMaxAmount;
        this.teleportationDelay = plugin.teleportationDelay;

        this.teleportationSound = plugin.sound;

        this.teleportationCooldown = plugin.teleportationCooldown;
    }


    /**
     * @param p Player to be checked.
     * @return Max amount of homes
     */
    public static int checkHomesMaxAmount(Player p) {
        for (PermissionAttachmentInfo permissions : p.getEffectivePermissions())
            if (permissions.getPermission().contains(maxHomesPermission))
                return Integer.parseInt(permissions.getPermission().split("\\.")[2]);

        return maxAmount;
    }

    /**
     * @param p Player to be checked.
     * @return Time to wait for teleportation.
     */
    public static int checkDelay(Player p) {
        for (PermissionAttachmentInfo permissions : p.getEffectivePermissions())
            if (permissions.getPermission().contains(teleportationDelayPermission))
                return Integer.parseInt(permissions.getPermission().split("\\.")[2]);

        return teleportationDelay;
    }

    /**
     * @param p Player to be checked.
     * @return Time to wait for another teleportation.
     */
    public static int checkCooldown(Player p) {
        for (PermissionAttachmentInfo permissions : p.getEffectivePermissions())
            if (permissions.getPermission().contains(teleportationCooldownPermission))
                return  Integer.parseInt(permissions.getPermission().split("\\.")[2]);

        return teleportationDelay;
    }

    /**
     * @param p Player to be checked.
     * @return Teleportation sound name.
     */
    public static String checkTeleportationSound(Player p) {
        for (PermissionAttachmentInfo permissions : p.getEffectivePermissions())
            if (permissions.getPermission().contains(teleportationSoundPermission))
                return permissions.getPermission().split("\\.")[2].toUpperCase();

        return teleportationSound;
    }
}
