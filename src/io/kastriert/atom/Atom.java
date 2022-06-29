package io.kastriert.atom;
/*

Created by extSayzz 



*/

import io.kastriert.atom.commands.AtomCommand;
import io.kastriert.atom.event.PlayerJoinListener;
import io.kastriert.atom.event.PlayerQuitListener;
import io.kastriert.atom.manager.NetworkManager;
import io.kastriert.atom.decoder.PacketDecoder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public final class Atom extends JavaPlugin {

    private static Atom instance;


    public static final String
        PREFIX = "§8» §f§lAtom§8-§f§lAntiCrash §8«";
    public Map<String, PacketDecoder> injections = new HashMap();

    public static Atom getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        System.setProperty("-Dog4j2.formatMsgNoLookup", "true");
        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerQuitListener(), this);
        Bukkit.getPluginManager().registerEvents(new NetworkManager(), this);

        Bukkit.getConsoleSender().sendMessage("§7This Server is running " + PREFIX + " §7Version 1§8.§70");

        this.getCommand("atom").setExecutor(new AtomCommand());


    }

    @Override
    public void onDisable() {

        for (Player all : Bukkit.getOnlinePlayers()) {
            if (injections.containsKey(all)) {
                injections.get(all).unInject();
            }
        }
    }
}
