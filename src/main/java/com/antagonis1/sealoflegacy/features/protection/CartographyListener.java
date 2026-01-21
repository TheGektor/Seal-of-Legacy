package com.antagonis1.sealoflegacy.features.protection;

import com.antagonis1.sealoflegacy.SealOfLegacy;
import com.antagonis1.sealoflegacy.features.sealing.SealedItemManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.CartographyInventory;
import org.bukkit.inventory.ItemStack;

public class CartographyListener implements Listener {

    private final SealOfLegacy plugin;

    public CartographyListener(SealOfLegacy plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) return;
        if (event.getClickedInventory().getType() != InventoryType.CARTOGRAPHY) return;
        
        // Slot 2 is result in Cartography table usually
        if (event.getSlot() != 2) return;
        
        CartographyInventory inv = (CartographyInventory) event.getClickedInventory();
        ItemStack mapItem = inv.getItem(0);
        ItemStack secondItem = inv.getItem(1); // Glass pane or empty map
        
        SealedItemManager manager = plugin.getSealedItemManager();
        
        if (manager.isSealed(mapItem) || manager.isSealed(secondItem)) {
            if (event.getWhoClicked().hasPermission("itemlock.bypass")) return;
            event.setCancelled(true);
        }
    }
}
