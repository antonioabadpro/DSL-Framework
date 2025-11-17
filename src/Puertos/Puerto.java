package Puertos;

import Slots.Slot;

public abstract class Puerto {
    
    protected Slot slot;

    public Puerto(Slot slot) {
        this.slot = slot;
    }

    public Slot getSlot() {
        return this.slot;
    }
    
}
