package top.DrakGod.DgMCPlugin.Handlers;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import top.DrakGod.DgMCPlugin.Global;
import top.DrakGod.DgMCPlugin.Main;

/**
 * 命令处理器类，实现CommandExecutor和Global接口
 */
public class Commands implements CommandExecutor, Global {
    // 存储命令对象的Map
    public Map<String, Command> Commands;

    /**
     * 构造函数，初始化命令处理器
     */
    public Commands() {
        // 创建一个HashMap来存储命令对象
        Commands = new HashMap<>();
        // 获取主插件实例
        Main Main_Class = Get_Main();
        // 遍历主插件描述文件中的所有命令
        Main_Class.getDescription().getCommands().forEach((var Name, var Value) -> {
            // 获取命令对象
            Command Command = Main_Class.getCommand(Name);
            // 设置命令的使用说明
            Command.setUsage((String) Value.get("usage"));
            // 设置命令的别名
            Command.setAliases((List<String>) Value.get("aliases"));
            // 设置命令的描述
            Command.setDescription((String) Value.get("description"));
            // 设置命令的执行器为当前实例
            Command.setExecutor(this);
            // 将命令对象存储到Map中
            Commands.put(Name, Command);
        });
    }

    /**
     * 处理命令执行的方法
     * 
     * @param Sender  命令发送者
     * @param Command 执行的命令
     * @param Label   命令的别名
     * @param Args    命令的参数
     * @return 如果命令执行成功则返回true，否则返回false
     */
    @Override
    public boolean onCommand(CommandSender Sender, Command Command, String Label, String[] Args) {
        String Command_Name = Command.getName();
        if (Command_Name.equalsIgnoreCase("dgmc")) {
            Command SubCommand = Commands.getOrDefault(Args[0],Commands.get("help"));
            onCommand(Sender, SubCommand, SubCommand.getLabel(), Arrays.copyOfRange(Args, 1, -1));
        }
        
    }
}
