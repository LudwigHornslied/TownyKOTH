package xyz.ludwicz.townykoth.commands;

import com.google.common.collect.ImmutableList;
import com.palmergames.bukkit.towny.Towny;
import com.palmergames.bukkit.towny.TownyMessaging;
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
import xyz.ludwicz.townykoth.loot.Loot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class KOTHCommand implements CommandExecutor, TabCompleter {

    private static final List<String> kothTabCompletes = ImmutableList.of(
            "help",
            "list",
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
            "help",
            "location",
            "loc",
            "distance",
            "dist",
            "captime",
            "loot",
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
                    if (args.length == 2) {
                        return NameUtil.filterByStart(kothSetTabCompletes, args[1]);
                    } else if (args.length == 3) {
                        switch (args[1]) {
                            case "location":
                            case "loc":
                            case "distance":
                            case "dist":
                            case "captime":
                            case "loot":
                            case "name":
                                return NameUtil.filterByStart(TownyKOTH.getInstance().getKothHandler().getKothNames(), args[2]);
                        }
                    } else if (args.length == 4) {
                        switch (args[1]) {
                            case "loot":
                                return NameUtil.filterByStart(TownyKOTH.getInstance().getLootHandler().getLootNames(), args[3]);
                        }
                    }
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

                Messaging.sendMsg(sender, ChatColor.BLUE + koth.getName() + ChatColor.YELLOW + " can be contested.");
            }
        } else {
            switch (args[0].toLowerCase()) {
                case "?":
                case "help":
                    showHelp(sender);
                    break;
                case "list":
                    parseKothList(sender, args);
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
        sender.sendMessage(ChatTools.formatCommand("", "/koth", "list", ""));
        sender.sendMessage(ChatTools.formatCommand("Admin", "/koth", "create [name]", ""));
        sender.sendMessage(ChatTools.formatCommand("Admin", "/koth", "delete [koth]", ""));
        sender.sendMessage(ChatTools.formatCommand("Admin", "/koth", "set [] .. []", "'/koth set' for help"));
        sender.sendMessage(ChatTools.formatCommand("Admin", "/koth", "teleport [koth]", ""));
        sender.sendMessage(ChatTools.formatCommand("Admin", "/koth", "activate [koth]", ""));
        sender.sendMessage(ChatTools.formatCommand("Admin", "/koth", "deactivate [koth]", ""));
    }

    private static final int KOTH_PER_PAGE = 7;

    private void parseKothList(CommandSender sender, String[] args) {
        try {
            if (!sender.hasPermission("townykoth.command.koth.list"))
                throw new Exception(Messaging.NO_PERMISSION);

            int page;
            if (args.length < 2) {
                page = 1;
            } else {
                try {
                    page = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    throw new Exception(String.format("Usage: /koth list [page]"));
                }
            }

            if (page <= 0)
                throw new Exception("Invalid page.");

            List<KOTH> kothList = new ArrayList<>(TownyKOTH.getInstance().getKothHandler().getKoths());

            int startIndex = KOTH_PER_PAGE * (page - 1);
            if (startIndex >= kothList.size())
                throw new Exception("Invalid page.");

            sender.sendMessage(ChatTools.formatTitle(ChatColor.YELLOW + "Koths"));
            for (int index = startIndex, i = 0; index < kothList.size() && i < KOTH_PER_PAGE; index++, i++) {
                KOTH koth = kothList.get(index);
                sender.sendMessage(ChatColor.DARK_AQUA + koth.getName());
            }
            Towny.getAdventure().sender(sender).sendMessage(TownyMessaging.getPageNavigationFooter("townykoth:koth list", page, "", kothList.size() / KOTH_PER_PAGE + 1));
        } catch (Exception e) {
            Messaging.sendErrorMsg(sender, e.getMessage());
        }
    }

    private void parseKothCreate(Player sender, String[] args) {
        try {
            if (!sender.hasPermission("townykoth.command.koth.create"))
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
            if (!sender.hasPermission("townykoth.command.koth.delete"))
                throw new Exception(Messaging.NO_PERMISSION);

            if (args.length < 2)
                throw new Exception(String.format("Usage: /koth delete [koth]"));

            KOTH koth = TownyKOTH.getInstance().getKothHandler().getKoth(args[1]);
            if (koth == null)
                throw new NotRegisteredException(String.format(Messaging.KOTH_DOESNT_EXIST, args[1]));

            if (koth.isActive())
                throw new Exception("Koth is active! You should deactivate the koth before deleting it.");

            TownyKOTH.getInstance().getKothHandler().deleteKoth(koth);
            Messaging.sendMsg(sender, ChatColor.AQUA + "Koth " + koth.getName() + " has been deleted.");
        } catch (Exception e) {
            Messaging.sendErrorMsg(sender, e.getMessage());
        }
    }

    private void parseKothSet(CommandSender sender, String[] args) {
        try {
            if (args.length < 3) {
                showSetHelp(sender);
                return;
            }

            if (args[1].equalsIgnoreCase("help") || args[1].equals("?")) {
                showSetHelp(sender);
            } else if (args[1].equalsIgnoreCase("location") || args[1].equalsIgnoreCase("loc")) {
                if (!sender.hasPermission("townykoth.command.koth.set.location"))
                    throw new Exception(Messaging.NO_PERMISSION);

                KOTH koth = TownyKOTH.getInstance().getKothHandler().getKoth(args[2]);
                if (koth == null)
                    throw new NotRegisteredException(String.format(Messaging.KOTH_DOESNT_EXIST, args[2]));

                if (koth.isActive())
                    throw new Exception("Koth is active! You should deactivate the koth before doing this.");

                if (!(sender instanceof Player))
                    throw new Exception(Messaging.PLAYER_ONLY);

                Player player = (Player) sender;
                koth.setCapLocation(player.getLocation());
                TownyKOTH.getInstance().getKothHandler().save();
                Messaging.sendMsg(sender, ChatColor.AQUA + "Set cap location for " + koth.getName() + " Koth.");
            } else if (args[1].equalsIgnoreCase("distance") || args[1].equalsIgnoreCase("dist")) {
                if (!sender.hasPermission("townykoth.command.koth.set.distance"))
                    throw new Exception(Messaging.NO_PERMISSION);

                KOTH koth = TownyKOTH.getInstance().getKothHandler().getKoth(args[2]);
                if (koth == null)
                    throw new NotRegisteredException(String.format(Messaging.KOTH_DOESNT_EXIST, args[2]));

                if (koth.isActive())
                    throw new Exception("Koth is active! You should deactivate the koth before doing this.");

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
                if (!sender.hasPermission("townykoth.command.koth.set.captime"))
                    throw new Exception(Messaging.NO_PERMISSION);

                KOTH koth = TownyKOTH.getInstance().getKothHandler().getKoth(args[2]);
                if (koth == null)
                    throw new NotRegisteredException(String.format(Messaging.KOTH_DOESNT_EXIST, args[2]));

                if (koth.isActive())
                    throw new Exception("Koth is active! You should deactivate the koth before doing this.");

                if (args.length < 4)
                    throw new Exception("Usage: /koth set captime [koth] [minutes]");

                int minutes;
                try {
                    minutes = Integer.parseInt(args[3]);
                } catch (NumberFormatException e) {
                    throw new Exception("Usage: /koth set captime [koth] [minutes]");
                }

                if (minutes < 1)
                    throw new Exception("Koth cap time can't be negative or 0.");

                koth.setCapTime(minutes * 60 * 1000);
                TownyKOTH.getInstance().getKothHandler().save();
                Messaging.sendMsg(sender, ChatColor.AQUA + "Koth cap time has been set to " + minutes + " minutes.");
            } else if (args[1].equalsIgnoreCase("loot")) {
                if (!sender.hasPermission("townykoth.command.koth.set.loot"))
                    throw new Exception(Messaging.NO_PERMISSION);

                KOTH koth = TownyKOTH.getInstance().getKothHandler().getKoth(args[2]);
                if (koth == null)
                    throw new NotRegisteredException(String.format(Messaging.KOTH_DOESNT_EXIST, args[2]));

                if (args.length < 4)
                    throw new Exception("Usage: /koth set loot [koth] [loot]");

                Loot loot = TownyKOTH.getInstance().getLootHandler().getLoot(args[3]);
                if (loot == null)
                    throw new NotRegisteredException(String.format(Messaging.LOOT_DOESNT_EXIST, args[3]));

                koth.setLoot(loot.getName());
                Messaging.sendMsg(sender, "Koth loot has been set to " + loot.getName() + ".");
            } else if (args[1].equalsIgnoreCase("name")) {
                if (!sender.hasPermission("townykoth.command.koth.set.name"))
                    throw new Exception(Messaging.NO_PERMISSION);

                KOTH koth = TownyKOTH.getInstance().getKothHandler().getKoth(args[2]);
                if (koth == null)
                    throw new NotRegisteredException(String.format(Messaging.KOTH_DOESNT_EXIST, args[2]));

                if (koth.isActive())
                    throw new Exception("Koth is active! You should deactivate the koth before doing this.");

                if (args.length < 4)
                    throw new Exception("Usage: /koth set name [koth] [newname]");

                String newName = args[3];
                if (TownyKOTH.getInstance().getKothHandler().getKoth(newName) != null)
                    new AlreadyRegisteredException("The koth with name " + newName + " already exists!");

                koth.setName(newName);
                TownyKOTH.getInstance().getKothHandler().save();
                Messaging.sendMsg(sender, ChatColor.AQUA + "Koth name has been changed to " + koth.getName() + ".");
            } else {
                showSetHelp(sender);
            }

        } catch (Exception e) {
            Messaging.sendErrorMsg(sender, e.getMessage());
        }
    }

    private void showSetHelp(CommandSender sender) {
        sender.sendMessage(ChatTools.formatTitle("/koth set"));
        sender.sendMessage(ChatTools.formatCommand("Admin", "/koth set", "name [koth] [newname]", ""));
        sender.sendMessage(ChatTools.formatCommand("Admin", "/koth set", "location [koth]", ""));
        sender.sendMessage(ChatTools.formatCommand("Admin", "/koth set", "distance [koth] [distance]", ""));
        sender.sendMessage(ChatTools.formatCommand("Admin", "/koth set", "captime [koth] [minutes]", ""));
        sender.sendMessage(ChatTools.formatCommand("Admin", "/koth set", "loot [koth] [loot]", ""));
    }

    private void parseKothTeleport(Player sender, String[] args) {
        try {
            if (!sender.hasPermission("townykoth.command.koth.teleport"))
                throw new Exception(Messaging.NO_PERMISSION);

            if (args.length < 2)
                throw new Exception(String.format("Usage: /koth teleport [koth]"));

            KOTH koth = TownyKOTH.getInstance().getKothHandler().getKoth(args[1]);
            if (koth == null)
                throw new NotRegisteredException(String.format(Messaging.KOTH_DOESNT_EXIST, args[1]));

            Location location = koth.getCapLocationBukkit();
            if (location == null)
                throw new Exception("An error has occured! The world might have not been loaded?");

            sender.teleport(location);
        } catch (Exception e) {
            Messaging.sendErrorMsg(sender, e.getMessage());
        }
    }

    private void parseKothStart(CommandSender sender, String[] args) {
        try {
            if (!sender.hasPermission("townykoth.command.koth.start"))
                throw new Exception(Messaging.NO_PERMISSION);

            if (args.length < 2)
                throw new Exception(String.format("Usage: /koth start [koth]"));

            KOTH koth = TownyKOTH.getInstance().getKothHandler().getKoth(args[1]);
            if (koth == null)
                throw new NotRegisteredException(String.format(Messaging.KOTH_DOESNT_EXIST, args[1]));

            koth.activate();
        } catch (Exception e) {
            Messaging.sendErrorMsg(sender, e.getMessage());
        }
    }

    private void parseKothStop(CommandSender sender, String[] args) {
        try {
            if (!sender.hasPermission("townykoth.command.koth.stop"))
                throw new Exception(Messaging.NO_PERMISSION);

            if (args.length < 2)
                throw new Exception(String.format("Usage: /koth stop [koth]"));

            KOTH koth = TownyKOTH.getInstance().getKothHandler().getKoth(args[1]);
            if (koth == null)
                throw new NotRegisteredException(String.format(Messaging.KOTH_DOESNT_EXIST, args[1]));

            if (!koth.isActive())
                throw new Exception("Koth isn't active.");

            koth.deactivate(true);
        } catch (Exception e) {
            Messaging.sendErrorMsg(sender, e.getMessage());
        }
    }
}
