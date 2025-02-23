package top.DrakGod.DgMCPlugin.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.PluginDescriptionFile;

import org.bukkit.command.CommandSender;

import top.DrakGod.DgMCPlugin.Global;
import top.DrakGod.DgMCPlugin.Main;
import top.DrakGod.DgMCPlugin.Functions.*;

@SuppressWarnings({ "unchecked", "rawtypes", "static-access" })
public interface DgMCCommand extends Global {
    public void Init();

    public String Get_Command_Name();

    public List<String> On_TabComplete(Main Main, CommandSender Sender, String Label, String[] Args);

    public boolean On_Command(Main Main, CommandSender Sender, String Label, String[] Args);

    public default Map<String, Object> Get_Command_Config() {
        PluginDescriptionFile Description_File = Get_Main().getDescription();
        return Description_File.getCommands().get(Get_Command_Name());
    }

    public default PluginCommand Get_Command() {
        return Get_Main().getCommand(Get_Command_Name());
    };

    public default String Get_Usage() {
        return (String) Get_Command_Config().get("usage");
    }

    public default List<String> Get_Aliases() {
        return (List<String>) Get_Command_Config().get("aliases");
    }

    public default String Get_Description() {
        return (String) Get_Command_Config().get("description");
    }

    public default String Get_Permission() {
        return (String) Get_Command_Config().get("permission");
    }

    public default List<String> TabComplete(CommandSender Sender, Command Command, String Label, String[] Args) {
        return On_TabComplete(Get_Main(), Sender, Label, Args);
    };

    public default boolean Command(CommandSender Sender, Command Command, String Label, String[] Args) {
        Main Main = Get_Main();

        if (!Sender.hasPermission(Get_Permission())) {
            Sender.sendMessage("§c你没有权限使用此命令");
            return true;
        }

        return On_Command(Main, Sender, Label, Args);
    }

    public default void Register_Command() {
        String Command_Name = Get_Command_Name();
        PluginCommand Command = Get_Main().getCommand(Command_Name);

        Map<String, Object> Command_Config = Get_Command_Config();

        Command.setUsage((String) Command_Config.get("usage"));
        Command.setAliases((List<String>) Command_Config.get("aliases"));
        Command.setDescription((String) Command_Config.get("description"));
        Command.setPermission("");

        Command.setExecutor(this::Command);
        Command.setTabCompleter(this::TabComplete);
        Get_Main().Commands.put(Command_Name, this);
    }

    public static void Register_Commands() {
        List<Class> Class_List = Classes.Get_Package_Classes(Main.class.getPackageName() + ".Commands");

        List<Class<DgMCCommand>> Command_Classes = new ArrayList<>();
        for (Class Clazz : Class_List) {
            if (DgMCCommand.class.isAssignableFrom(Clazz)) {
                Command_Classes.add((Class<DgMCCommand>) Clazz);
            }
        }

        List<DgMCCommand> Command_Instances = Classes.Get_Instances(Command_Classes);
        Command_Instances.forEach((Command) -> {
            Command.Register_Command();
        });
        Command_Instances.forEach((Command) -> {
            Command.Init();
        });
    }
}
