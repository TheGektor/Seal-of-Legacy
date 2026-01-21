package com.antagonis1.sealoflegacy.features.protection;

import com.antagonis1.sealoflegacy.SealOfLegacy;
import com.antagonis1.sealoflegacy.features.sealing.SealedItemManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;

public class CraftingListener implements Listener {

    private final SealOfLegacy plugin;

    public CraftingListener(SealOfLegacy plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPrepareCraft(PrepareItemCraftEvent event) {
        SealedItemManager manager = plugin.getSealedItemManager();
        
        // Scan matrix for sealed items
        for (ItemStack item : event.getInventory().getMatrix()) {
            if (item != null && manager.isSealed(item)) {
                if (event.getView().getPlayer().hasPermission("itemlock.bypass")) return;
                
                // Determine logic: "Block usage in recipes (optional)" -> Yes, requested.
                // "Block copying finished books" -> Book copying is a recipe involving the book.
                // So blocking any recipe with sealed item effectively blocks book copying.
                
                event.getInventory().setResult(null);
                return;
            }
        }
    }
}
