package com.guoche.teyvatdelight.api.harvest;

import net.neoforged.bus.api.Event;

/** Server game-bus events. Neither event cancels interaction or changes the block reset. */
public abstract class HarvestResultEvent extends Event {
    private final HarvestContext context;
    private final HarvestResult result;
    protected HarvestResultEvent(HarvestContext context, HarvestResult result) {
        this.context = context;
        this.result = result;
    }
    public final HarvestContext context() { return context; }
    public final HarvestResult result() { return result; }

    /** After datapack rules, before emission. Listener logic should only modify the result. */
    public static final class Modify extends HarvestResultEvent {
        public Modify(HarvestContext context, HarvestResult result) { super(context, result); }
    }

    /** Read-only finalized result, not proof that a player picked up an item. */
    public static final class Resolved extends HarvestResultEvent {
        private final boolean emitted;
        public Resolved(HarvestContext context, HarvestResult result, boolean emitted) {
            super(context, result.snapshot());
            this.emitted = emitted;
        }
        /** False for native loot calculations, which may also be requested without breaking. */
        public boolean emitted() { return emitted; }
    }
}
