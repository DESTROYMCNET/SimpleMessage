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

package lol.hyper.simplemessage.commands;

import lol.hyper.simplemessage.SimpleMessage;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class CommandTogglePM implements CommandExecutor {

    private final SimpleMessage simpleMessage;

    public CommandTogglePM(SimpleMessage simpleMessage) {
        this.simpleMessage = simpleMessage;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof ConsoleCommandSender) {
            sender.sendMessage(ChatColor.RED + "You must be a player for this command.");
            return true;
        }
        Player player = (Player) sender;
        if (simpleMessage.privateMessagesOff.contains(player)) {
            simpleMessage.privateMessagesOff.remove(player);
            sender.sendMessage(ChatColor.GOLD + "Private messages are now on.");
        } else if (!simpleMessage.privateMessagesOff.contains(player)) {
            simpleMessage.privateMessagesOff.add(player);
            sender.sendMessage(ChatColor.GOLD + "Private messages are now off.");
        }
        return true;
    }
}
