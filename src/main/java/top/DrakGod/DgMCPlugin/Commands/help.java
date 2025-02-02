package top.DrakGod.DgMCPlugin.Commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import top.DrakGod.DgMCPlugin.Main;
import top.DrakGod.DgMCPlugin.Handlers.Commands;

public class help implements DgMCCommand {
    public Commands Class_Commands;
    public HashMap<String, Command> Commands;
    public List<List<String>> Help_Pages;

    public DgMCCommand Init() {
        Main Main = Get_Main();
        Class_Commands = Main.Class_Commands;
        Commands = Class_Commands.Commands;
        return this;
    }

    public String Get_Command_Name() {
        return "help";
    }

    public boolean On_Command(Main Main, CommandSender Sender, String Label, String[] Args) {
        Reload_Help_Pages(Sender);

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
        String Last_Page_Command = "/help " + Last_Page_Number;
        TextComponent Last_Page_Button = new TextComponent(
                Last_Page_Button_Color + "上一页(" + Last_Page_Command + ")");

        boolean Next_Page_Error = Page_Number + 1 > Help_Pages_Lenth;
        String Next_Page_Button_Color = Next_Page_Error ? "§7" : "§6";
        Integer Next_Page_Number = Next_Page_Error ? 1 : Page_Number + 1;
        String Next_Page_Command = "/help " + Next_Page_Number;
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

    public void Reload_Help_Pages(CommandSender Sender) {
        HashMap<String, String> Command_Permissions = Class_Commands.Command_Permissions;

        HashMap<String, Command> New_Commands = new HashMap<>();
        for (String Name : Commands.keySet()) {
            Command Command = Commands.get(Name);
            if (Sender.hasPermission(Command_Permissions.get(Name))) {
                New_Commands.put(Name, Command);
            }
        }

        Integer Commands_Lenth = New_Commands.size();
        Integer Help_Pages_Number = (int) Math.ceil(Commands_Lenth / 6F);
        Help_Pages = new ArrayList<>();

        Iterator<String> Iterator = New_Commands.keySet().iterator();
        for (int i = 0; i < Help_Pages_Number; i++) {
            List<String> Help_Page = new ArrayList<>();
            int Page_Commands_Number = Math.min(Commands_Lenth - i * 6, 6);
            for (int j = 0; j < Page_Commands_Number; j++) {
                try {
                    Help_Page.add(Iterator.next());
                } catch (Exception e) {
                    break;
                }
            }
            Help_Pages.add(Help_Page);
        }
    }
}
