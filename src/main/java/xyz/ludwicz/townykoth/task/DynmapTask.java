package xyz.ludwicz.townykoth.task;

import org.bukkit.scheduler.BukkitRunnable;
import org.dynmap.DynmapAPI;
import org.dynmap.markers.Marker;
import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerIcon;
import org.dynmap.markers.MarkerSet;
import xyz.ludwicz.townykoth.KOTH;
import xyz.ludwicz.townykoth.TownyKOTH;
import xyz.ludwicz.townykoth.util.BlockCoord;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class DynmapTask extends BukkitRunnable {

    private static DynmapAPI dynmapAPI;
    private static MarkerAPI markerAPI;

    private static MarkerSet markerSet;

    private static Map<KOTH, Marker> kothMarkerMap = new ConcurrentHashMap<>();

    public static void initialize() {
        dynmapAPI = TownyKOTH.getInstance().getDynmap();
        markerAPI = dynmapAPI.getMarkerAPI();

        markerSet = markerAPI.getMarkerSet("townykoth.markerset");
        if (markerSet == null)
            markerSet = markerAPI.createMarkerSet("townykoth.markerset", "TownyKOTH", null, false);
        markerSet.setMarkerSetLabel("TownyKOTH");

        markerAPI.createMarkerIcon("crown", "Crown", TownyKOTH.getInstance().getResource("crown.png"));

        Set<KOTH> koths = TownyKOTH.getInstance().getKothHandler().getKoths();

        for (KOTH koth : koths) {
            kothMarkerMap.put(koth, createMarker(koth));
        }

        new DynmapTask().runTaskTimerAsynchronously(TownyKOTH.getInstance(), 100L, 300L);
    }

    @Override
    public void run() {
        updateKothMarkers();
    }

    private static void updateKothMarkers() {
        Iterator<KOTH> iterator = kothMarkerMap.keySet().iterator();

        while (iterator.hasNext()) {
            KOTH koth = iterator.next();
            Marker marker = kothMarkerMap.get(koth);

            if(marker.getMarkerID().equals(koth.getName())) {
                updateMarker(koth, marker);
            } else {
                iterator.remove();

                kothMarkerMap.put(koth, createMarker(koth));
            }
        }
    }

    private static Marker createMarker(KOTH koth) {
        MarkerIcon icon;
        if (koth.isActive()) {
            icon = markerAPI.getMarkerIcon("fire");
        } else {
            icon = markerAPI.getMarkerIcon("crown");
        }

        BlockCoord coord = koth.getCapLocation();

        Marker marker = markerSet.createMarker(koth.getName(), koth.getName() + " KOTH", koth.getWorld(), coord.getX(), coord.getY(), coord.getZ(), icon, false);

        marker.setLabel(koth.getName() + " KOTH");

        return marker;
    }

    private static void updateMarker(KOTH koth, Marker marker) {
        MarkerIcon icon;
        if (koth.isActive()) {
            icon = markerAPI.getMarkerIcon("fire");
        } else {
            icon = markerAPI.getMarkerIcon("crown");
        }

        BlockCoord coord = koth.getCapLocation();

        marker.setMarkerIcon(icon);
        marker.setLocation(koth.getWorld(), coord.getX(), coord.getY(), coord.getZ());
    }

    public static void newMarker(KOTH koth) {
        kothMarkerMap.put(koth, createMarker(koth));
    }

    public static void removeMarker(KOTH koth) {
        kothMarkerMap.remove(koth).deleteMarker();
    }
}
