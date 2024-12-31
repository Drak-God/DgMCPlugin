package top.DrakGod.DgMCPlugin.Handlers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import top.DrakGod.DgMCPlugin.Global;
import top.DrakGod.DgMCPlugin.Main;

/**
 * 命令处理器类，实现CommandExecutor和Global接口
 */
public class Commands implements CommandExecutor, Global {

    // 存储命令对象的Map
    public Map<String, Command> Commands;
    // 存储帮助页面的列表，每个元素是一个包含命令名称的列表
    public List<List<String>> Help_Pages;

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
            PluginCommand Command = Main_Class.getCommand(Name);
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
        // 获取命令的数量
        Integer Commands_Lenth = Commands.size();
        // 计算帮助页面的数量
        Integer Help_Pages_Number = Commands_Lenth / 6 + 1;
        // 初始化帮助页面列表
        Help_Pages = new ArrayList<>();
        // 遍历每个帮助页面
        for (int i = 0; i < Help_Pages_Number; i++) {
            // 创建一个新的帮助页面列表
            List<String> Help_Page = new ArrayList<>();
            // 计算当前页面的命令数量
            Integer Page_Commands_Number = Commands_Lenth - i * 6;
            // 如果命令数量超过6个，则限制为6个
            if (Page_Commands_Number > 6) {
                Page_Commands_Number = 6;
            }
            // 将命令名称添加到帮助页面列表中
            for (int j = 0; j < Page_Commands_Number; j++) {
                Help_Page.add((String) Commands.keySet().toArray()[i * 6 + j]);
            }
            // 将帮助页面列表添加到总帮助页面列表中
            Help_Pages.add(Help_Page);
        }
    }

    /**
     * 处理命令执行的方法
     *
     * @param Sender 命令发送者
     * @param Command 执行的命令
     * @param Label 命令的别名
     * @param Args 命令的参数
     * @return 如果命令执行成功则返回true，否则返回false
     */
    @Override
    public boolean onCommand(CommandSender Sender, Command Command, String Label, String[] Args) {
        // 获取命令名称
        String Command_Name = Command.getName();
        // 如果命令名称为"dgmc"
        if (Command_Name.equalsIgnoreCase("dgmc")) {
            // 复制命令参数
            String[] New_Args = Args;
            // 获取"help"命令对象
            Command SubCommand = Commands.get("help");
            // 如果参数长度不为0
            if (Args.length != 0) {
                // 获取子命令名称
                String SubCommand_Name = Args[0];
                // 复制子命令参数
                New_Args = Arrays.copyOfRange(Args, 1, Args.length);
                // 获取子命令对象，如果不存在则使用"dgmc"命令对象
                SubCommand = Commands.getOrDefault(SubCommand_Name, Commands.get("dgmc"));
            }
            // 如果子命令对象为"dgmc"命令对象
            if (SubCommand == Commands.get("dgmc")) {
                // 发送错误消息
                Sender.sendMessage("§c子命令错误,显示帮助");
                // 使用"help"命令对象
                SubCommand = Commands.get("help");
                // 清空参数
                New_Args = new String[0];
            }
            // 递归调用onCommand方法处理子命令
            return onCommand(Sender, SubCommand, SubCommand.getLabel(), New_Args);
        }
        // 如果命令名称为"help"
        if (Command_Name.equalsIgnoreCase("help")) {
            // 获取帮助页面的数量
            Integer Help_Pages_Lenth = Help_Pages.size();
            // 默认显示第一页
            Integer Page_Number = 1;
            // 如果参数长度为1
            if (Args.length == 1) {
                // 获取要显示的页码
                Page_Number = Integer.valueOf(Args[0]);
                // 如果页码超出范围，则显示第一页
                if (Page_Number > Help_Pages_Lenth || Page_Number < 1) {
                    Sender.sendMessage("§c页数错误,回退至第一页");
                    Page_Number = 1;
                }
            } else if (Args.length > 1) {
                // 如果参数过多，则显示错误消息
                Sender.sendMessage("§c参数过多");
                return true;
            }
            // 发送帮助页面的标题
            Sender.sendMessage("§e------ ======= §1Dg§4MC§6Plugin§b帮助 §e======= ------");
            // 发送帮助页面的说明
            Sender.sendMessage("§e====== ------ §6<>为必填 []为选填 §e------ ======");
            // 遍历当前帮助页面的命令
            for (String Name : Help_Pages.get(Page_Number - 1)) {
                // 获取命令对象
                Command HelpCommand = Commands.get(Name);
                // 发送命令的使用说明和描述
                TextComponent Msg = new TextComponent("§6" + HelpCommand.getUsage() + " §e-§6 " + HelpCommand.getDescription());
                Msg.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/" + Name));
                Sender.spigot().sendMessage(Msg);
            }
            // 创建一个ComponentBuilder对象，用于构建点击事件
            ComponentBuilder Msg = new ComponentBuilder();

            // 判断是否为第一页
            boolean Last_Page_Error = Page_Number - 1 < 1;
            // 设置上一页按钮的颜色
            String Last_Page_Button_Color = Last_Page_Error ? "§7" : "§6";
            // 设置上一页的页码
            Integer Last_Page_Number = Last_Page_Error ? Help_Pages_Lenth : Page_Number - 1;
            // 设置上一页的命令
            String Last_Page_Command = "/dgmc help " + Last_Page_Number;
            // 创建上一页按钮
            TextComponent Last_Page_Button = new TextComponent(Last_Page_Button_Color + "上一页(" + Last_Page_Command + ")");

            // 判断是否为最后一页
            boolean Next_Page_Error = Page_Number + 1 > Help_Pages_Lenth;
            // 设置下一页按钮的颜色
            String Next_Page_Button_Color = Next_Page_Error ? "§7" : "§6";
            // 设置下一页的页码
            Integer Next_Page_Number = Next_Page_Error ? 1 : Page_Number + 1;
            // 设置下一页的命令
            String Next_Page_Command = "/dgmc help " + Next_Page_Number;
            // 创建下一页按钮
            TextComponent Next_Page_Button = new TextComponent(Next_Page_Button_Color + "下一页(" + Next_Page_Command + ")");

            // 设置上一页按钮的点击事件
            Last_Page_Button.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, Last_Page_Command));
            // 设置下一页按钮的点击事件
            Next_Page_Button.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, Next_Page_Command));

            // 构建消息
            Msg.append("§e当前页: §6" + Page_Number + "§e/§6" + Help_Pages_Lenth + " ");
            Msg.append(Last_Page_Button);
            Msg.append("§e|");
            Msg.append(Next_Page_Button);
            // 发送消息
            Sender.spigot().sendMessage(Msg.create());
            return true;
        }
        return false;
    }
}
