package xyz.ludwicz.townykoth;

import com.gmail.goosius.siegewar.SiegeWar;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.dynmap.DynmapAPI;
import xyz.ludwicz.townykoth.commands.KOTHCommand;
import xyz.ludwicz.townykoth.listeners.BukkitListener;
import xyz.ludwicz.townykoth.listeners.KOTHListener;
import xyz.ludwicz.townykoth.listeners.TownyListener;
import xyz.ludwicz.townykoth.task.DynmapTask;

public class TownyKOTH extends JavaPlugin {

    @Getter
    private static TownyKOTH instance;

    @Getter
    private KOTHHandler kothHandler;

    @Getter
    private DynmapAPI dynmap;
    @Getter
    private SiegeWar siegeWar;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        setupHandlers();
        setupListeners();
        setupCommands();
        setupDynmap();
        setupSiegeWar();
    }

    private void setupHandlers() {
        kothHandler = new KOTHHandler();
    }

    private void setupListeners() {
        Bukkit.getPluginManager().registerEvents(new BukkitListener(), this);
        Bukkit.getPluginManager().registerEvents(new KOTHListener(), this);
        Bukkit.getPluginManager().registerEvents(new TownyListener(), this);
    }

    private void setupCommands() {
        getCommand("koth").setExecutor(new KOTHCommand());
    }

    private void setupDynmap() {
        try {
            Plugin dynmap = Bukkit.getPluginManager().getPlugin("dynmap");
            if (dynmap == null) {
                getLogger().info("Dynmap plugin not found.");
            } else {
                getLogger().info("Dynmap has been hooked.");

                this.dynmap = (DynmapAPI) dynmap;

                DynmapTask.initialize();
            }
        } catch (Exception e) {
            getLogger().warning("An error has occured while loading Dynmap.");
        }
    }

    private void setupSiegeWar() {
        try {
            Plugin siegeWar = Bukkit.getPluginManager().getPlugin("SiegeWar");
            if (dynmap == null) {
                getLogger().info("SiegeWar plugin not found.");
            } else {
                getLogger().info("SiegeWar has been hooked.");

                this.siegeWar = (SiegeWar) siegeWar;
            }
        } catch (Exception e) {
            getLogger().warning("An error has occured while loading SiegeWar.");
        }
    }
}
