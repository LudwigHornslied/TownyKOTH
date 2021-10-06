package xyz.ludwicz.townykoth.listeners;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Nation;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import xyz.ludwicz.townykoth.KOTH;
import xyz.ludwicz.townykoth.Messaging;
import xyz.ludwicz.townykoth.TownyKOTH;
import xyz.ludwicz.townykoth.events.KOTHActivatedEvent;
import xyz.ludwicz.townykoth.events.KOTHCapturedEvent;
import xyz.ludwicz.townykoth.events.KOTHControlLostEvent;
import xyz.ludwicz.townykoth.events.KOTHControlStartEvent;
import xyz.ludwicz.townykoth.events.KOTHDeactivatedEvent;
import xyz.ludwicz.townykoth.loot.Loot;

public class KOTHListener implements Listener {

    @EventHandler
    public void onActivated(KOTHActivatedEvent event) {
        Messaging.sendGlobalMsg(ChatColor.BLUE + event.getKoth().getName() + ChatColor.YELLOW + " can be contested now.");
    }

    @EventHandler
    public void onDeactivated(KOTHDeactivatedEvent event) {
        if(!event.isTerminate())
            return;

        Messaging.sendGlobalMsg(ChatColor.BLUE + event.getKoth().getName() + ChatColor.YELLOW + " was terminated.");
    }

    @EventHandler
    public void onStartCapping(KOTHControlStartEvent event) {
        Nation nation = TownyAPI.getInstance().getResident(event.getCapper()).getNationOrNull();
        if(nation == null) {
            event.setCancelled(true);
            return;
        }

        Messaging.sendGlobalMsg(ChatColor.GOLD + nation.getName() + ChatColor.YELLOW + " is controlling " + ChatColor.BLUE + event.getKoth().getName() + ChatColor.YELLOW + ".");
    }

    @EventHandler
    public void onKnock(KOTHControlLostEvent event) {
        Messaging.sendGlobalMsg("Control of " + ChatColor.BLUE + event.getKoth().getName() + ChatColor.YELLOW + " lost.");
    }

    @EventHandler
    public void onCaptured(KOTHCapturedEvent event) {
        Nation nation = TownyAPI.getInstance().getResident(event.getCapper()).getNationOrNull();
        if(nation == null)
            return;

        KOTH koth = event.getKoth();

        Messaging.sendGlobalMsg(ChatColor.BLUE + koth.getName() + ChatColor.YELLOW + " has been controlled by " + ChatColor.WHITE + "[" + ChatColor.GOLD + nation.getName() + ChatColor.WHITE + "] " + ChatColor.GRAY + event.getCapper().getDisplayName());

        if(koth.getLoot() == null)
            return;

        Loot loot = TownyKOTH.getInstance().getLootHandler().getLoot(koth.getLoot());
        if(loot == null)
            return;

        for(String command : loot.getCommands()) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replaceAll("<player>", event.getCapper().getName()).replaceAll("<nation>", nation.getName()));
        }
    }
}
