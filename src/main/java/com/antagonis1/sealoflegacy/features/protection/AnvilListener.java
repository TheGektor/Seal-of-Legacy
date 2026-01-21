package com.antagonis1.sealoflegacy.features.protection;

import com.antagonis1.sealoflegacy.SealOfLegacy;
import com.antagonis1.sealoflegacy.features.sealing.SealedItemManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;

public class AnvilListener implements Listener {

    private final SealOfLegacy plugin;

    public AnvilListener(SealOfLegacy plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        SealedItemManager manager = plugin.getSealedItemManager();
        ItemStack first = event.getInventory().getItem(0);
        ItemStack second = event.getInventory().getItem(1);

        if (manager.isSealed(first)) {
            if (event.getView().getPlayer().hasPermission("itemlock.bypass")) return;
            event.setResult(null); // Clear result to prevent taking it
            return;
        }

        // If verifying second item (e.g. merging two sealed items? or book on sealed item?)
        // If the second item is sealed (like a sealed book?), we should probably also block using it as ingredient?
        // User request: "Block copying ... with sealed book".
        if (manager.isSealed(second)) {
            if (event.getView().getPlayer().hasPermission("itemlock.bypass")) return;
            event.setResult(null);
        }
    }
}
