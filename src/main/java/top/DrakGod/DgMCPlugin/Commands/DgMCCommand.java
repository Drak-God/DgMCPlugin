package top.DrakGod.DgMCPlugin.Commands;

import java.util.HashMap;

import org.bukkit.command.CommandSender;

import top.DrakGod.DgMCPlugin.Global;
import top.DrakGod.DgMCPlugin.Main;

public interface DgMCCommand extends Global {
    public DgMCCommand Init();

    public String Get_Command_Name();

    public boolean On_Command(Main Main, CommandSender Sender, String Label, String[] Args);

    public default boolean Call(Main Main, CommandSender Sender, String Label, String[] Args) {
        HashMap<String, String> Permissions = Main.Class_Commands.Command_Permissions;
        if (!Sender.hasPermission(Permissions.get(Get_Command_Name()))) {
            Sender.sendMessage("§c你没有权限使用此命令");
            return true;
        }

        return On_Command(Main, Sender, Label, Args);
    }

    public default void Register_Command() {
        DgMCCommand Command = Init();
        String Command_Name = Get_Command_Name();
        Get_Main().Class_Commands.Command_Classes.put(Command_Name, Command);
    };
}
