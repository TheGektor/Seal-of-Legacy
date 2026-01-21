package com.antagonis1.sealoflegacy.features.commands;

import com.antagonis1.sealoflegacy.SealOfLegacy;
import com.antagonis1.sealoflegacy.utils.MessageManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class LockCheckCommand implements CommandExecutor {

    private final SealOfLegacy plugin;

    public LockCheckCommand(SealOfLegacy plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players.");
            return true;
        }

        if (!player.hasPermission("itemlock.check")) {
             MessageManager.sendMessage(player, plugin.getConfig().getString("messages.no-permission"));
             return true;
        }

        ItemStack item = player.getInventory().getItemInMainHand();
        boolean sealed = plugin.getSealedItemManager().isSealed(item);
        
        if (sealed) {
            MessageManager.sendMessage(player, "<green>This item is sealed.");
        } else {
            MessageManager.sendMessage(player, "<red>This item is NOT sealed.");
        }
        return true;
    }
}
