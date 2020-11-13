package lol.hyper.simplemessage.tools;

import lol.hyper.simplemessage.SimpleMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.UUID;

public class IgnoreListHandler {

    private final SimpleMessage simpleMessage;

    public IgnoreListHandler(SimpleMessage simpleMessage) {
        this.simpleMessage = simpleMessage;
    }

    JSONParser parser;
    FileWriter writer;
    FileReader reader;

    public ArrayList<UUID> getPlayerIgnoreList(UUID player) {
        ArrayList<UUID> list = new ArrayList<>();

        File ignoredList = new File(simpleMessage.ignoreLists.toFile(), player.toString() + ".json");

        if (!ignoredList.exists()) {
            return null;
        } else {
            try {
                parser = new JSONParser();
                reader = new FileReader(ignoredList);
                JSONObject jsonObject = (JSONObject) parser.parse(reader);
                reader.close();
                JSONArray partyMembers = (JSONArray) jsonObject.get("ignored");
                for (String partyMember : (Iterable<String>) partyMembers) {
                    list.add(UUID.fromString(partyMember));
                }
            } catch (ParseException | IOException e) {
                e.printStackTrace();
                return null;
            }
            return list;
        }
    }

    /**
     * Removes/adds user to ignore list.
     * @param uuid Owner's ignore list.
     * @param uuid2 Player to remove/add.
     */
    public void updateList(UUID uuid, UUID uuid2) {
        File ignoredList = new File(simpleMessage.ignoreLists.toFile(), uuid.toString() + ".json");
        Player player = Bukkit.getPlayer(uuid);
        OfflinePlayer player2 = Bukkit.getOfflinePlayer(uuid2);
        JSONObject jsonObject;
        if (ignoredList.exists()) {
            try {
                parser = new JSONParser();
                reader = new FileReader(ignoredList);
                jsonObject = (JSONObject) parser.parse(reader);
                reader.close();
            } catch (ParseException | IOException e) {
                e.printStackTrace();
                player.sendMessage(ChatColor.RED + "There was an issue reading/writing your ignore file. Please contact the server owner!");
                return;
            }
        } else {
            jsonObject = new JSONObject();
            JSONArray empty = new JSONArray();
            jsonObject.put("ignored", empty);
        }

        JSONArray ignoredPlayers = (JSONArray) jsonObject.get("ignored");
        if (ignoredPlayers.contains(uuid2.toString())) {
            ignoredPlayers.remove(uuid2.toString());
            player.sendMessage(ChatColor.GOLD + "You are no longer ignoring " + player2.getName() + ".");
            simpleMessage.logger.info("Updating ignorelist file for " + player.getName() + ". Removing player " + player2.toString() + " from the list.");
        } else {
            ignoredPlayers.add(uuid2.toString());
            player.sendMessage(ChatColor.GOLD + player2.getName() + " has been ignored.");
            simpleMessage.logger.info("Updating ignorelist file for " + player.getName() + ". Adding player " + player2.toString() + " to the list.");
        }
        jsonObject.put("ignored", ignoredPlayers);
        if (ignoredPlayers.size() == 0) {
            try {
                Files.delete(ignoredList.toPath());
                simpleMessage.logger.info("Deleting empty ignore file " + ignoredList);
                return;
            } catch (IOException e) {
                simpleMessage.logger.severe("Unable to delete file " + ignoredList.getAbsolutePath());
                e.printStackTrace();
                return;
            }
        }
        try {
            writer = new FileWriter(ignoredList);
            writer.write(jsonObject.toJSONString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
            player.sendMessage(ChatColor.RED + "There was an issue reading/writing your ignore file. Please contact the server owner!");
        }
    }
}
