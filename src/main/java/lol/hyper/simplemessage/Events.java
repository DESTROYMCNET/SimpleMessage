package lol.hyper.simplemessage;

import lol.hyper.simplemessage.tools.IgnoreLists;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Events implements Listener {
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        SimpleMessage.getInstance().reply.remove(event.getPlayer());
    }
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        File playerList = new File(SimpleMessage.getInstance().ignoreLists.toFile(), player.getUniqueId() + ".json");
        FileWriter writer;
        if (!playerList.exists()) {
            try {
                boolean createFile = playerList.createNewFile();
                if (!createFile) {
                    player.sendMessage(ChatColor.RED + "There was an issue reading/writing your ignore file. Please contact the server owner!");
                } else {
                    JSONObject ignoreObject = new JSONObject();
                    JSONArray empty = new JSONArray();
                    ignoreObject.put("ignored", empty);
                    writer = new FileWriter(playerList);
                    writer.write(ignoreObject.toJSONString());
                    writer.close();
                    Bukkit.getLogger().info("Creating new ignorelist file for " + player.getName() + ".");
                }
            } catch (IOException e) {
                player.sendMessage(ChatColor.RED + "There was an issue reading/writing your ignore file. Please contact the server owner!");
                e.printStackTrace();
            }
        }
    }
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) throws IOException, ParseException {
        if (SimpleMessage.getInstance().generalChatOff.contains(event.getPlayer())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "Cannot send message. You have general chat off.");
        }
        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            Player ignoredPlayer = event.getPlayer();

            if (IgnoreLists.get(p.getUniqueId()).contains(ignoredPlayer.getUniqueId())) {
                event.getRecipients().remove(p);
            } else if (SimpleMessage.getInstance().generalChatOff.contains(p)) {
                event.getRecipients().remove(p);
            }
        }
    }
}
