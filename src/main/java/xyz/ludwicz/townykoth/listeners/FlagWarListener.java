package xyz.ludwicz.townykoth.listeners;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.WorldCoord;
import io.github.townyadvanced.flagwar.events.CellAttackEvent;
import io.github.townyadvanced.flagwar.objects.CellUnderAttack;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import xyz.ludwicz.townykoth.TownyKOTH;

public class FlagWarListener implements Listener {

    @EventHandler
    public void onFlag(CellAttackEvent event) {
        CellUnderAttack data = event.getData();

        TownBlock townBlock = TownyAPI.getInstance().getTownBlock(WorldCoord.parseWorldCoord(data.getWorldName(), data.getX(), data.getZ()));
        if (townBlock == null || !townBlock.hasTown())
            return;

        Town town = townBlock.getTownOrNull();
        if (TownyKOTH.getInstance().getKothHandler().getKoth(town.getName()) == null)
            return;

        event.setReason("Can't attack Koth town.");
        event.setCancelled(true);
    }
}
