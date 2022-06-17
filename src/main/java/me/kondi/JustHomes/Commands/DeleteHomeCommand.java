package me.kondi.JustHomes.Commands;

import me.clip.placeholderapi.PlaceholderAPI;
import me.kondi.JustHomes.Data.PlayerData;
import me.kondi.JustHomes.Home.Home;
import me.kondi.JustHomes.Home.HomeNames;
import me.kondi.JustHomes.JustHomes;
import me.kondi.JustHomes.Utils.Messages;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class DeleteHomeCommand {

    private final String prefix;

    private final PlayerData playerData;

    public DeleteHomeCommand(JustHomes plugin) {
        this.prefix = plugin.prefix;

        this.playerData = plugin.playerData;
    }


    public void delete(Player p, String[] args) {
        String uuid = p.getUniqueId().toString();

        if (args.length == 0) {
            p.sendMessage(prefix + Messages.get("SpecifyHomeNameException"));
            return;
        }
        if (playerData.countPlayerHomes(uuid) == 0) {
            p.sendMessage(prefix + Messages.get("UserHasNoHomes"));
            return;
        }
        String homeName = args[0];
        Home home = playerData.getHome(uuid, homeName);

        if (home == null) {
            p.sendMessage(prefix + Messages.get("UnknownHomeName"));
            return;
        }

        HomeNames.addHomeName(uuid, homeName);
        playerData.deleteHome(home);
        p.sendMessage(prefix + PlaceholderAPI.setPlaceholders(p, Messages.get("DeletedHome")));
    }

}



