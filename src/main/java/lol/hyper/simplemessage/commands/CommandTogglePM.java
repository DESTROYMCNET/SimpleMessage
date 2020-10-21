package lol.hyper.simplemessage.commands;

import lol.hyper.simplemessage.SimpleMessage;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandTogglePM implements CommandExecutor {

    private final SimpleMessage simpleMessage;

    public CommandTogglePM(SimpleMessage simpleMessage) {
        this.simpleMessage = simpleMessage;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
        if (simpleMessage.privateMessagesOff.contains(player)) {
            simpleMessage.privateMessagesOff.remove(player);
            sender.sendMessage(ChatColor.GOLD + "Private messages are now on.");
        } else if (!simpleMessage.privateMessagesOff.contains(player)) {
            simpleMessage.privateMessagesOff.add(player);
            sender.sendMessage(ChatColor.GOLD + "Private messages are now off.");
        }
        return true;
    }
}
