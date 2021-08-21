package xyz.ludwicz.townykoth.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@AllArgsConstructor
public class KOTHControlLostEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    @Getter
    private xyz.ludwicz.townykoth.KOTH KOTH;

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
