package io.kastriert.atom.decoder;
/*

Created by extSayzz 



*/

import io.kastriert.atom.Atom;
import io.kastriert.atom.manager.NotifyManager;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.MessageToMessageDecoder;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;
import java.util.List;

public class PacketDecoder {

    private final Player player;
    private final String name;
    private Channel channel;

    private long lastTabComplete;

    public PacketDecoder(final Player player) {
        this.player = player;
        this.name = player.getName();

    }

    public void inject() {
        final CraftPlayer craftPlayer = (CraftPlayer) this.player;
        channel = craftPlayer.getHandle().playerConnection.networkManager.channel;
        channel.pipeline().addAfter("atom_decoder", this.name, new MessageToMessageDecoder() {
            @Override
            protected void decode(ChannelHandlerContext channelHandlerContext, Object o, List list) throws Exception {
                Packet<?> packet = (Packet<?>) o;
                if (!readPacket(packet)) {
                    list.add(packet);
                }
            }
        });
    }

    public void unInject() {
        final ChannelPipeline pipeline = this.channel.pipeline();
        if (pipeline.get(this.name) != null) {
            pipeline.remove(this.name);
            if (Atom.getInstance().injections.containsKey(this.name)) {
                Atom.getInstance().injections.remove(this.name);
            }
        }
    }

    private boolean readPacket(final Packet<?> packet) {
        try {
            if (packet instanceof PacketPlayInCustomPayload) {
                final PacketPlayInCustomPayload packetPlayInCustomPayload = (PacketPlayInCustomPayload) packet;
                String s = packetPlayInCustomPayload.a();
                if (s.equals("MC|BEdit") || s.equals("MC|BSign")) {
                    this.kickPlayer(this.player, this.channel);
                    return true;
                } else if (packet == null) {
                    this.kickPlayer(player, channel);
                }
            } else if (packet instanceof PacketPlayInCustomPayload) {
                final PacketPlayInCustomPayload packetPlayInCustomPayload = (net.minecraft.server.v1_8_R3.PacketPlayInCustomPayload) packet;
                String object = packetPlayInCustomPayload.a();
                if (packetPlayInCustomPayload.b().writerIndex() > Short.MAX_VALUE) return false;
                if (packetPlayInCustomPayload.a().equals("MC|BEdit") || packetPlayInCustomPayload.a().equals("MC|BSign")) {
                    if (packetPlayInCustomPayload.a().getBytes().length != 8 && packetPlayInCustomPayload.a().getBytes().length != 11 && packetPlayInCustomPayload.a().getBytes().length != 3 && packetPlayInCustomPayload.a().getBytes().length != 10 && packetPlayInCustomPayload.a().getBytes().length != 5 && packetPlayInCustomPayload.a().getBytes().length != 16 && this.player.getOpenInventory().getType() != InventoryType.ANVIL && this.player.getOpenInventory().getType() != InventoryType.BEACON && !object.equalsIgnoreCase("MC|AdvCdm")) {

                        try {
                            final Field packetBufferField = PacketDataSerializer.class.getDeclaredField("a");
                            packetBufferField.setAccessible(true);
                            final ByteBuf byteBuf = (ByteBuf) packetBufferField.get(packetPlayInCustomPayload.b());
                            if (byteBuf.capacity() > 8000) {
                                this.kickPlayer(this.player, this.channel);
                                return true;
                            } else if (packet == null) {
                                this.kickPlayer(player, channel);
                            }
                        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException |
                                 IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
                return true;
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

        private void kickPlayer(final Player player, final Channel channel) {
        new BukkitRunnable() {
            @Override
            public void run() {
                PacketPlayOutKickDisconnect packetPlayOutKickDisconnect = new PacketPlayOutKickDisconnect(new IChatBaseComponent.ChatSerializer().a("{text:\"" + "§fDisconnected" + "\"}")) {
                };
                ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packetPlayOutKickDisconnect);
                channel.close();
            }
        }.run();
    }
}
