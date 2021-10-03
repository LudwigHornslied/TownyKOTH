package xyz.ludwicz.townykoth;

import com.gmail.goosius.siegewar.metadata.TownMetaDataController;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Town;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import xyz.ludwicz.townykoth.events.KOTHActivatedEvent;
import xyz.ludwicz.townykoth.events.KOTHCapturedEvent;
import xyz.ludwicz.townykoth.events.KOTHControlLostEvent;
import xyz.ludwicz.townykoth.events.KOTHControlStartEvent;
import xyz.ludwicz.townykoth.events.KOTHDeactivatedEvent;
import xyz.ludwicz.townykoth.util.BlockCoord;
import xyz.ludwicz.townykoth.util.TimeUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class KOTH {

    @Getter
    @Setter
    private String name;
    @Getter
    private String world;
    @Getter
    private BlockCoord capLocation;
    @Getter
    @Setter
    private int capDistance;
    @Getter
    @Setter
    private long capTime;

    @Getter
    @Setter
    private transient boolean active;

    @Getter
    private transient UUID capper = null;
    @Getter
    private transient long capAt = -1;

    @Getter
    private transient BossBar bossBar;

    public KOTH(String name, Location location) {
        this.name = name;
        this.capDistance = 3;
        this.capTime = 60 * 15 * 1000;

        world = location.getWorld().getName();
        capLocation = BlockCoord.parseCoord(location);
    }

    public void afterDeserealization() {
        capper = null;
        capAt = -1;

        bossBar = Bukkit.createBossBar(ChatColor.BLUE.toString() + ChatColor.BOLD + name + " KOTH: " + ChatColor.YELLOW + TimeUtil.digitalTime(getRemaining()), BarColor.YELLOW, BarStyle.SOLID);
    }

    public void tick() {
        if (capper == null) {
            List<Player> playersOnCap = new ArrayList<>();

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (canCap(player))
                    playersOnCap.add(player);
            }

            Collections.shuffle(playersOnCap);

            if (playersOnCap.size() != 0)
                startCapping(playersOnCap.get(0));
        } else {
            Player capperPlayer = Bukkit.getPlayer(capper);
            if (capperPlayer == null || !canCap(capperPlayer)) {
                resetCap();
            } else if (System.currentTimeMillis() > capAt) {
                finishCapping(capperPlayer);
            }
        }

        long remaining = getRemaining();
        bossBar.setTitle(ChatColor.BLUE.toString() + ChatColor.BOLD + name + " KOTH: " + ChatColor.YELLOW + TimeUtil.digitalTime(remaining));
        bossBar.setProgress((double) remaining / capTime);
    }

    private boolean canCap(Player player) {
        return isCapzone(player.getLocation()) && player.isOnline() && !player.isDead() && player.getGameMode() == GameMode.SURVIVAL && TownyAPI.getInstance().getResidentNationOrNull(TownyAPI.getInstance().getResident(player)) != null;
    }

    public void startCapping(Player player) {
        KOTHControlStartEvent event = new KOTHControlStartEvent(this, player);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled())
            return;

        capper = player.getUniqueId();
        capAt = System.currentTimeMillis() + capTime;
    }

    public void finishCapping(Player player) {
        deactivate(false);

        Bukkit.getPluginManager().callEvent(new KOTHCapturedEvent(this, player));
    }

    public void resetCap() {
        capAt = -1;
        capper = null;

        Bukkit.getPluginManager().callEvent(new KOTHControlLostEvent(this));
    }

    public void activate() {
        if (active)
            return;

        active = true;
        capper = null;
        capAt = -1;

        Bukkit.getOnlinePlayers().forEach(player -> bossBar.addPlayer(player));
        bossBar.setVisible(true);

        Bukkit.getPluginManager().callEvent(new KOTHActivatedEvent(this));
    }

    public void deactivate(boolean terminate) {
        if (!active)
            return;

        active = false;
        capper = null;
        capAt = -1;

        bossBar.removeAll();
        bossBar.setVisible(false);

        Bukkit.getPluginManager().callEvent(new KOTHDeactivatedEvent(this, terminate));
    }

    public boolean isCapzone(Location location) {
        if (!location.getWorld().getName().equalsIgnoreCase(world))
            return false;

        int xDiff = Math.abs(location.getBlockX() - capLocation.getX());
        int yDiff = Math.abs(location.getBlockY() - capLocation.getY());
        int zDiff = Math.abs(location.getBlockZ() - capLocation.getZ());

        return xDiff <= capDistance && yDiff <= 5 && zDiff <= capDistance;
    }

    public long getRemaining() {
        if (capAt > 0) {
            return capAt - System.currentTimeMillis();
        } else {
            return capTime;
        }
    }

    public Town getTown() {
        return TownyAPI.getInstance().getTown(name);
    }

    public Location getCapLocationBukkit() {
        World world = Bukkit.getWorld(this.world);
        if (world == null)
            return null;

        return new Location(world, capLocation.getX(), capLocation.getY(), capLocation.getZ());
    }

    public void setCapLocation(Location location) {
        world = location.getWorld().getName();
        capLocation = BlockCoord.parseCoord(location);
    }

    public void updateTown() {
        Town town = getTown();
        if(town == null)
            return;

        town.setAdminEnabledPVP(true);

        if(TownyKOTH.getInstance().getSiegeWar() != null) {
            // Only if minecraft exists then....
            TownMetaDataController.setSiegeImmunityEndTime(town, 999999999999999L);
        }
    }

    public void onJoin(Player player) {
        bossBar.addPlayer(player);
    }
}
