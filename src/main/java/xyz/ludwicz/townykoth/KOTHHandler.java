package xyz.ludwicz.townykoth;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Town;
import lombok.Getter;
import org.apache.commons.io.FileUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.ludwicz.townykoth.task.DynmapTask;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class KOTHHandler {

    private static Gson GSON = new GsonBuilder().setPrettyPrinting().serializeNulls().create();

    @Getter
    private Set<KOTH> koths = new HashSet<>();

    public KOTHHandler() {
        load();

        new BukkitRunnable() {

            @Override
            public void run() {
                for (KOTH koth : koths)
                    if (koth.isActive())
                        koth.tick();
            }
        }.runTaskTimer(TownyKOTH.getInstance(), 5L, 5L);
    }

    public void load() {
        try {
            File directory = new File(TownyKOTH.getInstance().getDataFolder(), "koths");

            if (!directory.exists())
                directory.mkdirs();

            for (File file : directory.listFiles()) {
                if (!file.getName().endsWith(".json"))
                    continue;

                KOTH koth = GSON.fromJson(FileUtils.readFileToString(file, "UTF-8"), KOTH.class);
                koth.afterDeserealization();
                koths.add(koth);
                koth.updateTown();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void save() {
        try {
            File directory = new File(TownyKOTH.getInstance().getDataFolder(), "koths");

            if (!directory.exists())
                directory.mkdirs();

            for (File file : directory.listFiles())
                file.delete();

            for (KOTH koth : koths) {
                FileUtils.write(new File(directory, koth.getName() + ".json"), GSON.toJson(koth), "UTF-8");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void newKoth(String name, Location location) {
        KOTH koth = new KOTH(name, location);
        koths.add(koth);
        koth.afterDeserealization();
        save();

        koth.updateTown();
        DynmapTask.newMarker(koth);
    }

    public void deleteKoth(KOTH koth) {
        koths.remove(koth);
        save();

        DynmapTask.removeMarker(koth);
    }

    public void removeLoot(String loot) {
        for(KOTH koth : koths) {
            if(koth.getLoot() != null && koth.getLoot().equalsIgnoreCase(loot))
                koth.setLoot(null);
        }
    }

    public KOTH getKoth(String name) {
        for (KOTH koth : koths) {
            if (koth.getName().equalsIgnoreCase(name))
                return koth;
        }

        return null;
    }

    public KOTH getKoth(Location location) {
        if(TownyKOTH.getInstance().getConfig().getBoolean("koth_zone.include_townclaims", true)) {
            Town town = TownyAPI.getInstance().getTown(location);
            if(town != null) {
                for(KOTH koth : koths)
                    if(town.equals(koth.getTown()))
                        return koth;
            }
        }

        int radius = TownyKOTH.getInstance().getConfig().getInt("koth_zone.radius", 150);
        for(KOTH koth : koths) {
            double dist;
            try {
                dist = koth.getCapLocationBukkit().distance(location);
            } catch (IllegalArgumentException e) {
                continue;
            }

            if(dist <= radius)
                return koth;
        }

        return null;
    }

    public List<String> getKothNames() {
        ArrayList<String> kothNames = new ArrayList<>();

        for(KOTH koth : koths) {
            kothNames.add(koth.getName());
        }

        return kothNames;
    }

    public void onJoin(Player player) {
        for (KOTH koth : koths) {
            if(koth.isActive())
                koth.onJoin(player);
        }
    }
}
