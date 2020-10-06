package lol.hyper.simplemessage;

import lol.hyper.simplemessage.tools.IgnoreLists;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.json.simple.parser.ParseException;

import java.io.IOException;

public class Events implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerQuitEvent event) {
        SimpleMessage.getInstance().reply.remove(event.getPlayer().getName());
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
