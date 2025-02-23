package top.DrakGod.DgMCPlugin.Functions;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import top.DrakGod.DgMCPlugin.Main;

@SuppressWarnings("rawtypes")
public class Classes {
    public static Class Get_Class(String Class_Name) {
        try {
            return Class.forName(Class_Name);
        } catch (Exception e) {
            return null;
        }
    }

    public static List<Class> Get_Package_Classes(String Package_Name) {
        return Get_Package_Class_Names(Package_Name)
                .stream()
                .map(Classes::Get_Class)
                .toList();
    }

    public static List<String> Get_Package_Class_Names(String Package_Name) {
        String Path = Package_Name.replace(".", "/");
        List<String> Classes = new ArrayList<>();

        try {
            JarFile Jar_File = new JarFile(Main.getPlugin(Main.class).Get_File());
            Enumeration<JarEntry> Entries = Jar_File.entries();

            while (Entries.hasMoreElements()) {
                String Entry_Name = Entries.nextElement().getName();

                if (!(Entry_Name.startsWith(Path) && Entry_Name.endsWith(".class"))) {
                    continue;
                }

                if (Path.split("/").length + 1 != Entry_Name.split("/").length) {
                    continue;
                }

                Classes.add(Entry_Name.replace("/", ".").substring(0, Entry_Name.length() - 6));
            }
            Jar_File.close();
        } catch (Exception e) {
            return null;
        }

        return Classes;
    }

    public static <T> T Get_Instance(Class<T> Class) {
        try {
            Constructor<T> Constructor = Class.getConstructor();
            Constructor.setAccessible(true);
            return Constructor.newInstance();
        } catch (Exception e) {
            return null;
        }
    }

    public static <T> T Get_Instance(Class<T> Class, Class<?>[] Parameter_Types) {
        try {
            Constructor<T> Constructor = Class.getConstructor(Parameter_Types);
            Constructor.setAccessible(true);
            return Constructor.newInstance();
        } catch (Exception e) {
            return null;
        }
    }

    public static <T> List<T> Get_Instances(List<Class<T>> Classes) {
        try {
            List<T> Instances = new ArrayList<>();
            for (Class<T> Class : Classes) {
                Instances.add(Get_Instance(Class));
            }
            return Instances;
        } catch (Exception e) {
            return null;
        }
    }

    public static <T> List<T> Get_Instances(List<Class<T>> Classes, Class<?>[][] Parameter_Types) {
        if (Classes.size() != Parameter_Types.length) {
            return null;
        }

        try {
            List<T> Instances = new ArrayList<>();
            Iterator<Class<T>> Class_Iterator = Classes.iterator();
            Iterator<Class<?>[]> Type_Iterator = Arrays.asList(Parameter_Types).iterator();
            while (Class_Iterator.hasNext() && Type_Iterator.hasNext()) {
                Class<T> Class = Class_Iterator.next();
                Class<?>[] Types = Type_Iterator.next();

                Instances.add(Get_Instance(Class, Types));
            }
            return Instances;
        } catch (Exception e) {
            return null;
        }
    }
}
