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

import lol.hyper.simplemessage.commands.*;
import lol.hyper.simplemessage.tools.IgnoreListHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

public final class SimpleMessage extends JavaPlugin {

    public final ArrayList < Player > privateMessagesOff = new ArrayList < > ();
    public final ArrayList < Player > generalChatOff = new ArrayList < > ();
    // First player RECEIVES the message, the second player SENDS it.
    public final HashMap < Player, Player > reply = new HashMap < > ();
    public final Path ignoreLists = Paths.get(this.getDataFolder() + File.separator + "ignorelists");
    public final Logger logger = this.getLogger();

    public Events events;
    public CommandIgnore commandIgnore;
    public CommandIgnoreList commandIgnoreList;
    public CommandMessage commandMessage;
    public CommandToggleChat commandToggleChat;
    public CommandTogglePM commandTogglePM;
    public IgnoreListHandler ignoreListHandler;

    @Override
    public void onEnable() {
        ignoreListHandler = new IgnoreListHandler(this);
        events = new Events(this);
        commandIgnore = new CommandIgnore(this);
        commandIgnoreList = new CommandIgnoreList(this);
        commandMessage = new CommandMessage(this);
        commandToggleChat = new CommandToggleChat(this);
        commandTogglePM = new CommandTogglePM(this);
        if (!Files.exists(ignoreLists)) {
            try {
                Files.createDirectories(ignoreLists);
            } catch (IOException e) {
                logger.severe("Unable to create folder " + ignoreLists.toString() + "! Please make the folder manually or check folder permissions!");
                e.printStackTrace();
            }
        }
        this.getCommand("msg").setExecutor(commandMessage);
        this.getCommand("r").setExecutor(commandMessage);
        this.getCommand("ignore").setExecutor(commandIgnore);
        this.getCommand("ignorelist").setExecutor(commandIgnoreList);
        this.getCommand("togglepm").setExecutor(commandTogglePM);
        this.getCommand("togglechat").setExecutor(commandToggleChat);
        Bukkit.getServer().getPluginManager().registerEvents(events, this);
    }

    public boolean isVanished(String player) {
        if (Bukkit.getPlayerExact(player) == null) {
            return false;
        } else {
            Player player2 = Bukkit.getPlayerExact(player);
            assert player2 != null;
            for (MetadataValue meta: player2.getMetadata("vanished")) {
                if (meta.asBoolean()) return true;
            }
        }
        return false;
    }
}