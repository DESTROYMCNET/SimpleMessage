package lol.hyper.simplemessage.commands;

import com.google.common.base.Strings;
import lol.hyper.simplemessage.SimpleMessage;
import lol.hyper.simplemessage.tools.IgnoreListHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandMessage implements CommandExecutor {

    private final SimpleMessage simpleMessage;
    private final IgnoreListHandler ignoreListHandler;

    public CommandMessage(SimpleMessage simpleMessage, IgnoreListHandler ignoreLists) {
        this.simpleMessage = simpleMessage;
        this.ignoreListHandler = ignoreLists;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check for message commands.
        if (command.getName().equalsIgnoreCase("msg") || command.getName().equalsIgnoreCase("tell") || command.getName().equalsIgnoreCase("w") || command.getName().equalsIgnoreCase("whisper")) {
            // Check if sender is player.
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "You must be a player for this command.");
            } else if (args.length < 2) {
                // Check valid syntax.
                sender.sendMessage(ChatColor.RED + "Invalid syntax. Do /msg <player> <message> instead.");
            } else {
                // Get who is sending the message to who.
                Player commandSender = Bukkit.getPlayerExact(sender.getName());
                Player commandReceiver = Bukkit.getPlayerExact(args[0]);
                // Check if they are not a real player or vanished.
                if (commandReceiver == null || !commandReceiver.isOnline() || simpleMessage.isVanished(commandReceiver.getName())) {
                    sender.sendMessage(ChatColor.RED + "That player was not found.");
                } else {
                    // Check if they are blocking each other.
                    boolean blockingReceiver;
                    boolean blockingSender;
                    if (ignoreListHandler.getPlayerIgnoreList(commandSender.getUniqueId()) == null) {
                        blockingReceiver = false;
                    } else {
                        blockingReceiver = ignoreListHandler.getPlayerIgnoreList(commandSender.getUniqueId()).contains(commandReceiver.getUniqueId());
                    }

                    if (ignoreListHandler.getPlayerIgnoreList(commandReceiver.getUniqueId()) == null) {
                        blockingSender = false;
                    } else {
                        blockingSender = ignoreListHandler.getPlayerIgnoreList(commandReceiver.getUniqueId()).contains(commandReceiver.getUniqueId());
                    }
                    boolean privateMessagesOffReceiver = simpleMessage.privateMessagesOff.contains(commandReceiver);
                    boolean privateMessagesOffSender = simpleMessage.privateMessagesOff.contains(commandSender);

                    // If they are not, send them the message.
                    if ((!blockingReceiver && !blockingSender) && (!privateMessagesOffReceiver && !privateMessagesOffSender)) {
                        // Get the message from the command and put it into 1 string.
                        String playerMessage = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

                        Pattern greenTextPattern = Pattern.compile("^>(\\S*).*");
                        Matcher greenTextMatcher = greenTextPattern.matcher(playerMessage);
                        if (greenTextMatcher.find()) {
                            playerMessage = ChatColor.GREEN + playerMessage;
                        }

                        // Add the player to the reply map and send them the message.
                        simpleMessage.reply.put(commandReceiver, commandSender);
                        commandSender.sendMessage(ChatColor.LIGHT_PURPLE + "[To " + commandSender.getName() + "] " + ChatColor.RESET + playerMessage.toString());
                        commandReceiver.sendMessage(ChatColor.LIGHT_PURPLE + "[From " + commandSender.getName() + "] " + ChatColor.RESET + playerMessage.toString());
                    }

                    // That player has PMs off.
                    if (privateMessagesOffReceiver) {
                        sender.sendMessage(ChatColor.RED + "Cannot send message. That player has private messages off.");
                        return true;
                    }

                    // You have PMs off.
                    if (privateMessagesOffSender) {
                        sender.sendMessage(ChatColor.RED + "Cannot send message. You have private messages off.");
                        return true;
                    }

                    // If you are ignoring the player.
                    if (blockingReceiver) {
                        sender.sendMessage(ChatColor.RED + "Cannot send message. You are ignoring this player.");
                        return true;
                    }
                    // If the player is ignoring you.
                    if (blockingSender) {
                        sender.sendMessage(ChatColor.RED + "Cannot send message. That player is ignoring you.");
                        return true;
                    }
                }
            }

        }
        // Check if using reply command,
        if (command.getName().equalsIgnoreCase("r") || command.getName().equalsIgnoreCase("reply")) {
            // Check if sender is a player.
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "You must be a player for this command.");
            } else if (args.length == 0) {
                // Check for valid syntax.
                sender.sendMessage(ChatColor.RED + "Invalid syntax. Do /r <message> instead.");
            } else {
                // Get the sender.
                Player commandSender = Bukkit.getPlayerExact(sender.getName());
                // Get the message from the command and create 1 string.

                String playerMessage = String.join(" ", args);
                // Check to see if someone has messaged you.
                if (simpleMessage.reply.get(commandSender) != null) {
                    // If they have, get who messaged you.
                    Player commandReceiver = simpleMessage.reply.get(commandSender);

                    // If player is offline.
                    if (Bukkit.getPlayerExact(commandReceiver.getName()) == null) {
                        sender.sendMessage(ChatColor.RED + "That player is offline.");
                    } else {
                        // Send the message if the player is online.
                        simpleMessage.reply.put(commandReceiver, commandSender);
                        commandSender.sendMessage(ChatColor.LIGHT_PURPLE + "[To " + commandReceiver.getName() + "] " + ChatColor.RESET + playerMessage.toString());
                        commandReceiver.sendMessage(ChatColor.LIGHT_PURPLE + "[From " + commandSender.getName() + "] " + ChatColor.RESET + playerMessage.toString());
                    }
                } else {
                    // If no one has sent you a message.
                    sender.sendMessage(ChatColor.RED + "No one has messaged you.");
                }
            }
        }
        return true;
    }
}
