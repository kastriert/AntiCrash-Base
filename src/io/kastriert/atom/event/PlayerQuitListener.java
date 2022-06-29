package io.kastriert.atom.event;
/*

Created by extSayzz 



*/

import io.kastriert.atom.Atom;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {

    @EventHandler
    public void onQuit(final PlayerQuitEvent playerQuitEvent) {

        final Player player = playerQuitEvent.getPlayer();
        if (Atom.getInstance().injections.containsKey(playerQuitEvent.getPlayer().getName())) {
            Atom.getInstance().injections.get(playerQuitEvent.getPlayer().getName()).unInject();
        }
    }
}
