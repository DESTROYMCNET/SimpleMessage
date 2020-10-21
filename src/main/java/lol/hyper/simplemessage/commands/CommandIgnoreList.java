package lol.hyper.simplemessage.commands;

import lol.hyper.simplemessage.SimpleMessage;
import lol.hyper.simplemessage.tools.IgnoreLists;
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
    private final IgnoreLists ignoreLists;

    public CommandIgnoreList(SimpleMessage simpleMessage, IgnoreLists ignoreLists) {
        this.simpleMessage = simpleMessage;
        this.ignoreLists = ignoreLists;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check if sender is player.
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.GOLD + "You must be a player for this command.");
        } else {
            sender.sendMessage(ChatColor.GOLD + "--------------------------------------------");
            sender.sendMessage(ChatColor.DARK_AQUA + "Fetching ignore list...");
            // Get the ignore list into an array.
            try {
                ArrayList<UUID> fileContents = ignoreLists.get(Bukkit.getPlayerExact(sender.getName()).getUniqueId());
                ArrayList<String> playerNames = new ArrayList<>();

                // If the list is not empty, print the list to the player.
                if (fileContents.size() != 0) {
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
            } catch (IOException | ParseException e) {
                sender.sendMessage(ChatColor.RED + "There was an issue getting your ignore list. Please contact hyperdefined.");
                sender.sendMessage(ChatColor.GOLD + "--------------------------------------------");
                e.printStackTrace();
            }
        }
        return true;
    }
}
