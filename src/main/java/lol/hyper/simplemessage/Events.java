package lol.hyper.simplemessage;

import lol.hyper.simplemessage.tools.IgnoreListHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Events implements Listener {

    private final SimpleMessage simpleMessage;
    private final IgnoreListHandler ignoreListHandler;

    public Events(SimpleMessage simpleMessage, IgnoreListHandler ignoreListHandler) {
        this.simpleMessage = simpleMessage;
        this.ignoreListHandler = ignoreListHandler;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        simpleMessage.reply.remove(event.getPlayer());
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (simpleMessage.generalChatOff.contains(event.getPlayer())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "Cannot send message. You have general chat off.");
        }

        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            Player ignoredPlayer = event.getPlayer();

            if (ignoreListHandler.getPlayerIgnoreList(p.getUniqueId()) == null) {
                return;
            }

            if (ignoreListHandler.getPlayerIgnoreList(p.getUniqueId()).contains(ignoredPlayer.getUniqueId())) {
                event.getRecipients().remove(p);
            } else if (simpleMessage.generalChatOff.contains(p)) {
                event.getRecipients().remove(p);
            }
        }

        String playerMessage = event.getMessage();
        Pattern greenTextPattern = Pattern.compile("^>(\\S*).*");
        Matcher greenTextMatcher = greenTextPattern.matcher(playerMessage);
        if (greenTextMatcher.find()) {
            playerMessage = ChatColor.GREEN + playerMessage;
            event.setMessage(playerMessage);
        }
    }
}
