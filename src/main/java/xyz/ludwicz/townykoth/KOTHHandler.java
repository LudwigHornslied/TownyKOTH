package xyz.ludwicz.townykoth;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import org.apache.commons.io.FileUtils;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.HashSet;
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
        koths.add(new KOTH(name, location));
        save();
    }

    public KOTH getKoth(String name) {
        for (KOTH koth : koths) {
            if (koth.getName().equalsIgnoreCase(name))
                return koth;
        }

        return null;
    }
}
