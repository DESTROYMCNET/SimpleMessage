package lol.hyper.simplemessage.commands;

import lol.hyper.simplemessage.SimpleMessage;
import lol.hyper.simplemessage.tools.IgnoreLists;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public class CommandIgnore implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check if the sender is a player.
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.GOLD + "You must be a player for this command.");
            return true;
        } else {
            // Check for valid syntax.
            if (args.length == 0) {
                sender.sendMessage(ChatColor.RED + "Invalid syntax. Do /ignore <player> instead.");
            } else if (args.length > 1) {
                sender.sendMessage(ChatColor.RED + "Invalid syntax. Do /ignore <player> instead.");
            } else {
                // First, get the player's file and see if it exists.
                File playerList = new File(SimpleMessage.getInstance().ignoreLists.toFile(), Bukkit.getPlayerExact(sender.getName()).getUniqueId().toString() + ".json");
                // If the file doesn't exists, create it.
                FileWriter writer;
                if (!playerList.exists()) {
                    try {
                        // Create a new json file with an empty array.
                        boolean createFile = playerList.createNewFile();
                        if (!createFile) {
                            sender.sendMessage(ChatColor.RED + "There was an issue reading/writing your ignore file. Please contact the server owner!");
                        } else {
                            JSONObject ignoreObject = new JSONObject();
                            JSONArray empty = new JSONArray();
                            ignoreObject.put("ignored", empty);
                            writer = new FileWriter(playerList);
                            writer.write(ignoreObject.toJSONString());
                            writer.close();
                            Bukkit.getLogger().info("Creating new ignorelist file for " + sender.getName() + ".");
                        }
                    } catch (IOException e) {
                        sender.sendMessage(ChatColor.RED + "There was an issue reading/writing your ignore file. Please contact the server owner!");
                        e.printStackTrace();
                    }
                }
                // Get the player who is going to be ignored, make sure they are real.
                String ignored = args[0];
                if (Bukkit.getPlayerExact(ignored) == null || SimpleMessage.getInstance().isVanished(ignored)) {
                    sender.sendMessage(ChatColor.RED + "That player was not found.");
                } else if (sender.getName().equalsIgnoreCase(ignored)) {
                    // Don't let people ignore themselves.
                    sender.sendMessage(ChatColor.RED + "You cannot ignore yourself.");
                } else {
                    try {
                        // First, get their current json file and grab their current ignore list.
                        ArrayList<UUID> ignoreList = IgnoreLists.get(Bukkit.getPlayerExact(sender.getName()).getUniqueId());
                        UUID ignoredPlayer = Bukkit.getPlayerExact(ignored).getUniqueId();
                        // If the list contains the person they are using the command on, remove them from the list.
                        JSONParser parser;
                        FileReader reader;
                        if (ignoreList.contains(Bukkit.getPlayerExact(ignored).getUniqueId())) {
                            parser = new JSONParser();
                            reader = new FileReader(playerList);
                            JSONObject jsonObject = (JSONObject) parser.parse(reader);
                            reader.close();
                            JSONArray ignoredPlayers = (JSONArray) jsonObject.get("ignored");
                            ignoredPlayers.remove(ignoredPlayer.toString());
                            jsonObject.put("ignored", ignoredPlayers);
                            writer = new FileWriter(playerList);
                            writer.write(jsonObject.toJSONString());
                            writer.close();
                            sender.sendMessage(ChatColor.GOLD + "You are no longer ignoring " + ignored + ".");
                            Bukkit.getLogger().info("Updating ignorelist file for " + sender.getName() + ". Removing player " + ignoredPlayer.toString() + " from the list.");
                        } else {
                            // If they are not on the list, add them to the list.
                            parser = new JSONParser();
                            reader = new FileReader(playerList);
                            JSONObject jsonObject = (JSONObject) parser.parse(reader);
                            reader.close();
                            JSONArray ignoredPlayers = (JSONArray) jsonObject.get("ignored");
                            ignoredPlayers.add(ignoredPlayer.toString());
                            jsonObject.put("ignored", ignoredPlayers);
                            writer = new FileWriter(playerList);
                            writer.write(jsonObject.toJSONString());
                            writer.close();
                            sender.sendMessage(ChatColor.GOLD + ignored + " has been ignored.");
                            Bukkit.getLogger().info("Updating ignorelist file for " + sender.getName() + ". Adding player " + ignoredPlayer.toString() + " to the list.");
                        }
                    } catch (IOException | ParseException e) {
                        sender.sendMessage(ChatColor.RED + "There was an issue reading/writing your ignore file. Please contact the server owner!");
                        e.printStackTrace();
                    }
                }

            }
        }
        return true;
    }
}
