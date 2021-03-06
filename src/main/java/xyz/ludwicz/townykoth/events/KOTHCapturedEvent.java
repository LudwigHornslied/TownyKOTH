package xyz.ludwicz.townykoth.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import xyz.ludwicz.townykoth.KOTH;

@AllArgsConstructor
public class KOTHCapturedEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Getter
    private final KOTH koth;
    @Getter
    private final Player capper;
}
