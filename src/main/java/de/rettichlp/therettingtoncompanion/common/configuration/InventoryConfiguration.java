package de.rettichlp.therettingtoncompanion.common.configuration;

import lombok.Data;

@Data
public class InventoryConfiguration {

    private boolean instantQuickMove = true;
    private boolean autoRestock = true;
}
