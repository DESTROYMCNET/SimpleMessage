/*
 * This file is part of SimpleMessage.
 *
 * SimpleMessage is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SimpleMessage is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SimpleMessage.  If not, see <https://www.gnu.org/licenses/>.
 */

package lol.hyper.simplemessage.tools;

import lol.hyper.simplemessage.SimpleMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.UUID;

public class IgnoreListHandler {

    private final SimpleMessage simpleMessage;
    FileWriter writer;

    public IgnoreListHandler(SimpleMessage simpleMessage) {
        this.simpleMessage = simpleMessage;
    }

    /**
     * Write data to JSON file.
     * @param file File to write data to.
     * @param jsonToWrite Data to write to file. This much be a JSON string.
     */
    private void writeFile(File file, String jsonToWrite) {
        try {
            writer = new FileWriter(file);
            writer.write(jsonToWrite);
            writer.close();
        } catch (IOException e) {
            simpleMessage.logger.severe("Unable to write file " + file.getAbsolutePath());
            simpleMessage.logger.severe("This is bad, really bad.");
            e.printStackTrace();
        }
    }

    private JSONObject readIgnoreFile(UUID player) {
        File ignoredList = new File(simpleMessage.ignoreLists.toFile(), player.toString() + ".json");
        JSONObject object;
        try {
            BufferedReader br = new BufferedReader(new FileReader(ignoredList));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            while (line != null) {
                sb.append(line);
                line = br.readLine();
            }
            object = new JSONObject(sb.toString());
            br.close();
        } catch (Exception e) {
            simpleMessage.logger.severe("Unable to read file " + ignoredList.getAbsolutePath());
            simpleMessage.logger.severe("This is bad, really bad.");
            e.printStackTrace();
            object = null;
        }
        return object;
    }

    public ArrayList<UUID> getPlayerIgnoreList(UUID player) {
        ArrayList<UUID> list = new ArrayList<>();

        File ignoredList = new File(simpleMessage.ignoreLists.toFile(), player.toString() + ".json");

        if (!ignoredList.exists()) {
            return null;
        } else {
            JSONObject object = readIgnoreFile(player);
            if (object != null) {
                JSONArray ignoredPlayers = object.getJSONArray("ignored");
                for (int i = 0; i < ignoredPlayers.length(); i++) {
                    list.add(UUID.fromString(ignoredPlayers.getString(i)));
                }
            }
            return list;
        }
    }

    /**
     * Removes/adds user to ignore list.
     * @param listOwner Owner's ignore list.
     * @param player Player to remove/add.
     */
    public void updateList(UUID listOwner, UUID player) {
        File ignoredList = new File(simpleMessage.ignoreLists.toFile(), listOwner.toString() + ".json");
        Player listOwnerPlayer = Bukkit.getPlayer(listOwner);
        Player player2 = Bukkit.getPlayer(player);
        JSONObject jsonObject;
        if (ignoredList.exists()) {
            jsonObject = readIgnoreFile(listOwner);
        } else {
            jsonObject = new JSONObject();
            JSONArray empty = new JSONArray();
            jsonObject.put("ignored", empty);
        }

        JSONArray ignoredPlayers = jsonObject.getJSONArray("ignored");
        boolean removedPlayer = false;
        for (int i = 0; i < ignoredPlayers.length(); i++) {
            String current = ignoredPlayers.getString(i);
            if (current.equals(player.toString())) {
                removedPlayer = true;
                ignoredPlayers.remove(i);
                listOwnerPlayer.sendMessage(ChatColor.GOLD + "You are no longer ignoring " + player2.getName() + ".");
                simpleMessage.logger.info("Updating ignorelist file for " + listOwnerPlayer.getName() + ". Removing player "
                        + player2.getName() + " from the list.");
            }
        }
        if (!removedPlayer) {
            ignoredPlayers.put(player.toString());
            listOwnerPlayer.sendMessage(ChatColor.GOLD + player2.getName() + " has been ignored.");
            simpleMessage.logger.info("Updating ignorelist file for " + listOwnerPlayer.getName() + ". Adding player "
                    + player2.getName() + " to the list.");
        }
        jsonObject.put("ignored", ignoredPlayers);
        if (ignoredPlayers.length() == 0) {
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
        writeFile(ignoredList, jsonObject.toString());
    }
}
