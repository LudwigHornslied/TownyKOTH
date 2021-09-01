package xyz.ludwicz.townykoth.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import xyz.ludwicz.townykoth.KOTH;

@AllArgsConstructor
public class KOTHDeactivatedEvent extends Event {

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
    private boolean terminate;
}
