package com.antagonis1.sealoflegacy.features.commands;

import com.antagonis1.sealoflegacy.SealOfLegacy;
import com.antagonis1.sealoflegacy.utils.MessageManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class LockReloadCommand implements CommandExecutor {

    private final SealOfLegacy plugin;

    public LockReloadCommand(SealOfLegacy plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("itemlock.admin")) {
            MessageManager.sendMessage(sender, plugin.getConfig().getString("messages.no-permission"));
            return true;
        }
        
        plugin.reloadConfig();
        MessageManager.sendMessage(sender, plugin.getConfig().getString("messages.reload-success"));
        return true;
    }
}
