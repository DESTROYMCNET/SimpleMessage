package lol.hyper.simplemessage.commands;

import lol.hyper.simplemessage.SimpleMessage;
import lol.hyper.simplemessage.tools.IgnoreListHandler;
import lol.hyper.simplemessage.tools.UUIDToName;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public class CommandIgnoreList implements CommandExecutor {

    private final SimpleMessage simpleMessage;
    private final IgnoreListHandler ignoreListHandler;

    public CommandIgnoreList(SimpleMessage simpleMessage, IgnoreListHandler ignoreListHandler) {
        this.simpleMessage = simpleMessage;
        this.ignoreListHandler = ignoreListHandler;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check if sender is player.
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You must be a player for this command.");
        } else {
            sender.sendMessage(ChatColor.GOLD + "--------------------------------------------");
            sender.sendMessage(ChatColor.DARK_AQUA + "Fetching ignore list...");
            // Get the ignore list into an array.
            ArrayList<UUID> fileContents = ignoreListHandler.getPlayerIgnoreList(Bukkit.getPlayerExact(sender.getName()).getUniqueId());
            ArrayList<String> playerNames = new ArrayList<>();

            // If the list is not empty, print the list to the player.
            if (fileContents != null) {
                Bukkit.getScheduler().runTaskAsynchronously(simpleMessage, () -> {
                    // We convert the UUIDs to player names.
                    for (UUID uuid : fileContents) {
                        String name = UUIDToName.getName(uuid);
                        playerNames.add(name);
                    }
                    // Output the new array with the converted player names.
                    for (String playerName : playerNames) {
                        sender.sendMessage(ChatColor.DARK_AQUA + playerName);
                    }
                    sender.sendMessage(ChatColor.GOLD + "--------------------------------------------");
                });
            } else {
                // If the list is empty, simply output no one.
                sender.sendMessage(ChatColor.DARK_AQUA + "No one.");
                sender.sendMessage(ChatColor.GOLD + "--------------------------------------------");
            }
        }
        return true;
    }
}
