package com.antagonis1.sealoflegacy.features.sealing;

import com.antagonis1.sealoflegacy.SealOfLegacy;
import com.antagonis1.sealoflegacy.utils.MessageManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SealedItemManager {

    private final SealOfLegacy plugin;
    private final SealingDAO sealingDAO;
    
    // NBT Keys
    private final NamespacedKey KEY_SEALED;
    private final NamespacedKey KEY_OWNER;
    private final NamespacedKey KEY_TIMESTAMP;
    private final NamespacedKey KEY_SIGNATURE;
    private final NamespacedKey KEY_ITEM_UUID;

    public SealedItemManager(SealOfLegacy plugin) {
        this.plugin = plugin;
        this.sealingDAO = new SealingDAO(plugin.getDatabaseManager(), plugin.getLogger());
        
        this.KEY_SEALED = new NamespacedKey(plugin, "sealed");
        this.KEY_OWNER = new NamespacedKey(plugin, "owner");
        this.KEY_TIMESTAMP = new NamespacedKey(plugin, "timestamp");
        this.KEY_SIGNATURE = new NamespacedKey(plugin, "signature");
        this.KEY_ITEM_UUID = new NamespacedKey(plugin, "item_uuid");
    }

    public boolean isSealed(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
        return pdc.has(KEY_SEALED, PersistentDataType.BYTE);
    }
    
    public UUID getOwner(ItemStack item) {
        if (!isSealed(item)) return null;
        PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
        String uuidStr = pdc.get(KEY_OWNER, PersistentDataType.STRING);
        return uuidStr != null ? UUID.fromString(uuidStr) : null;
    }

    public void sealItem(Player player, ItemStack item) {
        if (item == null || item.getType().isAir()) return;
        
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        
        UUID itemUuid = UUID.randomUUID();
        String timestamp = String.valueOf(System.currentTimeMillis());
        String signature = UUID.randomUUID().toString().substring(0, 8); // Simple signature
        
        pdc.set(KEY_SEALED, PersistentDataType.BYTE, (byte) 1);
        pdc.set(KEY_OWNER, PersistentDataType.STRING, player.getUniqueId().toString());
        pdc.set(KEY_TIMESTAMP, PersistentDataType.STRING, timestamp);
        pdc.set(KEY_SIGNATURE, PersistentDataType.STRING, signature);
        pdc.set(KEY_ITEM_UUID, PersistentDataType.STRING, itemUuid.toString());
        
        // Add Lore
        List<Component> lore = meta.lore();
        if (lore == null) lore = new ArrayList<>();
        
        String loreLine = plugin.getConfig().getString("settings.lore-line", "<gradient:red:dark_red>🔒 ЗАПЕЧАТАНО</gradient>");
        lore.add(MessageManager.parse(loreLine));
        meta.lore(lore);
        
        item.setItemMeta(meta); // Apply meta
        
        // Save to DB asynchronously
        sealingDAO.saveSealedItem(itemUuid, player.getUniqueId(), signature);
    }

    public void unsealItem(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return;
        
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        
        if (!pdc.has(KEY_SEALED, PersistentDataType.BYTE)) return;
        
        String itemUuidStr = pdc.get(KEY_ITEM_UUID, PersistentDataType.STRING);
        
        pdc.remove(KEY_SEALED);
        pdc.remove(KEY_OWNER);
        pdc.remove(KEY_TIMESTAMP);
        pdc.remove(KEY_SIGNATURE);
        pdc.remove(KEY_ITEM_UUID);
        
        // Remove Lore
        List<Component> lore = meta.lore();
        if (lore != null) {
            // Needed to identify and remove the specific line. 
            // For simplicity, we might remove the last line if we know it's ours, 
            // or better, reconstruct lore without the sealed line.
            // Since MiniMessage components don't easily equal string config, 
            // we will assume it's the specific visual we added. 
            // A more robust way is to strip the lore line? 
            // For now, let's just leave it or try to remove the last line if it matches?
            // User requested "Lore: Заблокировано".
            // Implementation: Remove the specific configured line if found.
            // But strict equality with components is hard. 
            // Let's remove the *last* line if we assume it was added last.
            // Or better, we can modify the lore list.
             if (!lore.isEmpty()) {
                 // Removing the last line is a safe heuristic if we just added it.
                 // But multiple seals? (Check blocked).
                 lore.remove(lore.size() - 1); 
             }
        }
        meta.lore(lore);
        item.setItemMeta(meta);

        if (itemUuidStr != null) {
            sealingDAO.deleteSealedItem(UUID.fromString(itemUuidStr));
        }
    }
    
    public SealingDAO getDao() {
        return sealingDAO;
    }
}
