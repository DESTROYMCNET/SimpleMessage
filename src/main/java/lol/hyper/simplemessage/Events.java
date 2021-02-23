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

package lol.hyper.simplemessage;

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

    public Events(SimpleMessage simpleMessage) {
        this.simpleMessage = simpleMessage;
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

            if (simpleMessage.ignoreListHandler.getPlayerIgnoreList(p.getUniqueId()) == null) {
                continue;
            }

            if (simpleMessage.ignoreListHandler.getPlayerIgnoreList(p.getUniqueId()).contains(ignoredPlayer.getUniqueId())) {
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
