package lol.hyper.simplemessage;

import lol.hyper.simplemessage.commands.*;
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

public final class SimpleMessage extends JavaPlugin {

    private static SimpleMessage instance;

    public final ArrayList<Player> privateMessagesOff = new ArrayList<>();
    public final ArrayList<Player> generalChatOff = new ArrayList<>();
    public final HashMap<Player, Player> reply = new HashMap<>();
    public final Path ignoreLists = Paths.get(this.getDataFolder() + File.separator + "ignorelists");

    public static SimpleMessage getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        if (!Files.exists(ignoreLists)) {
            try {
                Files.createDirectories(ignoreLists);
            } catch (IOException e) {
                Bukkit.getLogger().severe("Unable to create folder " + ignoreLists.toString() + "! Please make the folder manually or check folder permissions!");
                e.printStackTrace();
            }
        }
        this.getCommand("msg").setExecutor(new CommandMessage());
        this.getCommand("r").setExecutor(new CommandMessage());
        this.getCommand("ignore").setExecutor(new CommandIgnore());
        this.getCommand("ignorelist").setExecutor(new CommandIgnoreList());
        this.getCommand("togglepm").setExecutor(new CommandTogglePM());
        this.getCommand("togglechat").setExecutor(new CommandToggleChat());
        Bukkit.getServer().getPluginManager().registerEvents(new Events(), this);
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
