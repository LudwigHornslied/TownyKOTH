package xyz.ludwicz.townykoth.commands;

import com.google.common.collect.ImmutableList;
import com.palmergames.bukkit.towny.exceptions.AlreadyRegisteredException;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.utils.NameUtil;
import com.palmergames.bukkit.util.ChatTools;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import xyz.ludwicz.townykoth.KOTH;
import xyz.ludwicz.townykoth.Messaging;
import xyz.ludwicz.townykoth.TownyKOTH;

import java.util.Collections;
import java.util.List;

public class KOTHCommand implements CommandExecutor, TabCompleter {

    private static final List<String> kothTabCompletes = ImmutableList.of(
            "help",
            "create",
            "remove",
            "delete",
            "set",
            "teleport",
            "tp",
            "activate",
            "start",
            "deactivate",
            "stop"
    );

    private static final List<String> kothSetTabCompletes = ImmutableList.of(
            "location",
            "loc",
            "distance",
            "dist",
            "captime",
            "name"
    );

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 1) {
            return NameUtil.filterByStart(kothTabCompletes, args[0]);
        } else {
            switch (args[0].toLowerCase()) {
                case "remove":
                case "delete":
                case "teleport":
                case "tp":
                case "activate":
                case "start":
                case "deactivate":
                case "stop":
                    if (args.length == 2)
                        return NameUtil.filterByStart(TownyKOTH.getInstance().getKothHandler().getKothNames(), args[1]);

                    break;
                case "set":
                    if (args.length == 2)
                        return NameUtil.filterByStart(kothSetTabCompletes, args[1]);

                    break;
            }
        }

        return Collections.emptyList();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            for (KOTH koth : TownyKOTH.getInstance().getKothHandler().getKoths()) {
                if (!koth.isActive())
                    continue;
            }
        } else {
            switch (args[0].toLowerCase()) {
                case "?":
                case "help":
                    showHelp(sender);
                    break;
                case "create":
                    if (!(sender instanceof Player)) {
                        Messaging.sendErrorMsg(sender, Messaging.PLAYER_ONLY);
                        break;
                    }
                    parseKothCreate((Player) sender, args);
                    break;
                case "remove":
                case "delete":
                    parseKothDelete(sender, args);
                    break;
                case "set":
                    parseKothSet(sender, args);
                    break;
                case "teleport":
                case "tp":
                    if (!(sender instanceof Player)) {
                        Messaging.sendErrorMsg(sender, Messaging.PLAYER_ONLY);
                        break;
                    }
                    parseKothTeleport((Player) sender, args);
                    break;
                case "activate":
                case "start":
                    parseKothStart(sender, args);
                    break;
                case "deactivate":
                case "stop":
                    parseKothStop(sender, args);
                    break;
                default:
                    break;
            }
        }
        return false;
    }

    private void showHelp(CommandSender sender) {
        sender.sendMessage(ChatTools.formatTitle("/koth"));
        sender.sendMessage(ChatTools.formatCommand("Admin", "/koth", "create [name]", ""));
        sender.sendMessage(ChatTools.formatCommand("Admin", "/koth", "delete [koth]", ""));
        sender.sendMessage(ChatTools.formatCommand("Admin", "/koth", "set [] .. []", "'/koth set' for help"));
        sender.sendMessage(ChatTools.formatCommand("Admin", "/koth", "teleport [koth]", ""));
        sender.sendMessage(ChatTools.formatCommand("Admin", "/koth", "activate [koth]", ""));
        sender.sendMessage(ChatTools.formatCommand("Admin", "/koth", "deactivate [koth]", ""));
    }

    private void parseKothCreate(Player sender, String[] args) {
        try {
            if (!sender.hasPermission("townykoth.admin"))
                throw new Exception(Messaging.NO_PERMISSION);

            if (args.length < 2)
                throw new Exception(String.format("Usage: /koth create [name]"));

            if (TownyKOTH.getInstance().getKothHandler().getKoth(args[1]) != null)
                new AlreadyRegisteredException("The koth with name " + args[1] + " already exists!");

            TownyKOTH.getInstance().getKothHandler().newKoth(args[1], sender.getLocation());
            Messaging.sendMsg(sender, ChatColor.AQUA + "Koth " + args[1] + " has been created.");
        } catch (Exception e) {
            Messaging.sendErrorMsg(sender, e.getMessage());
        }
    }

    private void parseKothDelete(CommandSender sender, String[] args) {
        try {
            if (!sender.hasPermission("townykoth.admin"))
                throw new Exception(Messaging.NO_PERMISSION);

            if (args.length < 2)
                throw new Exception(String.format("Usage: /koth delete [koth]"));

            KOTH koth = TownyKOTH.getInstance().getKothHandler().getKoth(args[1]);
            if (koth == null)
                throw new NotRegisteredException(String.format(Messaging.DOESNT_EXIST, args[1]));

            TownyKOTH.getInstance().getKothHandler().deleteKoth(koth);
            Messaging.sendMsg(sender, ChatColor.AQUA + "Koth " + koth.getName() + " has been deleted.");
        } catch (Exception e) {
            Messaging.sendErrorMsg(sender, e.getMessage());
        }
    }

    private void parseKothSet(CommandSender sender, String[] args) {
        try {
            if (!sender.hasPermission("townykoth.admin"))
                throw new Exception(Messaging.NO_PERMISSION);

            if (args.length < 3) {
                showSetHelp(sender);
                return;
            }

            if (args[1].equalsIgnoreCase("location") || args[1].equalsIgnoreCase("loc")) {
                KOTH koth = TownyKOTH.getInstance().getKothHandler().getKoth(args[2]);
                if (koth == null)
                    throw new NotRegisteredException(String.format(Messaging.DOESNT_EXIST, args[2]));

                if (!(sender instanceof Player))
                    throw new Exception(Messaging.PLAYER_ONLY);

                Player player = (Player) sender;
                koth.setCapLocation(player.getLocation());
                TownyKOTH.getInstance().getKothHandler().save();
                Messaging.sendMsg(sender, ChatColor.AQUA + "Set cap location for " + koth.getName() + " Koth.");
            } else if (args[1].equalsIgnoreCase("distance") || args[1].equalsIgnoreCase("dist")) {
                KOTH koth = TownyKOTH.getInstance().getKothHandler().getKoth(args[2]);
                if (koth == null)
                    throw new NotRegisteredException(String.format(Messaging.DOESNT_EXIST, args[2]));

                if (args.length < 4)
                    throw new Exception("Usage: /koth set distance [koth] [number]");

                int dist;
                try {
                    dist = Integer.parseInt(args[3]);
                } catch (NumberFormatException e) {
                    throw new Exception("Usage: /koth set distance [koth] [number]");
                }

                koth.setCapDistance(dist);
                TownyKOTH.getInstance().getKothHandler().save();
                Messaging.sendMsg(sender, ChatColor.AQUA + "Set max distance for " + koth.getName() + " Koth.");
            } else if (args[1].equalsIgnoreCase("captime")) {
                KOTH koth = TownyKOTH.getInstance().getKothHandler().getKoth(args[2]);
                if (koth == null)
                    throw new NotRegisteredException(String.format(Messaging.DOESNT_EXIST, args[2]));

                if (args.length < 4)
                    throw new Exception("Usage: /koth set captime [koth] [minutes]");

                int minutes;
                try {
                    minutes = Integer.parseInt(args[3]);
                } catch (NumberFormatException e) {
                    throw new Exception("Usage: /koth set captime [koth] [minutes]");
                }

                if(minutes < 1)
                    throw new Exception("Koth cap time can't be negative or 0.");

                koth.setCapTime(minutes * 60 * 1000);
                TownyKOTH.getInstance().getKothHandler().save();
                Messaging.sendMsg(sender, ChatColor.AQUA + "Koth cap time has been set to " + minutes + " minutes.");
            } else if (args[1].equalsIgnoreCase("name")) {
                KOTH koth = TownyKOTH.getInstance().getKothHandler().getKoth(args[2]);
                if (koth == null)
                    throw new NotRegisteredException(String.format(Messaging.DOESNT_EXIST, args[2]));

                if (args.length < 4)
                    throw new Exception("Usage: /koth set name [koth] [newname]");

                String newName = args[3];
                if(TownyKOTH.getInstance().getKothHandler().getKoth(newName) != null)
                    new AlreadyRegisteredException("The koth with name " + newName + " already exists!");


            } else {
                showSetHelp(sender);
            }

        } catch (Exception e) {
            Messaging.sendErrorMsg(sender, e.getMessage());
        }
    }

    private void showSetHelp(CommandSender sender) {
        sender.sendMessage(ChatTools.formatTitle("/koth set"));
    }

    private void parseKothTeleport(Player sender, String[] args) {
        try {
            if (args.length < 2)
                throw new Exception(String.format("Usage: /koth teleport [koth]"));

            KOTH koth = TownyKOTH.getInstance().getKothHandler().getKoth(args[1]);
            if (koth == null)
                throw new NotRegisteredException(String.format(Messaging.DOESNT_EXIST, args[1]));

            Location location = koth.getCapLocationBukkit();
            if(location == null)
                throw new Exception("An error has occured! The world might have not been loaded?");

            sender.teleport(location);
        } catch (Exception e) {
            Messaging.sendErrorMsg(sender, e.getMessage());
        }
    }

    private void parseKothStart(CommandSender sender, String[] args) {
        try {
            if (!sender.hasPermission("townykoth.admin"))
                throw new Exception(Messaging.NO_PERMISSION);

            if (args.length < 2)
                throw new Exception(String.format("Usage: /koth start [koth]"));

            KOTH koth = TownyKOTH.getInstance().getKothHandler().getKoth(args[1]);
            if (koth == null)
                throw new NotRegisteredException(String.format(Messaging.DOESNT_EXIST, args[1]));

            koth.activate();
        } catch (Exception e) {
            Messaging.sendErrorMsg(sender, e.getMessage());
        }
    }

    private void parseKothStop(CommandSender sender, String[] args) {
        try {
            if (!sender.hasPermission("townykoth.admin"))
                throw new Exception(Messaging.NO_PERMISSION);

            if (args.length < 2)
                throw new Exception(String.format("Usage: /koth stop [koth]"));

            KOTH koth = TownyKOTH.getInstance().getKothHandler().getKoth(args[1]);
            if (koth == null)
                throw new NotRegisteredException(String.format(Messaging.DOESNT_EXIST, args[1]));
        } catch (Exception e) {
            Messaging.sendErrorMsg(sender, e.getMessage());
        }
    }
}
