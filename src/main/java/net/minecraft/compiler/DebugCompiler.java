package net.minecraft.compiler;

import javax.tools.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class DebugCompiler {

    public static void main(String[] args) throws Exception {
        String code = "public class DynamicClass { public void execute() { System.out.println(\"Hello, world!\"); } }";
        compileAndRun(code, "DynamicClass");
    }

    public static void compileAndRun(String code, String className) throws Exception {
        // Save source in .java file.
        File sourceFile = new File(className + ".java");
        try (FileWriter writer = new FileWriter(sourceFile)) {
            writer.write(code);
        }

        // Compile source file.
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        compiler.run(null, null, null, sourceFile.getPath());

        // Load and instantiate compiled class.
        URLClassLoader classLoader = URLClassLoader.newInstance(new URL[] { new File("").toURI().toURL() });
        Class<?> cls = Class.forName(className, true, classLoader);
        Object instance = cls.getDeclaredConstructor().newInstance();
        cls.getMethod("execute").invoke(instance);
    }
}
