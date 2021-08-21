package xyz.ludwicz.townykoth;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.ludwicz.townykoth.commands.KOTHCommand;
import xyz.ludwicz.townykoth.listeners.TownyListener;

public class TownyKOTH extends JavaPlugin {

    @Getter
    private static TownyKOTH instance;

    @Getter
    private KOTHHandler kothHandler;

    @Override
    public void onEnable() {
        instance = this;

        setupHandlers();
        setupListeners();
        setupCommands();
    }

    private void setupHandlers() {
        kothHandler = new KOTHHandler();
    }

    private void setupListeners() {
        Bukkit.getPluginManager().registerEvents(new TownyListener(), this);
    }

    private void setupCommands() {
        getCommand("koth").setExecutor(new KOTHCommand());
    }
}
