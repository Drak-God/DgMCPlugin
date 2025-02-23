package top.DrakGod.DgMCPlugin.Commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.bukkit.command.CommandSender;

import top.DrakGod.DgMCPlugin.Main;
import top.DrakGod.DgMCPlugin.Functions.Classes;
import top.DrakGod.DgMCPlugin.Util.DgMCCommand;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class dgmc implements DgMCCommand {
    public HashMap<String, DgMCCommand> Commands;
    public HashMap<String, DgMCCommand> All_Commands;

    @Override
    public void Init() {
        Commands = Get_Main().Commands;
        All_Commands = (HashMap) Commands.clone();
        Register_dgmcCommands();
    }

    @Override
    public String Get_Command_Name() {
        return "dgmc";
    }

    @Override
    public List<String> On_TabComplete(Main Main, CommandSender Sender, String Label, String[] Args) {
        List<String> Tab_Completes = new ArrayList<>();
        if (Args.length == 1) {
            Tab_Completes = new ArrayList(All_Commands.keySet());
            Tab_Completes.remove("dgmc");

            Iterator<String> Command_Iterator = Tab_Completes.iterator();
            while (Command_Iterator.hasNext()) {
                String Command_Name = Command_Iterator.next();
                if (!Sender.hasPermission(All_Commands.get(Command_Name).Get_Permission())) {
                    Command_Iterator.remove();
                }
            }
        } else {
            DgMCCommand SubCommand = All_Commands.get(Args[0]);
            if (SubCommand != null) {
                String[] New_Args = Arrays.copyOfRange(Args, 1, Args.length);
                Tab_Completes = SubCommand.On_TabComplete(Main, Sender, Label, New_Args);
            }
        }
        return Tab_Completes;
    }

    @Override
    public boolean On_Command(Main Main, CommandSender Sender, String Label, String[] Args) {
        String[] New_Args = Args;
        DgMCCommand SubCommand = null;

        if (Args.length != 0) {
            String SubCommand_Name = Args[0];
            New_Args = Arrays.copyOfRange(Args, 1, Args.length);
            SubCommand = All_Commands.get(SubCommand_Name);
        }

        if (SubCommand == null) {
            Sender.sendMessage("§c子命令错误,显示帮助");
            SubCommand = All_Commands.get("help");
            New_Args = new String[0];
        }

        return SubCommand.Command(Sender, SubCommand.Get_Command(), SubCommand.Get_Command_Name(), New_Args);
    }

    public void Register_dgmcCommands() {
        List<Class> Class_List = Classes.Get_Package_Classes(dgmc.class.getPackageName() + ".dgmcCommands");

        List<Class<DgMCCommand>> Command_Classes = new ArrayList<>();
        for (Class Clazz : Class_List) {
            if (DgMCCommand.class.isAssignableFrom(Clazz)) {
                Command_Classes.add((Class<DgMCCommand>) Clazz);
            }
        }

        List<DgMCCommand> Command_Instances = Classes.Get_Instances(Command_Classes);
        Command_Instances.forEach((Command) -> {
            All_Commands.put(Command.Get_Command_Name(), Command);
        });
        Command_Instances.forEach((Command) -> {
            Command.Init();
        });
    }
}
