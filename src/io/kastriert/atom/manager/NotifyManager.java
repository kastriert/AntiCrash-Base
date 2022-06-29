package io.kastriert.atom.manager;
/*

Created by extSayzz 



*/

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class NotifyManager {

    private final Player player;

    private NotifyManager(final Player player) {
        this.player = player;
    }

    public void notify(final String message) {
        Bukkit.getOnlinePlayers().forEach(all -> {
            if (all.hasPermission("*") || all.isOp() || all.hasPermission("atom.notify")) {
                all.sendMessage(message);
            }
        });
    }
}
