package xyz.ludwicz.townykoth.listeners;

import com.palmergames.adventure.text.Component;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.event.NewTownEvent;
import com.palmergames.bukkit.towny.event.damage.TownyFriendlyFireTestEvent;
import com.palmergames.bukkit.towny.event.statusscreen.TownStatusScreenEvent;
import com.palmergames.bukkit.towny.event.town.TownMapColourLocalCalculationEvent;
import com.palmergames.bukkit.towny.event.town.TownMapColourNationalCalculationEvent;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.statusscreens.StatusScreen;
import com.palmergames.bukkit.towny.utils.CombatUtil;
import com.palmergames.bukkit.util.ChatTools;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import xyz.ludwicz.townykoth.KOTH;
import xyz.ludwicz.townykoth.TownyKOTH;
import xyz.ludwicz.townykoth.util.TimeUtil;

import java.util.ArrayList;
import java.util.List;

public class TownyListener implements Listener {

    @EventHandler
    public void onTownCreate(NewTownEvent event) {
        KOTH koth = TownyKOTH.getInstance().getKothHandler().getKoth(event.getTown().getName());
        if (koth == null)
            return;

        koth.updateTown();
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onTownInfo(TownStatusScreenEvent event) {
        Town town = event.getTown();
        KOTH koth = TownyKOTH.getInstance().getKothHandler().getKoth(town.getName());
        if (koth == null)
            return;

        StatusScreen statusScreen = event.getStatusScreen();

        statusScreen.replaceComponent("title", Component.text(ChatTools.formatTitle(ChatColor.YELLOW + town.getFormattedName() + " " + ChatColor.RED + "(KOTH)")));

        statusScreen.removeStatusComponent("subtitle");
        statusScreen.removeStatusComponent("board");
        statusScreen.removeStatusComponent("registered");
        statusScreen.removeStatusComponent("townblocks");
        statusScreen.removeStatusComponent("perm");
        statusScreen.removeStatusComponent("explosion");
        statusScreen.removeStatusComponent("firespread");
        statusScreen.removeStatusComponent("mobsspawns");
        statusScreen.removeStatusComponent("mobspawns");
        statusScreen.removeStatusComponent("bankstring");
        statusScreen.removeStatusComponent("mayor");
        statusScreen.removeStatusComponent("nation");
        statusScreen.removeStatusComponent("newline");
        statusScreen.removeStatusComponent("townranks");
        statusScreen.removeStatusComponent("residents");
        statusScreen.removeStatusComponent("outposts");

        statusScreen.addComponentOf("location", Component.text(ChatColor.DARK_GREEN + "Location: " + ChatColor.GREEN + koth.getCapLocation().getX() + ", " + koth.getCapLocation().getZ() + " (" + koth.getWorld() + ")"));
        statusScreen.addComponentOf("captime", Component.text(ChatColor.DARK_GREEN + "Cap Time: " + ChatColor.GREEN + TimeUtil.formatTime(koth.getCapTime())));

        List<String> additionalLines = new ArrayList<>();
        if (koth.isActive()) {
            additionalLines.add(ChatColor.DARK_GREEN + "KOTH: ");
            additionalLines.add(ChatColor.DARK_GREEN + " > Cap Time: " + ChatColor.GREEN + TimeUtil.formatTime(koth.getCapTime()));
            if (koth.getCapper() == null) {
                additionalLines.add(ChatColor.DARK_GREEN + " > Capper: " + ChatColor.GREEN + "None");
            } else {
                Player capper = Bukkit.getPlayer(koth.getCapper());
                additionalLines.add(ChatColor.DARK_GREEN + " > Capper: " + ChatColor.GREEN + capper.getName());
                additionalLines.add(ChatColor.DARK_GREEN + " > Nation: " + ChatColor.GREEN + TownyAPI.getInstance().getResidentNationOrNull(TownyAPI.getInstance().getResident(capper)));
                additionalLines.add(ChatColor.DARK_GREEN + " > Remaining: " + ChatColor.GREEN + TimeUtil.formatTime(koth.getRemaining()));
            }

        }
        event.setLines(additionalLines);
    }

    @EventHandler
    public void onFriendlyFire(TownyFriendlyFireTestEvent event) {
        Player attacker = event.getAttacker();
        Player defender = event.getDefender();

        Resident attackerResident = TownyAPI.getInstance().getResident(attacker);
        Resident defenderResident = TownyAPI.getInstance().getResident(defender);

        if (TownyKOTH.getInstance().getKothHandler().getKoth(attacker.getLocation()) == null)
            return;

        if (TownyKOTH.getInstance().getKothHandler().getKoth(defender.getLocation()) == null)
            return;

        if (!CombatUtil.isSameTown(attackerResident, defenderResident) && !CombatUtil.isSameNation(attackerResident, defenderResident))
            return;

        event.setPVP(true);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onColorLocalCalculation(TownMapColourLocalCalculationEvent event) {
        if(!TownyKOTH.getInstance().getConfig().getBoolean("town_dynmap_color.enabled", true))
            return;

        if (TownyKOTH.getInstance().getKothHandler().getKoth(event.getTown().getName()) == null)
            return;

        event.setMapColorHexCode(TownyKOTH.getInstance().getConfig().getString("town_dynmap_color.local", "000000"));
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onColorNationCalculation(TownMapColourNationalCalculationEvent event) {
        if(!TownyKOTH.getInstance().getConfig().getBoolean("town_dynmap_color.enabled", true))
            return;

        if (TownyKOTH.getInstance().getKothHandler().getKoth(event.getTown().getName()) == null)
            return;

        event.setMapColorHexCode(TownyKOTH.getInstance().getConfig().getString("town_dynmap_color.nation", "000000"));
    }
}
