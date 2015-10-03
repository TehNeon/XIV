package pw.latematt.xiv.event.events;

import pw.latematt.xiv.event.Cancellable;
import pw.latematt.xiv.event.Event;

/**
 * @author Matthew
 */
public class WorldBobbingEvent extends Event implements Cancellable {
    private boolean cancelled;

    public WorldBobbingEvent() {
        this.setCancelled(true); // The Alimighty Underlord Latematt said to just disable this completely.
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}