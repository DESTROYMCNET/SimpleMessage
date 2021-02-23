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
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandIgnore implements CommandExecutor {

    private final SimpleMessage simpleMessage;

    public CommandIgnore(SimpleMessage simpleMessage) {
        this.simpleMessage = simpleMessage;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check if the sender is a player.
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You must be a player for this command.");
            return true;
        } else {
            // Check for valid syntax.
            if (args.length == 0) {
                sender.sendMessage(ChatColor.RED + "Invalid syntax. Do /ignore <player> instead.");
            } else if (args.length > 1) {
                sender.sendMessage(ChatColor.RED + "Invalid syntax. Do /ignore <player> instead.");
            } else {
                // Get the player who is going to be ignored, make sure they are real.
                String ignored = args[0];
                if (Bukkit.getPlayerExact(ignored) == null || simpleMessage.isVanished(ignored)) {
                    sender.sendMessage(ChatColor.RED + "That player was not found.");
                } else if (sender.getName().equalsIgnoreCase(ignored)) {
                    // Don't let people ignore themselves.
                    sender.sendMessage(ChatColor.RED + "You cannot ignore yourself.");
                } else {
                    simpleMessage.ignoreListHandler.updateList(((Player) sender).getUniqueId(), Bukkit.getPlayerExact(ignored).getUniqueId());
                }
            }
        }
        return true;
    }
}
