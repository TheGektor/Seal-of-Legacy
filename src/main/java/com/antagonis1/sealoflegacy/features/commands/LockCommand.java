package com.antagonis1.sealoflegacy.features.commands;

import com.antagonis1.sealoflegacy.SealOfLegacy;
import com.antagonis1.sealoflegacy.features.sealing.SealedItemManager;
import com.antagonis1.sealoflegacy.utils.MessageManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class LockCommand implements CommandExecutor {

    private final SealOfLegacy plugin;

    public LockCommand(SealOfLegacy plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        SealedItemManager manager = plugin.getSealedItemManager();

        if (args.length > 0) {
            // Subcommands
            switch (args[0].toLowerCase()) {
                case "force":
                    if (!player.hasPermission("itemlock.admin")) {
                         MessageManager.sendMessage(player, plugin.getConfig().getString("messages.no-permission"));
                         return true;
                    }
                    // Force lock logic similar to lock but bypass some checks?
                    // For now, standard lock but explicitly requested.
                    // Or maybe lock others item? Not specified deeply. assuming item in hand.
                    sealHand(player, manager); 
                    return true;

                case "list":
                     if (!player.hasPermission("itemlock.admin")) {
                         MessageManager.sendMessage(player, plugin.getConfig().getString("messages.no-permission"));
                         return true;
                    }
                    // Async list
                    manager.getDao().getSealedItemsByOwner(player.getUniqueId()).thenAccept(items -> {
                         // Simple dummy list for now or proper pagination if needed.
                         MessageManager.sendMessage(player, "<gold>Sealed items count: " + items.size());
                         // TODO: Expand to show details
                    });
                    return true;
                    
                case "purge":
                     if (!player.hasPermission("itemlock.admin")) {
                         MessageManager.sendMessage(player, plugin.getConfig().getString("messages.no-permission"));
                         return true;
                    }
                    manager.getDao().purgeAll().thenRun(() -> {
                        MessageManager.sendMessage(player, "<green>All seals purged from database (items in world still allow unseal but are technically orphaned in DB).");
                    });
                    return true;
                    
                default:
                    MessageManager.sendMessage(player, "<red>Unknown subcommand.");
                    return true;
            }
        }

        if (!player.hasPermission("itemlock.use")) {
            MessageManager.sendMessage(player, plugin.getConfig().getString("messages.no-permission"));
            return true;
        }

        sealHand(player, manager);
        return true;
    }
    
    private void sealHand(Player player, SealedItemManager manager) {
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType().isAir()) {
            MessageManager.sendMessage(player, plugin.getConfig().getString("messages.no-item"));
            return;
        }

        if (manager.isSealed(item)) {
            MessageManager.sendMessage(player, plugin.getConfig().getString("messages.already-sealed"));
            return;
        }
        
        // TODO: Check 'itemlock.exempt' if targeting other player? But here we lock own item.
        
        manager.sealItem(player, item);
        MessageManager.sendMessage(player, plugin.getConfig().getString("messages.sealed-success").replace("<id>", "N/A")); // ID is DB auto-inc, might not be ready instantly unless we fetch.
    }
}
