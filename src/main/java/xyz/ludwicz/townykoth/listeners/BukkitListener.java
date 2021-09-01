package xyz.ludwicz.townykoth.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import xyz.ludwicz.townykoth.TownyKOTH;

public class BukkitListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        TownyKOTH.getInstance().getKothHandler().onJoin(event.getPlayer());
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {

    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {

    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {

    }
}
