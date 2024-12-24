package top.DrakGod.DgMCPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public interface Global {
    public org.bukkit.Server Server = Bukkit.getServer();
    public org.bukkit.plugin.PluginManager PluginManager = Server.getPluginManager();

    public Logger Server_Logger = Server.getLogger();
    public Logger Plugin_Logger = Bukkit.getLogger();
    public CommandSender Console = Server.getConsoleSender();

    public default Main Get_Main() {
        return Main.getPlugin(Main.class);
    }

    public default File Get_Data_Folder() {
        return Get_Main().getDataFolder();
    }

    public default void Server_Info(String msg) {
        Console.sendMessage(msg);
    }

    public default void Plugin_Info(String msg) {
        Console.sendMessage("[DgMCPlugin] "+msg);
    }

    public default void Copy_Data() throws IOException {
        JarFile Jar_File = new JarFile(Get_Main().Get_File());
        Enumeration<JarEntry> Entries = Jar_File.entries();
        File Data_Folder = Get_Data_Folder();
        
        while (Entries.hasMoreElements()) {
            JarEntry Entry = Entries.nextElement();
            String Entry_Name = Entry.getName();

            if (Entry_Name.startsWith("Data/") && !Entry.isDirectory()) {
                InputStream Input_Stream = getClass().getClassLoader().getResourceAsStream(Entry_Name);
                if (Input_Stream != null) {
                    File Target_File = new File(Data_Folder,Entry_Name.substring(5));
                    if (!Target_File.getParentFile().exists()) {
                        Target_File.getParentFile().mkdirs();
                    }
                    Files.copy(Input_Stream,Target_File.toPath(),StandardCopyOption.REPLACE_EXISTING);
                }
            }
        }
        Jar_File.close();
    }
}
