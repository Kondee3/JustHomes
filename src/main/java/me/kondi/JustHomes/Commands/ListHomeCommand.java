package me.kondi.JustHomes.Commands;


import me.kondi.JustHomes.Home.Home;
import me.kondi.JustHomes.JustHomes;
import me.kondi.JustHomes.Permissions.PermissionChecker;
import me.kondi.JustHomes.PlayerData.PlayerData;
import me.kondi.JustHomes.Utils.Messages;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class ListHomeCommand {

    private JustHomes plugin;
    private String prefix;

    private PlayerData playerData;

    public ListHomeCommand(JustHomes plugin) {
        this.plugin = plugin;
        this.prefix = plugin.prefix;

        this.playerData = plugin.playerData;
    }

    /**
     * Lists player's homes.
     *
     * @param p Player whose homes will be listed in the chat (only for this player)
     */
    public void execute(Player p) {
        String uuid = p
                .getUniqueId()
                .toString();
        if (playerData.getHomesAmount(uuid) == 0) {
            p.sendMessage(prefix + Messages.get("UserHasNoHomes"));
            return;
        }

        List<Home> keys = playerData.getListOfHomes(uuid);
        int maxHomesAmount = PermissionChecker.checkHomesMaxAmount(p);
        if (keys.size() < maxHomesAmount) maxHomesAmount = keys.size();

        if (keys.size() == 0) {
            p.sendMessage(prefix + Messages.get("UserHasNoHomes"));
            return;
        }
        TextComponent titleAndData = new TextComponent(Messages.get("ListHomesTitle") + "\n");
        for (int i = 1; i < maxHomesAmount + 1; i++) {
            Home home = keys.get(i - 1);
            TextComponent indexText = new TextComponent(Messages.get("ListColorOfIndexNumber") + i + ". ");
            TextComponent homeText = new TextComponent(Messages.get("ListColorOfIndexName") + home.getHomeName());
            if (i != maxHomesAmount)
                homeText.addExtra("\n");
            homeText.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("World= " + Bukkit
                    .getWorld(home.getWorldName())
                    .getEnvironment()
                    .name() + "\n" +
                    "X= " + (int) home.getX() + "\n" +
                    "Y= " + (int) home.getY() + "\n" +
                    "Z= " + (int) home.getZ()
            )));
            indexText.addExtra(homeText);
            titleAndData.addExtra(indexText);
        }
        p
                .spigot()
                .sendMessage(titleAndData);


    }

}
