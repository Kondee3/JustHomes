package me.kondi.JustHomes.Commands;

import me.kondi.JustHomes.Home.Home;
import me.kondi.JustHomes.JustHomes;
import me.kondi.JustHomes.Permissions.PermissionChecker;
import me.kondi.JustHomes.PlayerData.PlayerData;
import me.kondi.JustHomes.Utils.Messages;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class RenameHomeCommand {

    private final JustHomes plugin;
    private final String prefix;
    private PlayerData playerData;

    public RenameHomeCommand(JustHomes plugin) {
        this.plugin = plugin;
        this.prefix = plugin.prefix;
        this.playerData = plugin.playerData;
    }

    /**
     * Rename home.
     *
     * @param p    Player who want to change home name.
     * @param args Arguments including name of player's home and new name.
     */
    public void renameHome(Player p, String[] args) {

        String uuid = p.getUniqueId().toString();
        if (playerData.getHomesAmount(uuid) == 0) {
            p.sendMessage(prefix + Messages.get("UserHasNoHomes"));
            return;
        }

        if (args.length == 0) {
            p.sendMessage(prefix + Messages.get("SpecifyHomeNameException"));
            return;
        }
        if(args.length == 1) {
            p.sendMessage(prefix + Messages.get("SpecifyNewHomeNameException"));
            return;
        }
        if(playerData.getHome(uuid, args[1]) != null){
            p.sendMessage(prefix + Messages.get("DuplicateHomeNameException"));
            return;
        }
        String homeName = args[0];
        Home home = playerData.getHome(uuid, homeName);

        if (home == null) {
            p.sendMessage(prefix + Messages.get("UnknownHomeName"));
            return;
        }


        playerData.renameHome(home, args[1]);
        p.sendMessage(prefix + Messages.get("NameSuccessfullyChanged"));
    }


}
