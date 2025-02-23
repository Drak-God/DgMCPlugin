package top.DrakGod.DgMCPlugin.Commands.dgmcCommands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;

import top.DrakGod.DgMCPlugin.Main;
import top.DrakGod.DgMCPlugin.Util.DgMCCommand;

public class reload implements DgMCCommand {
    @Override
    public void Init() {
    }

    @Override
    public String Get_Command_Name() {
        return "reload";
    }

    @Override
    public PluginCommand Get_Command() {
        return Get_Main().Commands.get("dgmc").Get_Command();
    }

    @Override
    public String Get_Usage() {
        return "/dgmc reload";
    }

    @Override
    public List<String> Get_Aliases() {
        return new ArrayList<>();
    }

    @Override
    public String Get_Description() {
        return "重载插件";
    }

    @Override
    public String Get_Permission() {
        return "dgmc.commands.dgmc.reload";
    }

    @Override
    public List<String> On_TabComplete(Main Main, CommandSender Sender, String Label, String[] Args) {
        return new ArrayList<>();
    }

    @Override
    public boolean On_Command(Main Main, CommandSender Sender, String Label, String[] Args) {
        try {
            Main.onDisable();
            Main.onEnable();
            Sender.sendMessage(Plugin_Name + "§a重载成功");
        } catch (Exception e) {
            Sender.sendMessage(Plugin_Name + "§c重载失败");
            e.printStackTrace();
        }
        return true;
    }
}
