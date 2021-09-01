package xyz.ludwicz.townykoth;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class Messaging {

    public static final String KOTH_PREFIX = ChatColor.GOLD + "[KOTH] ";

    public static final String PLAYER_ONLY = "Only players can use this command.";
    public static final String NO_PERMISSION = "You don't have permission to use this command.";
    public static final String DOESNT_EXIST = "There's no KOTH with the name %s.";

    public static void sendErrorMsg(CommandSender sender, String message) {
        if(sender instanceof ConsoleCommandSender) {
            sender.sendMessage(ChatColor.stripColor(message));
        } else {
            sender.sendMessage(KOTH_PREFIX + ChatColor.RED + message);
        }
    }

    public static void sendMsg(CommandSender sender, String message) {
        if(sender instanceof ConsoleCommandSender) {
            sender.sendMessage(ChatColor.stripColor(message));
        } else {
            sender.sendMessage(KOTH_PREFIX + ChatColor.AQUA + message);
        }
    }

    public static void sendGlobalMsg(String message) {
        for(Player online : Bukkit.getOnlinePlayers())
            online.sendMessage(KOTH_PREFIX + ChatColor.YELLOW + message);
    }
}
