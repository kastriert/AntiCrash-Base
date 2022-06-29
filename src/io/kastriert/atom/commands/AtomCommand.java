package io.kastriert.atom.commands;
/*

Created by extSayzz 



*/

import io.kastriert.atom.Atom;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class AtomCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if (args.length != 0) {
            sender.sendMessage("§7This Server is secured by " + Atom.PREFIX + " §7by §fkastriert");
        }else {
            sender.sendMessage("§7This Server is secured by " + Atom.PREFIX + " §7by §fkastriert");
        }
        return false;
    }
}
