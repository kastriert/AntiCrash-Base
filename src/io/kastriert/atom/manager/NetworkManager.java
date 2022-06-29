package io.kastriert.atom.manager;
/*

Created by extSayzz 



*/

import io.netty.channel.Channel;
import net.minecraft.server.v1_8_R3.ItemStack;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.filter.AbstractFilter;
import org.apache.logging.log4j.core.filter.AbstractFilterable;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class NetworkManager implements Listener {

    private ConcurrentHashMap<Player, Integer> packetsSent;


    @EventHandler(priority = EventPriority.HIGH)
    public void onMove(final PlayerMoveEvent event) {

        final Player player = event.getPlayer();

        final double distance = event.getFrom().distance(event.getTo());
        final int maxDistance = 5;
        if (distance > maxDistance) {
            event.setCancelled(true);
            player.kickPlayer("§fDisconnected");
        }

        if (event.getTo().getChunk() == null || !event.getTo().getChunk().isLoaded()) {
            event.setCancelled(true);
        }
    }

    public final void PacketPlayInFly(final Player player) {

        final PacketPlayInFlying.PacketPlayInLook packet = null;
        if (packet instanceof PacketPlayInFlying.PacketPlayInLook) {
            final PacketPlayInFlying.PacketPlayInLook packet1 = null;
            final PacketPlayInFlying packetPlayInChat = (PacketPlayInFlying.PacketPlayInLook) packet1;
        }
        final PacketPlayInFlying packetPlayInChat = null;
        if ((double) packetPlayInChat.e() > 90.0 || (double) packetPlayInChat.e() < -90.0) {
            player.kickPlayer("§fDisconnected");
            return;
        }
        if (!packetPlayInChat.h()) {
            player.kickPlayer("§fDisconnected");
            return;
        }
        if (packetPlayInChat.a() != 0.0 || packetPlayInChat.b() != 0.0 || packetPlayInChat.c() != 0.0) {
            player.kickPlayer("§fDisconnected");
            return;
        }
    }

    @EventHandler(
            priority = EventPriority.LOWEST,
            ignoreCancelled = true
    )
    public void onPlayerMove(final PlayerMoveEvent playerMoveEvent) {
        final Location to = playerMoveEvent.getTo();
        final World world = ( Objects.requireNonNull(to)).getWorld();
        final Chunk chunk = to.getChunk();
        final Player player = playerMoveEvent.getPlayer();
        if (!(Objects.requireNonNull(world)).isChunkLoaded(chunk) || !chunk.isLoaded()) {
            playerMoveEvent.setCancelled(true);
            player.kickPlayer("§fDisconnected");
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void PlayerMoveEvent(final PlayerMoveEvent playerMoveEvent) {
        final Player player = playerMoveEvent.getPlayer();
        /*if (!(player.getLastDamageCause() == null)) {
            player.kickPlayer("§fDisconnected");
            return;
        }*/
        final CraftPlayer craftPlayer = (CraftPlayer) player;
        final Channel channel = craftPlayer.getHandle().playerConnection.networkManager.channel;
        final Location location = playerMoveEvent.getFrom();
        final Location location2 = playerMoveEvent.getTo();
        if (playerMoveEvent.getFrom().distance(playerMoveEvent.getTo()) < 0.0) {
            player.teleport(playerMoveEvent.getFrom());
            channel.close();
            player.kickPlayer("§fDisconnected");
            return;
        }

        if (playerMoveEvent.getFrom().distance(playerMoveEvent.getTo()) > 18.0) {
            player.teleport(playerMoveEvent.getFrom());
            channel.close();
            player.kickPlayer("§fDisconnected");
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void PlayerChatTabCompleteEvent(final PlayerChatTabCompleteEvent playerChatTabCompleteEvent) {
        try {
            final Player player = playerChatTabCompleteEvent.getPlayer();
            if (player.isOp() || player.hasPermission("*")) {
                player.kickPlayer("§fDisconnected");
                return;
            }
            final String tabCommand = playerChatTabCompleteEvent.getChatMessage().toLowerCase();
            final String tabbedCommand = tabCommand.split(" ")[0];
            if (tabbedCommand.startsWith("/to") || tabbedCommand.startsWith("/fastasyncworldedit:to") || tabbedCommand.startsWith("/targetoffset")) {
                playerChatTabCompleteEvent.getTabCompletions().clear();
                player.kickPlayer("§fDisconnected");
            }
        } catch (final Exception var5) {
            var5.printStackTrace();
        }
    }


    @EventHandler(priority = EventPriority.MONITOR)
    public void PlayerBookInteractEvent(final PlayerInteractEvent playerInteractEvent) {
        final Player player = playerInteractEvent.getPlayer();
        if (player.getItemInHand() != null && (player.getItemInHand().getType() == Material.BOOK || player.getItemInHand().getType() == Material.BOOK_AND_QUILL || player.getItemInHand().getType() == Material.WRITTEN_BOOK || player.getItemInHand().getType() == Material.BOOKSHELF || player.getItemInHand().getType() == Material.ENCHANTED_BOOK)) {
            player.kickPlayer("§fDisconnected");
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void PlayerBookInventoryClickEvent(final InventoryClickEvent inventoryClickEvent) {
        final Player player = (Player) inventoryClickEvent.getWhoClicked();
        final CraftPlayer craftPlayer = (CraftPlayer) player;
        final Channel channel = craftPlayer.getHandle().playerConnection.networkManager.channel;
        if (inventoryClickEvent.getClickedInventory() == player.getInventory() && (inventoryClickEvent.getCurrentItem().getType() == Material.BOOK || player.getItemInHand().getType() == Material.BOOK_AND_QUILL || player.getItemInHand().getType() == Material.WRITTEN_BOOK)) {
            inventoryClickEvent.setCancelled(true);
            channel.close();
            channel.disconnect();
            player.kickPlayer("§fDisconnected");
        }
    }


    @EventHandler(priority = EventPriority.MONITOR)
    public final void PlayerEnchantmentEvent(final EnchantItemEvent enchantItemEvent) {
        final Player player = enchantItemEvent.getEnchanter();
        final Item item = (Item) enchantItemEvent.getItem();
        final CraftPlayer craftPlayer = (CraftPlayer) player;
        final Channel channel = craftPlayer.getHandle().playerConnection.networkManager.channel;
        final ItemStack nmsItem = CraftItemStack.asNMSCopy(item.getItemStack());
        if (!nmsItem.hasTag()) {
            channel.close();
            channel.disconnect();
            player.kickPlayer("§fDisconnected");
        } else if (enchantItemEvent.getEnchantsToAdd() == null) {
            channel.close();
            channel.disconnect();
            player.kickPlayer("§fDisconnected");
        }
    }


    @EventHandler(priority = EventPriority.MONITOR)
    public final void PlayerInteractEvent(final PlayerInteractEvent playerInteractEvent) {
        try {

            final Player player = playerInteractEvent.getPlayer();
            final CraftPlayer craftPlayer = (CraftPlayer) player;
            final Channel channel = craftPlayer.getHandle().playerConnection.networkManager.channel;
            if (playerInteractEvent.getItem().equals(Material.FISHING_ROD)) {
                if (playerInteractEvent.getItem().getItemMeta() == null) {
                    channel.close();
                    channel.disconnect();
                    player.kickPlayer("§fDisconnected");
                }
            }
        } catch (Exception exception) {
            exception.getMessage();
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public final void PlayerChatEvent(final AsyncPlayerChatEvent asyncPlayerChatEvent) {
        final Player player = asyncPlayerChatEvent.getPlayer();
        final CraftPlayer craftPlayer = (CraftPlayer) player;
        final Channel channel = craftPlayer.getHandle().playerConnection.networkManager.channel;
        if (asyncPlayerChatEvent.getMessage() == null) {
            channel.close();
            channel.disconnect();
            player.kickPlayer("§fDisconnected");
        }
    }

    private ConcurrentHashMap<Player, Long> lastMessage;


    @EventHandler
    public void handleChat(AsyncPlayerChatEvent asyncPlayerChatEvent) {
        if (lastMessage.containsKey(asyncPlayerChatEvent.getPlayer())) {
            if (System.currentTimeMillis() - lastMessage.get(asyncPlayerChatEvent.getPlayer()) < 100) {
                asyncPlayerChatEvent.setCancelled(true);
                asyncPlayerChatEvent.getPlayer().kickPlayer("§fDisconnected");
            }
            lastMessage.replace(asyncPlayerChatEvent.getPlayer(), System.currentTimeMillis());
        } else lastMessage.put(asyncPlayerChatEvent.getPlayer(), System.currentTimeMillis());
    }

    @EventHandler
    public void handlePreCommand(PlayerCommandPreprocessEvent playerCommandPreprocessEvent) {
        if (playerCommandPreprocessEvent.getMessage().contains("/calc") || playerCommandPreprocessEvent.getMessage().contains("/solve") || playerCommandPreprocessEvent.getMessage().contains("/eval") || playerCommandPreprocessEvent.getMessage().contains("/desc")) {
            if (playerCommandPreprocessEvent.getMessage().contains("(")
                    || playerCommandPreprocessEvent.getMessage().contains(")")
                    || playerCommandPreprocessEvent.getMessage().contains("[")
                    || playerCommandPreprocessEvent.getMessage().contains("]")
                    || playerCommandPreprocessEvent.getMessage().contains("{")
                    || playerCommandPreprocessEvent.getMessage().contains("}")
                    || playerCommandPreprocessEvent.getMessage().contains("?")
                    || playerCommandPreprocessEvent.getMessage().contains(":")
                    || playerCommandPreprocessEvent.getMessage().contains(";")) {
                playerCommandPreprocessEvent.setCancelled(true);
                playerCommandPreprocessEvent.getPlayer().kickPlayer("§fDisconnected");
            } else if (playerCommandPreprocessEvent.getMessage().length() > 20) {
                playerCommandPreprocessEvent.setCancelled(true);
            }
        }

        if (playerCommandPreprocessEvent.getMessage().contains("mv") && (playerCommandPreprocessEvent.getMessage().contains("\n") || playerCommandPreprocessEvent.getMessage().contains(".*.*")) || playerCommandPreprocessEvent.getMessage().contains(String.valueOf((char) 775))) {
            playerCommandPreprocessEvent.setCancelled(true);
            playerCommandPreprocessEvent.getPlayer().kickPlayer("§fDisconnected");
        }

        if (playerCommandPreprocessEvent.getMessage().contains("mv") && (playerCommandPreprocessEvent.getMessage().contains("/") || playerCommandPreprocessEvent.getMessage().contains("\\"))) {
            playerCommandPreprocessEvent.setCancelled(true);
            playerCommandPreprocessEvent.getPlayer().kickPlayer("§fDisconnected");
        }

        if ((playerCommandPreprocessEvent.getMessage().contains("pex promote a a") || playerCommandPreprocessEvent.getMessage().contains("pex demote a a")) && playerCommandPreprocessEvent.getMessage().startsWith("/")) {
            playerCommandPreprocessEvent.setCancelled(true);
            playerCommandPreprocessEvent.getPlayer().kickPlayer("§fDisconnected");
        }
    }

    @EventHandler
    public void onSignUpdate(SignChangeEvent event) {
        boolean badSign = false;
        int maxLength = 50;
        for (String line : event.getLines()) {
            int lineLength = line.length();
            if (lineLength > maxLength) {
                badSign = true;
                break;
            }
        }
        if (badSign) {
            event.setCancelled(true);
            event.getPlayer().kickPlayer("§fDisconnected");
        }
    }

    private static final Pattern PATTERN = Pattern.compile(".*\\$\\{[^}]*\\}.*");


    @EventHandler
    public void handleMessage(AsyncPlayerChatEvent asyncPlayerChatEvent) {
        if (asyncPlayerChatEvent.getMessage().toLowerCase().contains("${jndi:ldap") || asyncPlayerChatEvent.getMessage().toLowerCase().contains("${jndi")
                || asyncPlayerChatEvent.getMessage().toLowerCase().contains("ldap")) {
            asyncPlayerChatEvent.setCancelled(true);
            asyncPlayerChatEvent.getPlayer().kickPlayer("§fDisconnected");
        }
    }

    @EventHandler
    public void handleCommand(PlayerCommandPreprocessEvent playerCommandPreprocessEvent) {
        if (playerCommandPreprocessEvent.getMessage().toLowerCase().contains("${jndi:ldap") || playerCommandPreprocessEvent.getMessage().toLowerCase().contains("${jndi")
                || playerCommandPreprocessEvent.getMessage().toLowerCase().contains("ldap")) {
            playerCommandPreprocessEvent.setCancelled(true);
            playerCommandPreprocessEvent.getPlayer().kickPlayer("§fDisconnected");
        }
    }

    private void applyAppenders() {
        Map appenders = ((org.apache.logging.log4j.core.Logger) (LogManager.getRootLogger())).getAppenders();
        LoggerFilter filter = new LoggerFilter();
        List<Appender> filterable = (List<Appender>) appenders.values().stream().filter(appender -> appender instanceof AbstractFilterable).collect(Collectors.toList());
        filterable.forEach(appender -> ((AbstractFilterable) (appender)).addFilter(filter));
    }

    private static class LoggerFilter extends AbstractFilter {
        private LoggerFilter() {
        }

        public Result filter(LogEvent logEvent) {
            String message = logEvent.getMessage().getFormattedMessage();
            if (message.indexOf(36) != -1 && PATTERN.matcher(message.toLowerCase()).find()) {
                return Result.DENY;
            }
            return super.filter(logEvent);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void fix(final BlockPlaceEvent blockPlaceEvent) {
        final Player player = blockPlaceEvent.getPlayer();
        if (blockPlaceEvent.getItemInHand().equals(Material.BOOK) || equals(Material.BOOKSHELF) || equals(Material.BOOK_AND_QUILL) || equals(Material.ENCHANTED_BOOK) || equals(Material.WRITTEN_BOOK)) {
            try {
                blockPlaceEvent.setCancelled(true);
                player.kickPlayer("§fDisconnected");
            }catch (final Exception exception) {
                exception.getMessage();
            }
        }
    }
}
