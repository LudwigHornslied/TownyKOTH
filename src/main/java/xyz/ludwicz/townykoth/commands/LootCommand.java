package xyz.ludwicz.townykoth.commands;

import com.google.common.collect.ImmutableList;
import com.palmergames.bukkit.towny.Towny;
import com.palmergames.bukkit.towny.TownyMessaging;
import com.palmergames.bukkit.towny.exceptions.AlreadyRegisteredException;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.utils.NameUtil;
import com.palmergames.bukkit.util.ChatTools;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import xyz.ludwicz.townykoth.Messaging;
import xyz.ludwicz.townykoth.TownyKOTH;
import xyz.ludwicz.townykoth.loot.Loot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class LootCommand implements CommandExecutor, TabCompleter {

    private static final List<String> lootTabCompletes = ImmutableList.of(
            "help",
            "list",
            "create",
            "remove",
            "delete",
            "edit",
            "command",
            "cmd"
    );

    private static final List<String> lootCmdTabCompletes = ImmutableList.of(
            "list",
            "add",
            "remove"
    );

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 1) {
            return NameUtil.filterByStart(lootTabCompletes, args[0]);
        } else {
            switch (args[0].toLowerCase()) {
                case "remove":
                case "delete":
                case "edit":
                    if (args.length == 2)
                        return NameUtil.filterByStart(TownyKOTH.getInstance().getLootHandler().getLootNames(), args[1]);

                    break;
                case "command":
                case "cmd":
                    if (args.length == 2)
                        return NameUtil.filterByStart(lootCmdTabCompletes, args[1]);

                    if (args.length == 3) {
                        switch (args[1].toLowerCase()) {
                            case "list":
                            case "add":
                            case "remove":
                                return NameUtil.filterByStart(TownyKOTH.getInstance().getLootHandler().getLootNames(), args[2]);
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
            showHelp(sender);
        } else {
            switch (args[0].toLowerCase()) {
                case "?":
                case "help":
                    showHelp(sender);
                    break;
                case "list":
                    parseLootList(sender, args);
                    break;
                case "create":
                    parseLootCreate(sender, args);
                    break;
                case "remove":
                case "delete":
                    parseLootDelete(sender, args);
                    break;
                case "edit":
                    if (!(sender instanceof Player)) {
                        Messaging.sendErrorMsg(sender, Messaging.PLAYER_ONLY);
                        break;
                    }
                    parseLootEdit((Player) sender, args);
                    break;
                case "cmd":
                case "command":
                    parseLootCommand(sender, args);
                    break;
            }
        }
        return true;
    }

    private void showHelp(CommandSender sender) {
        sender.sendMessage(ChatTools.formatTitle("/loot"));
        sender.sendMessage(ChatTools.formatCommand("Admin", "/loot", "list", ""));
        sender.sendMessage(ChatTools.formatCommand("Admin", "/loot", "create [name]", ""));
        sender.sendMessage(ChatTools.formatCommand("Admin", "/loot", "delete [loot]", ""));
        sender.sendMessage(ChatTools.formatCommand("Admin", "/loot", "edit [loot]", ""));
        sender.sendMessage(ChatTools.formatCommand("Admin", "/loot", "command [] .. []", "'/loot command' for help"));
    }

    private static final int LOOT_PER_PAGE = 7;

    private void parseLootList(CommandSender sender, String[] args) {
        try {
            if (!sender.hasPermission("townykoth.command.loot.list"))
                throw new Exception(Messaging.NO_PERMISSION);

            int page;
            if (args.length < 2) {
                page = 1;
            } else {
                try {
                    page = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    throw new Exception(String.format("Usage: /loot list [page]"));
                }
            }

            if (page <= 0)
                throw new Exception("Invalid page.");

            List<Loot> lootList = new ArrayList<>(TownyKOTH.getInstance().getLootHandler().getLoots());

            int startIndex = LOOT_PER_PAGE * (page - 1);
            if (startIndex >= lootList.size())
                throw new Exception("Invalid page.");

            sender.sendMessage(ChatTools.formatTitle(ChatColor.YELLOW + "Loots"));
            for (int index = startIndex, i = 0; index < lootList.size() && i < LOOT_PER_PAGE; index++, i++) {
                Loot loot = lootList.get(index);
                sender.sendMessage(ChatColor.DARK_AQUA + loot.getName());
            }
            Towny.getAdventure().sender(sender).sendMessage(TownyMessaging.getPageNavigationFooter("townykoth:loot list", page, "", lootList.size() / LOOT_PER_PAGE + 1));
        } catch (Exception e) {
            Messaging.sendErrorMsg(sender, e.getMessage());
        }
    }

    private void parseLootCreate(CommandSender sender, String[] args) {
        try {
            if (!sender.hasPermission("townykoth.command.loot.create"))
                throw new Exception(Messaging.NO_PERMISSION);

            if (args.length < 2)
                throw new Exception(String.format("Usage: /loot create [name]"));

            if (TownyKOTH.getInstance().getLootHandler().getLoot(args[1]) != null)
                new AlreadyRegisteredException("The loot with name " + args[1] + " already exists!");

            TownyKOTH.getInstance().getLootHandler().newLoot(args[1]);
            Messaging.sendMsg(sender, ChatColor.AQUA + "Loot " + args[1] + " has been created.");
        } catch (Exception e) {
            Messaging.sendErrorMsg(sender, e.getMessage());
        }
    }

    private void parseLootDelete(CommandSender sender, String[] args) {
        try {
            if (!sender.hasPermission("townykoth.command.loot.delete"))
                throw new Exception(Messaging.NO_PERMISSION);

            if (args.length < 2)
                throw new Exception(String.format("Usage: /loot delete [koth]"));

            Loot loot = TownyKOTH.getInstance().getLootHandler().getLoot(args[1]);
            if (loot == null)
                throw new NotRegisteredException(String.format(Messaging.LOOT_DOESNT_EXIST, args[1]));

            TownyKOTH.getInstance().getLootHandler().deleteLoot(loot);
            TownyKOTH.getInstance().getKothHandler().removeLoot(loot.getName());
            Messaging.sendMsg(sender, ChatColor.AQUA + "Loot " + loot.getName() + " has been deleted.");
        } catch (Exception e) {
            Messaging.sendErrorMsg(sender, e.getMessage());
        }
    }

    private void parseLootEdit(Player sender, String[] args) {
        try {
            if (!sender.hasPermission("townykoth.command.loot.edit"))
                throw new Exception(Messaging.NO_PERMISSION);

            throw new Exception("Unsupported at the moment...");
        } catch (Exception e) {
            Messaging.sendErrorMsg(sender, e.getMessage());
        }
    }

    private void parseLootCommand(CommandSender sender, String[] args) {
        try {
            if (args.length < 3) {
                showCommandHelp(sender);
                return;
            }

            if (args[1].equalsIgnoreCase("help") || args[1].equals("?")) {
                showCommandHelp(sender);
            } else if (args[1].equalsIgnoreCase("list")) {
                if (!sender.hasPermission("townykoth.command.loot.command.list"))
                    throw new Exception(Messaging.NO_PERMISSION);

                Loot loot = TownyKOTH.getInstance().getLootHandler().getLoot(args[2]);
                if (loot == null)
                    throw new NotRegisteredException(String.format(Messaging.LOOT_DOESNT_EXIST, args[2]));

                sender.sendMessage(ChatTools.formatTitle("Loot " + loot.getName()));
                for (String command : loot.getCommands()) {
                    sender.sendMessage(ChatTools.formatCommand("", command, "", ""));
                }
            } else if (args[1].equalsIgnoreCase("add")) {
                if (!sender.hasPermission("townykoth.command.loot.command.add"))
                    throw new Exception(Messaging.NO_PERMISSION);

                Loot loot = TownyKOTH.getInstance().getLootHandler().getLoot(args[2]);
                if (loot == null)
                    throw new NotRegisteredException(String.format(Messaging.LOOT_DOESNT_EXIST, args[2]));

                if (args.length < 4)
                    throw new Exception("Usage: /loot command add [loot] [command]");

                String command = String.join(" ", Arrays.copyOfRange(args, 3, args.length));
                loot.getCommands().add(command);
                TownyKOTH.getInstance().getLootHandler().save();
                Messaging.sendMsg(sender, ChatColor.AQUA + "Command added.");
            } else if (args[1].equalsIgnoreCase("remove")) {
                if (!sender.hasPermission("townykoth.command.loot.command.remove"))
                    throw new Exception(Messaging.NO_PERMISSION);

                Loot loot = TownyKOTH.getInstance().getLootHandler().getLoot(args[2]);
                if (loot == null)
                    throw new NotRegisteredException(String.format(Messaging.LOOT_DOESNT_EXIST, args[2]));

                if (args.length < 4)
                    throw new Exception("Usage: /loot command remove [loot] [index]");

                int index;
                try {
                    index = Integer.parseInt(args[3]);
                } catch (NumberFormatException e) {
                    throw new Exception("Usage: /loot command remove [loot] [index]");
                }

                if (index < 0 || index >= loot.getCommands().size())
                    throw new Exception("Index is out of range");

                loot.getCommands().remove(index);
                TownyKOTH.getInstance().getLootHandler().save();
                Messaging.sendMsg(sender, ChatColor.AQUA + "Command removed.");
            }
        } catch (Exception e) {
            Messaging.sendErrorMsg(sender, e.getMessage());
        }
    }

    private void showCommandHelp(CommandSender sender) {
        sender.sendMessage(ChatTools.formatTitle("/loot command"));
        sender.sendMessage(ChatTools.formatCommand("Admin", "/loot command", "list [loot]", ""));
        sender.sendMessage(ChatTools.formatCommand("Admin", "/loot command", "add [loot] [command]", ""));
        sender.sendMessage(ChatTools.formatCommand("Admin", "/loot command", "remove [loot] [index]", ""));
    }
}
