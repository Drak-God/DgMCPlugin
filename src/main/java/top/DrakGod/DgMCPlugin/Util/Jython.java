package top.DrakGod.DgMCPlugin.Util;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;

import top.DrakGod.DgMCPlugin.Global;
import top.DrakGod.DgMCPlugin.Main;
import top.DrakGod.DgMCPlugin.Functions.HttpConnection;

@SuppressWarnings("deprecation")
public class Jython implements Global {
    public boolean Can_Use;
    public final String Python_Library;

    public Jython(String Library) { 
        new Thread(this::Init).start();
        Python_Library = Library;
    }

    public void Init() {
        Can_Use = false;
        if (!Check_Downloads()) {
            boolean Success = Download();
            if (!Success) {
                Module_Log("ERROR", "§2Jython", "§cJython功能无法使用!");
            }
        }
        Can_Use = true;
    }

    public File Get_Jython_File() {
        Path Libs_Path = Get_Data_Folder().toPath();
        Libs_Path = Libs_Path.resolve("Libs");
        Libs_Path = Libs_Path.resolve("Jython.jar");
        return new File(Libs_Path.toString());
    }

    public boolean Download() {
        File Libs_Flie = Get_Jython_File();
        Module_Log("INFO", "§2Jython_Download", "§6正在下载Jython...");

        File Out = HttpConnection.Download("https://github.com/Drak-God/DgMCPlugin/downloads/Jython.jar", Libs_Flie);
        if (Out != null) {
            Module_Log("INFO", "§2Jython_Download", "§a下载Jython成功!");
            return true;
        }

        Out = HttpConnection.Download(
                "https://repo1.maven.org/maven2/org/python/jython-standalone/2.7.4/jython-standalone-2.7.4.jar",
                Libs_Flie);
        if (Out != null) {
            Module_Log("INFO", "§2Jython_Download", "§a下载Jython成功!");
            return true;
        }

        Module_Log("ERROR", "§2Jython_Download", "下载Jython失败!");
        return false;
    }

    public boolean Check_Downloads() {
        File Lib_Flie = Get_Jython_File();
        if (Lib_Flie.exists()) {
            return true;
        } else {
            return false;
        }
    }

    public boolean Run_Py(File Py_File) {
        if (!Can_Use) {
            return false;
        }
        try {
            URL[] URLs = new URL[] { Get_Jython_File().toURL() };
            URLClassLoader ClassLoader = new URLClassLoader(URLs, Main.class.getClassLoader());
            Class<?> Cls = ClassLoader.loadClass("org.python.util.PythonInterpreter");

            Object Interpreter = Cls.getDeclaredConstructor().newInstance();
            Method Exec_Method = Cls.getMethod("exec", String.class);
            Method Exec_File_Method = Cls.getMethod("execfile", String.class);
            Exec_Method.invoke(Interpreter, "import sys");
            Exec_Method.invoke(Interpreter, "sys.path.append('" + Python_Library + "')");
            Exec_File_Method.invoke(Interpreter, Py_File.getAbsolutePath());

            ClassLoader.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
