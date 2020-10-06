package lol.hyper.simplemessage.commands;

import lol.hyper.simplemessage.SimpleMessage;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandToggleChat implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
        if (SimpleMessage.getInstance().generalChatOff.contains(player)) {
            SimpleMessage.getInstance().generalChatOff.remove(player);
            sender.sendMessage(ChatColor.GOLD + "General chat is now on.");
        } else if (!SimpleMessage.getInstance().generalChatOff.contains(player)) {
            SimpleMessage.getInstance().generalChatOff.add(player);
            sender.sendMessage(ChatColor.GOLD + "General chat is now off.");
        }
        return true;
    }
}