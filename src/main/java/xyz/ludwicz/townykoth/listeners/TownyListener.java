package xyz.ludwicz.townykoth.listeners;

import com.palmergames.bukkit.towny.event.statusscreen.TownStatusScreenEvent;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.util.ChatTools;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import xyz.ludwicz.townykoth.KOTH;
import xyz.ludwicz.townykoth.TownyKOTH;

import java.util.ArrayList;
import java.util.List;

public class TownyListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onTownInfo(TownStatusScreenEvent event) {
        Town town = event.getTown();
        KOTH koth = TownyKOTH.getInstance().getKothHandler().getKoth(town.getName());
        if(koth == null)
            return;

        List<String> out = new ArrayList<>();
        out.add(ChatTools.formatTitle(ChatColor.YELLOW + town.getFormattedName() + " " + ChatColor.RED + "(KOTH)"));
        out.add(ChatColor.DARK_GREEN + "Location: " + ChatColor.GREEN + koth.getCapLocation().getX() + ", " + koth.getCapLocation().getZ() + " (" + koth.getWorld() + ")");

        event.setLines(out);
    }
}
