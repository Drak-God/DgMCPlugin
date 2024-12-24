package top.DrakGod.DgMCPlugin;

import java.io.File;
import java.io.IOException;

import org.bukkit.plugin.java.JavaPlugin;

import top.DrakGod.DgMCPlugin.Handlers.Commands;
import top.DrakGod.DgMCPlugin.Handlers.Listeners;

public class Main extends JavaPlugin implements Global {
    public Commands Class_Commands;
    public Listeners Class_Listeners;
    public QQBinds Class_QQBinds;

    public String Version;

    @Override
    public void onEnable() {
        Version = getDescription().getVersion();
        Server_Info("");
        Server_Info("§1┏━━  ┏━━┓ §4┏┓┏┓ ┏━━┓   §fDgMCPlugin");
        Server_Info("§1┃  ┃ ┃ ━┓ §4┃┗┛┃ ┃      §fv"+Version);
        Server_Info("§1┗━━  ┗━━┛ §4┗  ┛ ┗━━┛   §fBy DrakGod");
        Server_Info("");
    
        Class_Commands = new Commands();
        Class_Listeners = new Listeners();
        Class_QQBinds = new QQBinds();

        Check_Data_Folder();
        if (!isEnabled()) {return;}

        PluginManager.registerEvents(Class_Listeners, this);
        Plugin_Info("§1Dg§4MC§6专属插件§a已启用!");
    }

    @Override
    public void onDisable() {
        Plugin_Info("§1Dg§4MC§6专属插件§4已禁用!");
    }

    public File Get_File() {return getFile();}

    public void Check_Data_Folder() {
        File Data_Folder = Get_Data_Folder();
        if (!Data_Folder.exists()) {
            Plugin_Info("未检测到数据文件夹,正在初始化数据文件夹");
            Data_Folder.mkdirs();
            try {
                Copy_Data();
            } catch (IOException e) {
                Plugin_Info("数据文件夹初始化失败:"+e.toString());
                setEnabled(false);
                return;
            }
            Plugin_Info("数据文件夹初始化成功");
        }
    }
}