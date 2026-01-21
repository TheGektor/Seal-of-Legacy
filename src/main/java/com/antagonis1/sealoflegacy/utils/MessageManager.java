package com.antagonis1.sealoflegacy.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MessageManager {

    private static final MiniMessage miniMessage = MiniMessage.miniMessage();

    public static Component parse(String message) {
        return miniMessage.deserialize(message);
    }

    public static void sendMessage(CommandSender sender, String message) {
        if (message == null || message.isEmpty())
            return;
        sender.sendMessage(parse(message));
    }

    public static void sendMessage(Player player, String message) {
        if (message == null || message.isEmpty())
            return;
        player.sendMessage(parse(message));
    }

    public static String colorize(String text) {
        // Only for legacy support if absolutely needed, but we prefer MiniMessage
        // directly
        return text;
    }
}
