package com.owen2k6.fyb;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class FreezeYouBastard extends JavaPlugin implements Listener {

    private final Map<UUID, Location> frozenPlayers = new HashMap<>();

    @Override
    public void onDisable() {
        frozenPlayers.clear();
        getServer().getLogger().log(java.util.logging.Level.INFO, "FreezeYouBastard has been disabled.");
    }

    @Override
    public void onEnable() {
        getServer().getLogger().log(java.util.logging.Level.INFO, "FreezeYouBastard has been enabled.");
        getServer().getPluginManager().registerEvents(this, this);
    }

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (cmd.getName().equalsIgnoreCase("freeze")) {
            if (args.length != 1) {
                sender.sendMessage("Usage: /freeze <player>");
                return true;
            }

            Player target = Bukkit.getPlayer(args[0]);

            if (target == null) {
                sender.sendMessage(ChatColor.RED + "Unable to find player " + ChatColor.BLUE + args[0] + ChatColor.RED + ". Please check the spelling and try again.");
                return true;
            }


            toggleFreeze(target);
            if (isFrozen(target)) {
                sender.sendMessage(ChatColor.AQUA + target.getName() + " has been frozen.");
                target.sendMessage(ChatColor.AQUA + "You have been frozen.");
            } else {
                sender.sendMessage(ChatColor.GREEN + target.getName() + " has been unfrozen.");
                target.sendMessage(ChatColor.GREEN + "You have been unfrozen.");
            }

            return true;
        }

        return false;
    }

    ;

    @EventHandler(priority = Event.Priority.Lowest)
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (isFrozen(player)) {
            player.teleport(frozenPlayers.get(player.getUniqueId()));
            event.setCancelled(true);

        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (isFrozen(player)) {
            player.sendMessage(ChatColor.RED + "You can not interact with the world while frozen.");
            player.teleport(frozenPlayers.get(player.getUniqueId()));
            event.setCancelled(true);
        }
    }

    private void toggleFreeze(Player player) {
        UUID uuid = player.getUniqueId();
        if (isFrozen(player)) {
            player.teleport(frozenPlayers.get(uuid));
            frozenPlayers.remove(uuid);
        } else {
            frozenPlayers.put(uuid, player.getLocation());
        }
    }

    private boolean isFrozen(Player player) {
        return frozenPlayers.containsKey(player.getUniqueId());
    }
}
