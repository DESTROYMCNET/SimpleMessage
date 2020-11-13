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

    public final ArrayList<Player> privateMessagesOff = new ArrayList<>();
    public final ArrayList<Player> generalChatOff = new ArrayList<>();
    // First player RECEIVES the message, the second player SENDS it.
    public final HashMap<Player, Player> reply = new HashMap<>();
    public final Path ignoreLists = Paths.get(this.getDataFolder() + File.separator + "ignorelists");
    public Logger logger = this.getLogger();

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
        events = new Events(this, ignoreListHandler);
        commandIgnore = new CommandIgnore(this, ignoreListHandler);
        commandIgnoreList = new CommandIgnoreList(this, ignoreListHandler);
        commandMessage = new CommandMessage(this, ignoreListHandler);
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

    @Override
    public void onDisable() {
    }

    public boolean isVanished(String player) {
        if (Bukkit.getPlayerExact(player) == null) {
            return false;
        } else {
            Player player2 = Bukkit.getPlayerExact(player);
            assert player2 != null;
            for (MetadataValue meta : player2.getMetadata("vanished")) {
                if (meta.asBoolean()) return true;
            }
        }
        return false;
    }
}
