package top.DrakGod.DgMCPlugin.Commands;

import java.util.Arrays;
import java.util.HashMap;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import top.DrakGod.DgMCPlugin.Main;
import top.DrakGod.DgMCPlugin.Handlers.Commands;

public class dgmc implements DgMCCommand {
    public Commands Class_Commands;
    public HashMap<String, Command> Commands;

    public DgMCCommand Init() {
        Main Main = Get_Main();
        Class_Commands = Main.Class_Commands;
        Commands = Class_Commands.Commands;

        return this;
    }

    public String Get_Command_Name() {
        return "dgmc";
    }

    public boolean On_Command(Main Main, CommandSender Sender, String Label, String[] Args) {
        String[] New_Args = Args;
        Command SubCommand = Commands.get("help");
        if (Args.length != 0) {
            String SubCommand_Name = Args[0];
            New_Args = Arrays.copyOfRange(Args, 1, Args.length);
            SubCommand = Commands.getOrDefault(SubCommand_Name, Commands.get("dgmc"));
        }

        if (SubCommand == Commands.get("dgmc")) {
            Sender.sendMessage("§c子命令错误,显示帮助");
            SubCommand = Commands.get("help");
            New_Args = new String[0];
        }
        return Class_Commands.onCommand(Sender, SubCommand, SubCommand.getLabel(), New_Args);
    }
}
