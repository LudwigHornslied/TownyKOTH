package xyz.ludwicz.townykoth.listeners;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import xyz.ludwicz.townykoth.Messaging;
import xyz.ludwicz.townykoth.events.KOTHControlLostEvent;

public class KOTHListener implements Listener {

    @EventHandler
    public void onKnock(KOTHControlLostEvent event) {
        Messaging.sendGlobalMsg("Control of " + ChatColor.BLUE + event.getKOTH().getName() + ChatColor.YELLOW + " lost.");
    }
}
