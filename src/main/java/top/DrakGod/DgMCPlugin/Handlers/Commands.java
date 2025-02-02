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
import org.bukkit.entity.Player;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import top.DrakGod.DgMCPlugin.Global;
import top.DrakGod.DgMCPlugin.Main;

@SuppressWarnings({ "deprecation", "unchecked" })
public class Commands implements CommandExecutor, Global {
    public Map<String, Command> Commands;
    public List<List<String>> Help_Pages;

    public Commands() {
        Commands = new HashMap<>();
        Main Main_Class = Get_Main();
        Main_Class.getDescription().getCommands().forEach((var Name, var Value) -> {
            PluginCommand Command = Main_Class.getCommand(Name);
            Command.setUsage((String) Value.get("usage"));
            Command.setAliases((List<String>) Value.get("aliases"));
            Command.setDescription((String) Value.get("description"));
            Command.setExecutor(this);
            Commands.put(Name, Command);
        });

        Integer Commands_Lenth = Commands.size();
        Integer Help_Pages_Number = Commands_Lenth / 6 + 1;
        Help_Pages = new ArrayList<>();

        for (int i = 0; i < Help_Pages_Number; i++) {
            List<String> Help_Page = new ArrayList<>();
            Integer Page_Commands_Number = Commands_Lenth - i * 6;
            if (Page_Commands_Number > 6) {
                Page_Commands_Number = 6;
            }
            for (int j = 0; j < Page_Commands_Number; j++) {
                Help_Page.add((String) Commands.keySet().toArray()[i * 6 + j]);
            }
            Help_Pages.add(Help_Page);
        }
    }

    @Override
    public boolean onCommand(CommandSender Sender, Command Command, String Label, String[] Args) {
        String Command_Name = Command.getName();
        if (Command_Name.equalsIgnoreCase("dgmc")) {
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
            return onCommand(Sender, SubCommand, SubCommand.getLabel(), New_Args);
        }

        if (Command_Name.equalsIgnoreCase("help")) {
            Integer Help_Pages_Lenth = Help_Pages.size();
            Integer Page_Number = 1;
            if (Args.length == 1) {
                try {
                    Page_Number = Integer.valueOf(Args[0]);
                } catch (Exception e) {
                    Page_Number = 0;
                }
                if (Page_Number > Help_Pages_Lenth || Page_Number < 1) {
                    Sender.sendMessage("§c页数错误,回退至第一页");
                    Page_Number = 1;
                }
            } else if (Args.length > 1) {
                Sender.sendMessage("§c参数过多");
                return true;
            }

            Sender.sendMessage("§e------ ======= §1Dg§4MC§bPlugin§6帮助 §e======= ------");
            Sender.sendMessage("§e====== ------ §6<>为必填 []为选填 §e------ ======");
            for (String Name : Help_Pages.get(Page_Number - 1)) {
                Command HelpCommand = Commands.get(Name);
                TextComponent Msg = new TextComponent(
                        "§6" + HelpCommand.getUsage() + " §e-§6 " + HelpCommand.getDescription());
                Msg.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/" + Name));
                Sender.spigot().sendMessage(Msg);
            }
            ComponentBuilder Msg = new ComponentBuilder();

            boolean Last_Page_Error = Page_Number - 1 < 1;
            String Last_Page_Button_Color = Last_Page_Error ? "§7" : "§6";
            Integer Last_Page_Number = Last_Page_Error ? Help_Pages_Lenth : Page_Number - 1;
            String Last_Page_Command = "/dgmc help " + Last_Page_Number;
            TextComponent Last_Page_Button = new TextComponent(
                    Last_Page_Button_Color + "上一页(" + Last_Page_Command + ")");

            boolean Next_Page_Error = Page_Number + 1 > Help_Pages_Lenth;
            String Next_Page_Button_Color = Next_Page_Error ? "§7" : "§6";
            Integer Next_Page_Number = Next_Page_Error ? 1 : Page_Number + 1;
            String Next_Page_Command = "/dgmc help " + Next_Page_Number;
            TextComponent Next_Page_Button = new TextComponent(
                    Next_Page_Button_Color + "下一页(" + Next_Page_Command + ")");

            Last_Page_Button.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, Last_Page_Command));
            Next_Page_Button.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, Next_Page_Command));

            Msg.append("§e当前页: §6" + Page_Number + "§e/§6" + Help_Pages_Lenth + " ");
            Msg.append(Last_Page_Button);
            Msg.append("§e|");
            Msg.append(Next_Page_Button);
            Sender.spigot().sendMessage(Msg.create());
            return true;
        }

        if (Command_Name.equalsIgnoreCase("idbind")) {
            String Name;
            switch (Args.length) {
                case 0 -> {
                    if (Sender instanceof Player Player) {
                        Name = Player.getName();
                    } else {
                        Sender.sendMessage("§c参数过少");
                        return true;
                    }
                }

                case 1 -> {
                    Name = Args[0];
                    if (!Server.getOfflinePlayer(Name).hasPlayedBefore()) {
                        Sender.sendMessage("§c玩家不存在");
                        return true;
                    }
                }

                default -> {
                    Sender.sendMessage("§c参数过多");
                    return true;
                }
            }

            String ID = Get_Main().Class_IDBinds.Get_IDBind(Name);
            if (ID == null) {
                Sender.sendMessage("§6玩家 " + Name + " §c未绑定ID");
            } else {
                Sender.sendMessage("§6玩家 " + Name + " §a已绑定ID:" + ID);
            }

            return true;
        }
        return false;
    }
}
