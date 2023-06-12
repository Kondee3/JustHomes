package me.kondi.JustHomes.Commands;


import me.kondi.JustHomes.Home.Home;
import me.kondi.JustHomes.JustHomes;
import me.kondi.JustHomes.Utils.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Commands implements CommandExecutor, TabCompleter {
    private final JustHomes plugin;
    private final String prefix;

    public Commands(JustHomes plugin) {
        this.plugin = plugin;
        this.prefix = plugin.prefix;

    }


    /**
     *
     * @param sender Source of the command
     * @param cmd Command which was executed
     * @param label Alias of the command which was used
     * @param args Passed command arguments
     * @return
     */
    @Override
    public boolean onCommand(@NotNull CommandSender sender, Command cmd, @NotNull String label, String[] args) {

        if (cmd.getName().equalsIgnoreCase("reloadlanguage")) {
            if (sender.isOp() || sender.hasPermission("justhomes.loadlanguage")) {
                Messages.reload();
                sender.sendMessage(prefix + Messages.get("ReloadLaguageFile"));
                return true;
            }
        }

        if (!(sender instanceof Player p)) {
            sender.sendMessage(prefix + Messages.get("NotHumanException"));
            return true;
        }
        if (cmd.getName().equalsIgnoreCase("listhome")) {
            if (p.hasPermission("justhomes.listhome"))
                plugin.listHome.getList(p);
        }
        if (cmd.getName().equalsIgnoreCase("delhome")) {
            if (p.hasPermission("justhomes.delhome"))
                plugin.deleteHome.delete(p, args);

        }

        if (cmd.getName().equalsIgnoreCase("sethome")) {
            if (p.hasPermission("justhomes.sethome"))
                plugin.setHome.set(p, args);
        }
        if (cmd.getName().equalsIgnoreCase("home")) {
            if (p.hasPermission("justhomes.home"))
                plugin.homeCommand.get(p, args);

        }


        return true;
    }


    /**
     *
     * @param sender Source of the command.  For players tab-completing a
     *     command inside a command block, this will be the player, not
     *     the command block.
     * @param cmd Command which was executed
     * @param arg2 Alias of the command which was used
     * @param args The arguments passed to the command, including final
     *     partial argument to be completed
     * @return
     */
    @Override
    public ArrayList<String> onTabComplete(@NotNull CommandSender sender, Command cmd, @NotNull String arg2, String[] args) {
        if (cmd.getName().equalsIgnoreCase("delhome") || cmd.getName().equalsIgnoreCase("home")) {

            if (args.length == 1) {

                ArrayList<String> homes = new ArrayList<>();
                if (!(sender instanceof Player p)) {
                    sender.sendMessage(prefix + Messages.get("NotPlayerException"));
                    return homes;
                }
                String uuid = p.getUniqueId().toString();
                if (plugin.playerData.getHomesAmount(uuid) == 0) {
                    return homes;
                }


                List<Home> keys = plugin.playerData.getListOfHomes(uuid);
                int homesMaxAmount = plugin.permissionChecker.checkHomesMaxAmount(p);
                if (keys.size() < homesMaxAmount) homesMaxAmount = keys.size();
                for (int i = 0; i < homesMaxAmount; i++) {
                    if (keys.get(i).getHomeName().startsWith(args[0].toLowerCase())) {
                        homes.add(keys.get(i).getHomeName());
                    }

                }


                return homes;


            }
        }
        return null;
    }
}
