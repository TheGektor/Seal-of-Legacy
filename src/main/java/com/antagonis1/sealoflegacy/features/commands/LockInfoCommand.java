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

public class LockInfoCommand implements CommandExecutor {

    private final SealOfLegacy plugin;

    public LockInfoCommand(SealOfLegacy plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            return true;
        }
        
        if (!player.hasPermission("itemlock.info")) {
             MessageManager.sendMessage(player, plugin.getConfig().getString("messages.no-permission"));
             return true;
        }

        ItemStack item = player.getInventory().getItemInMainHand();
        SealedItemManager manager = plugin.getSealedItemManager();

        if (!manager.isSealed(item)) {
            MessageManager.sendMessage(player, plugin.getConfig().getString("messages.not-sealed"));
            return true;
        }

        MessageManager.sendMessage(player, plugin.getConfig().getString("messages.item-info.header"));
        MessageManager.sendMessage(player, plugin.getConfig().getString("messages.item-info.owner").replace("<owner>", String.valueOf(manager.getOwner(item))));
        // Add more info if needed
        MessageManager.sendMessage(player, plugin.getConfig().getString("messages.item-info.footer"));
        
        return true;
    }
}
