package xyz.ludwicz.townykoth.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import xyz.ludwicz.townykoth.KOTH;

@RequiredArgsConstructor
public class KOTHControlStartEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Getter
    @Setter
    private boolean cancelled = false;

    @Getter
    private final KOTH koth;
    @Getter
    private final Player capper;
}
