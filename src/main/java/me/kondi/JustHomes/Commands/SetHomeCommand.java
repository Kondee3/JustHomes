package me.kondi.JustHomes.Commands;


import me.clip.placeholderapi.PlaceholderAPI;
import me.kondi.JustHomes.Data.PlayerData;
import me.kondi.JustHomes.Home.Home;
import me.kondi.JustHomes.Home.HomeNames;
import me.kondi.JustHomes.JustHomes;
import me.kondi.JustHomes.Utils.Messages;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class SetHomeCommand {

    private JustHomes plugin;
    private String prefix;
    
    private PlayerData playerData;
    private Material[] damageBlocksMaterials = {Material.CACTUS, Material.FIRE, Material.CAMPFIRE, Material.SOUL_FIRE, Material.SOUL_CAMPFIRE, Material.MAGMA_BLOCK,
            Material.SWEET_BERRY_BUSH, Material.WITHER_ROSE, Material.LAVA, Material.POWDER_SNOW};
    private List<Material> damageBlocks = Arrays.asList(damageBlocksMaterials);

    public SetHomeCommand(JustHomes plugin) {
        this.plugin = plugin;
        this.prefix = plugin.prefix;

        this.playerData = plugin.playerData;
    }

    public SetHomeCommand() {
    }


    /**
     * Creates player's home.
     * @param p Player whose home will be created.
     * @param args Arguments including name of player's home.
     */
    public void set(Player p, String[] args) {

        String uuid = p.getUniqueId().toString();

        if (plugin.simpleProtection) {
            Material middle = p.getLocation().getBlock().getType();
            Material below = p.getWorld().getBlockAt(p.getLocation().getBlockX(), p.getLocation().getBlockY() - 1, p.getLocation().getBlockZ()).getType();
            if (damageBlocks.contains(below) || damageBlocks.contains(middle) || middle == Material.NETHER_PORTAL) {
                p.sendMessage(prefix + Messages.get("SetOnlyOnGroundException"));
                return;
            }
        }

        if (args.length == 0) {
            p.sendMessage(prefix + Messages.get("SpecifyHomeNameException"));
            return;
        }

        List<Home> playerHomes = playerData.getListOfHomes(uuid);

        if (playerHomes.size() == 0) {
            saveLoc(p, args[0]);
            p.sendMessage(prefix + PlaceholderAPI.setPlaceholders(p, Messages.get("CreatedHome")));
        } else {

            Home home = playerData.getHome(uuid, args[0]);
            if (home != null) {
                replaceLoc(p, home);
                p.sendMessage(prefix + PlaceholderAPI.setPlaceholders(p, Messages.get("EditedHome")));
                return;
            }
            if (playerHomes.size() >= plugin.permissionChecker.checkHomesMaxAmount(p)) {
                p.sendMessage(prefix + Messages.get("TooMuchHomesException"));
                return;
            }

            saveLoc(p, args[0]);
            p.sendMessage(prefix + PlaceholderAPI.setPlaceholders(p, Messages.get("CreatedHome")));
        }
    }


    public void saveLoc(Player p, String homeName) {
        String uuid = p.getUniqueId().toString();
        HomeNames.addHomeName(uuid, homeName);
        Home home = new Home(uuid, homeName, p.getLocation().getWorld().getName(), p.getLocation().getX(), p.getLocation().getY(), p.getLocation().getZ(), p.getLocation().getPitch(), p.getLocation().getYaw());
        playerData.addHome(home);
    }

    public void replaceLoc(Player p, Home home) {
        String uuid = p.getUniqueId().toString();
        HomeNames.addHomeName(uuid, home.getHomeName());
        Home newHome = new Home(uuid, home.getHomeName(), p.getLocation().getWorld().getName(), p.getLocation().getX(), p.getLocation().getY(), p.getLocation().getZ(), p.getLocation().getPitch(), p.getLocation().getYaw());
        playerData.replaceHome(home, newHome);
    }

}
