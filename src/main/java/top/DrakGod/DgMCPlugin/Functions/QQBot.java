package top.DrakGod.DgMCPlugin.Functions;

import org.python.util.PythonInterpreter;

public class QQBot {
    public static void main(String[] args) {
        PythonInterpreter interpreter = new PythonInterpreter();
        interpreter.exec("print('Hello, Jython!')");
    }
}
