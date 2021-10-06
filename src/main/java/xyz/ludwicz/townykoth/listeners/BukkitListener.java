package xyz.ludwicz.townykoth.listeners;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import xyz.ludwicz.townykoth.KOTH;
import xyz.ludwicz.townykoth.TownyKOTH;

import java.util.Random;

public class BukkitListener implements Listener {

    private static final Random RANDOM = new Random();

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        TownyKOTH.getInstance().getKothHandler().onJoin(event.getPlayer());
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        if (!TownyKOTH.getInstance().getConfig().getBoolean("koth_zone.keep_inventory", true))
            return;

        if (TownyKOTH.getInstance().getKothHandler().getKoth(event.getEntity().getLocation()) == null)
            return;

        event.setKeepInventory(true);
        event.setKeepLevel(true);
    }

    @EventHandler
    public void onArmorDamage(PlayerItemDamageEvent event) {
        if (!TownyKOTH.getInstance().getConfig().getBoolean("koth_zone.reduce_armor_damage", true))
            return;

        if (TownyKOTH.getInstance().getKothHandler().getKoth(event.getPlayer().getLocation()) == null)
            return;

        if (50 < RANDOM.nextInt(100)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!TownyKOTH.getInstance().getConfig().getBoolean("koth_zone.prevent_building", true))
            return;

        if (TownyKOTH.getInstance().getKothHandler().getKoth(event.getBlock().getLocation()) == null)
            return;

        if (event.getPlayer().getGameMode() == GameMode.CREATIVE)
            return;

        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        if (!TownyKOTH.getInstance().getConfig().getBoolean("koth_zone.prevent_building", true))
            return;

        if (TownyKOTH.getInstance().getKothHandler().getKoth(event.getBlock().getLocation()) == null)
            return;

        if (event.getPlayer().getGameMode() == GameMode.CREATIVE)
            return;

        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBurn(BlockBurnEvent event) {
        if (!TownyKOTH.getInstance().getConfig().getBoolean("koth_zone.prevent_building", true))
            return;

        if (TownyKOTH.getInstance().getKothHandler().getKoth(event.getBlock().getLocation()) == null)
            return;

        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockIgnite(BlockIgniteEvent event) {
        if (!TownyKOTH.getInstance().getConfig().getBoolean("koth_zone.prevent_building", true))
            return;

        if (TownyKOTH.getInstance().getKothHandler().getKoth(event.getBlock().getLocation()) == null)
            return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockPistonExtend(BlockPistonExtendEvent event) {
        if (!TownyKOTH.getInstance().getConfig().getBoolean("koth_zone.prevent_building", true))
            return;

        for (Block block : event.getBlocks()) {
            Block target = block.getRelative(event.getDirection());

            if (TownyKOTH.getInstance().getKothHandler().getKoth(target.getLocation()) != null) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onBlockPistonRetract(BlockPistonRetractEvent event) {
        if (!TownyKOTH.getInstance().getConfig().getBoolean("koth_zone.prevent_building", true))
            return;

        for (Block block : event.getBlocks()) {
            Block target = block.getRelative(event.getDirection());

            if (TownyKOTH.getInstance().getKothHandler().getKoth(target.getLocation()) != null) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onCobweb(BlockPlaceEvent event) {
        Block block = event.getBlockPlaced();
        if (block.getType() != Material.COBWEB)
            return;

        KOTH koth = TownyKOTH.getInstance().getKothHandler().getKoth(block.getLocation());
        if (koth == null)
            return;

        if (!koth.isCapzone(block.getLocation()))
            return;

        Player player = event.getPlayer();
        player.getInventory().remove(Material.COBWEB);
        player.sendMessage(ChatColor.RED + "You can't use cobwebs on the capzone.");
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if(!event.getView().getTitle().equals("Edit Loot"))
            return;

        TownyKOTH.getInstance().getLootHandler().save();
    }
}
