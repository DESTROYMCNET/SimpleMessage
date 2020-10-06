package lol.hyper.simplemessage.commands;

import lol.hyper.simplemessage.SimpleMessage;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandTogglePM implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
        if (SimpleMessage.getInstance().privateMessagesOff.contains(player)) {
            SimpleMessage.getInstance().privateMessagesOff.remove(player);
            sender.sendMessage(ChatColor.GOLD + "Private messages are now on.");
        } else if (!SimpleMessage.getInstance().privateMessagesOff.contains(player)) {
            SimpleMessage.getInstance().privateMessagesOff.add(player);
            sender.sendMessage(ChatColor.GOLD + "Private messages are now off.");
        }
        return true;
    }
}
