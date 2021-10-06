package xyz.ludwicz.townykoth;

import com.gmail.goosius.siegewar.SiegeWar;
import com.palmergames.bukkit.util.Version;
import io.github.townyadvanced.flagwar.FlagWar;
import io.github.townyadvanced.flagwar.FlagWarAPI;
import lombok.Getter;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.dynmap.DynmapAPI;
import xyz.ludwicz.townykoth.commands.KOTHCommand;
import xyz.ludwicz.townykoth.commands.LootCommand;
import xyz.ludwicz.townykoth.listeners.BukkitListener;
import xyz.ludwicz.townykoth.listeners.FlagWarListener;
import xyz.ludwicz.townykoth.listeners.KOTHListener;
import xyz.ludwicz.townykoth.listeners.TownyListener;
import xyz.ludwicz.townykoth.loot.LootHandler;
import xyz.ludwicz.townykoth.task.DynmapTask;

public class TownyKOTH extends JavaPlugin {

    @Getter
    private static TownyKOTH instance;

    @Getter
    private LootHandler lootHandler;
    @Getter
    private KOTHHandler kothHandler;

    @Getter
    private DynmapAPI dynmap;
    @Getter
    private SiegeWar siegeWar;
    @Getter
    private FlagWar flagWar;

    @Override
    public void onEnable() {
        instance = this;
        checkTownyVersion();
        saveDefaultConfig();

        setupHandlers();
        setupListeners();
        setupCommands();
        setupDynmap();
        setupSiegeWar();
        setupFlagWar();
        setupMetrics();
    }

    private void checkTownyVersion() {
        Version requiredVersion = Version.fromString("0.97.1.7");
        Version townyVersion = Version.fromString(Bukkit.getPluginManager().getPlugin("Towny").getDescription().getVersion());

        if(townyVersion.compareTo(requiredVersion) >= 0) {
            getLogger().info("Towny version checked: " + townyVersion.toString());
            return;
        }

        getLogger().severe("Towny version is lower than the required version.");
        Bukkit.getPluginManager().disablePlugin(this);
    }

    private void setupHandlers() {
        lootHandler = new LootHandler();
        kothHandler = new KOTHHandler();
    }

    private void setupListeners() {
        Bukkit.getPluginManager().registerEvents(new BukkitListener(), this);
        Bukkit.getPluginManager().registerEvents(new KOTHListener(), this);
        Bukkit.getPluginManager().registerEvents(new TownyListener(), this);
    }

    private void setupCommands() {
        getCommand("loot").setExecutor(new LootCommand());
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
            getLogger().severe("An error has occured while loading Dynmap.");
        }
    }

    private void setupSiegeWar() {
        try {
            Plugin siegeWar = Bukkit.getPluginManager().getPlugin("SiegeWar");
            if (siegeWar == null) {
                getLogger().info("SiegeWar plugin not found.");
            } else {
                getLogger().info("SiegeWar has been hooked.");

                this.siegeWar = (SiegeWar) siegeWar;
            }
        } catch (Exception e) {
            getLogger().severe("An error has occured while loading SiegeWar.");
        }
    }

    private void setupFlagWar() {
        try {
            Plugin flagWar = Bukkit.getPluginManager().getPlugin("FlagWar");
            if (flagWar == null) {
                getLogger().info("FlagWar plugin not found.");
            } else {
                getLogger().info("FlagWar has been hooked.");

                this.flagWar = (FlagWar) flagWar;

                Bukkit.getPluginManager().registerEvents(new FlagWarListener(), this);
            }
        } catch (Exception e) {
            getLogger().severe("An error has occured while loading FlagWar.");
        }
    }

    private void setupMetrics() {
        Metrics metrics = new Metrics(this, 12959);
    }
}
