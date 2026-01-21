package com.antagonis1.sealoflegacy.features.protection;

import com.antagonis1.sealoflegacy.SealOfLegacy;
import com.antagonis1.sealoflegacy.features.sealing.SealedItemManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.GrindstoneInventory;
import org.bukkit.inventory.ItemStack;

public class GrindstoneListener implements Listener {

    private final SealOfLegacy plugin;

    public GrindstoneListener(SealOfLegacy plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) return;
        if (event.getClickedInventory().getType() != InventoryType.GRINDSTONE) return;
        
        // Only care if they click the result slot (slot 2)
        if (event.getSlot() != 2) return;
        
        // Check input slots (0 and 1)
        GrindstoneInventory inv = (GrindstoneInventory) event.getClickedInventory();
        ItemStack upper = inv.getItem(0);
        ItemStack lower = inv.getItem(1);
        
        SealedItemManager manager = plugin.getSealedItemManager();
        
        // If any input is sealed, cancel the result take
        if (manager.isSealed(upper) || manager.isSealed(lower)) {
            if (event.getWhoClicked().hasPermission("itemlock.bypass")) return;
            event.setCancelled(true);
            // Optional: Message user?
        }
    }
}
