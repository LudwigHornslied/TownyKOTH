package xyz.ludwicz.townykoth.commands;

import com.palmergames.bukkit.towny.exceptions.AlreadyRegisteredException;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.ludwicz.townykoth.KOTH;
import xyz.ludwicz.townykoth.Messaging;
import xyz.ludwicz.townykoth.TownyKOTH;

public class KOTHCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {

        } else {
            switch (args[0].toLowerCase()) {
                case "create":
                    if(!(sender instanceof Player)) {
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
                    break;
                case "activate":
                case "start":
                    parseKothStart(sender, args);
                    break;
                case "deactivate":
                case "stop":
                    break;
            }
        }
        return false;
    }

    private void parseKothCreate(Player sender, String[] args) {
        try {
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
            if (args.length < 2)
                throw new Exception(String.format("Usage: /koth delete [koth]"));
        } catch (Exception e) {
            Messaging.sendErrorMsg(sender, e.getMessage());
        }
    }

    private void parseKothSet() {

    }

    private void parseKothStart(CommandSender sender, String[] args) {
        try {
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

    private void parseKothStop() {

    }
}
