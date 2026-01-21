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

import java.util.UUID;

public class UnlockCommand implements CommandExecutor {

    private final SealOfLegacy plugin;

    public UnlockCommand(SealOfLegacy plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        if (!player.hasPermission("itemlock.unlock")) {
            MessageManager.sendMessage(player, plugin.getConfig().getString("messages.no-permission"));
            return true;
        }

        SealedItemManager manager = plugin.getSealedItemManager();
        ItemStack item = player.getInventory().getItemInMainHand();

        if (item.getType().isAir()) {
            MessageManager.sendMessage(player, plugin.getConfig().getString("messages.no-item"));
            return true;
        }

        if (!manager.isSealed(item)) {
            MessageManager.sendMessage(player, plugin.getConfig().getString("messages.not-sealed"));
            return true;
        }

        UUID owner = manager.getOwner(item);
        if (!player.getUniqueId().equals(owner) && !player.hasPermission("itemlock.admin")) {
             String msg = plugin.getConfig().getString("messages.not-owner", "<red>Not owner!");
             // Simple replace for now, in real app use proper placeholders
             msg = msg.replace("<owner>", owner != null ? owner.toString() : "Unknown");
             MessageManager.sendMessage(player, msg);
             return true;
        }

        manager.unsealItem(item);
        MessageManager.sendMessage(player, plugin.getConfig().getString("messages.unsealed-success"));
        return true;
    }
}
