package top.DrakGod.DgMCPlugin.Handlers;

import top.DrakGod.DgMCPlugin.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;

public class Commands implements CommandExecutor, Global {
    public Map<String,PluginCommand> Commands;

    public Commands() {
        Commands = new HashMap<String,PluginCommand>();
        Get_Main().getDescription().getCommands().forEach((Name, Value) -> {
            PluginCommand Command = Get_Main().getCommand(Name);
            Command.setUsage((String) Value.get("usage"));
            Command.setAliases((List<String>) Value.get("aliases"));
            Command.setDescription((String) Value.get("description"));
            Command.setExecutor(this);
            Commands.put(Name,Command);
        });
    }

    @Override
    public boolean onCommand(CommandSender Sender, Command Command, String Label, String[] Args) {
        return false;
    }
}
