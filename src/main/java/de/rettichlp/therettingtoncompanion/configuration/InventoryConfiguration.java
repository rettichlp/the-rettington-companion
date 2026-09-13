package de.rettichlp.therettingtoncompanion.configuration;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class InventoryConfiguration {

    private boolean instantQuickMove = true;
    private boolean autoRestock = true;
    private Set<Integer> lockedSlots = new HashSet<>();
}
