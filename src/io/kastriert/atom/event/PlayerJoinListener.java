package io.kastriert.atom.event;
/*

Created by extSayzz 



*/

import io.kastriert.atom.Atom;
import io.kastriert.atom.decoder.PacketDecoder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent playerJoinEvent) {

            Player player = playerJoinEvent.getPlayer();
            PacketDecoder packetDecoder = new PacketDecoder(playerJoinEvent.getPlayer());
            packetDecoder.inject();
            Atom.getInstance().injections.put(playerJoinEvent.getPlayer().getName(), packetDecoder);
    }
}
