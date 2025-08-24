package net.minecraft.client.renderer;

import org.lwjgl.opengl.GL20;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class ShaderLoader {


    public static int GLOW_SHADER_PROGRAM;

    public static void initShaders() {
        GLOW_SHADER_PROGRAM = ShaderLoader.loadShader(
                "assets/minecraft/shaders/basic_glow.vsh",
                "assets/minecraft/shaders/basic_glow.fsh"
        );
    }

    public static int loadShader(String vertexPath, String fragmentPath) {
        int vertexShader = compileShader(vertexPath, GL20.GL_VERTEX_SHADER);
        int fragmentShader = compileShader(fragmentPath, GL20.GL_FRAGMENT_SHADER);

        int program = GL20.glCreateProgram();
        GL20.glAttachShader(program, vertexShader);
        GL20.glAttachShader(program, fragmentShader);
        GL20.glLinkProgram(program);

        if (GL20.glGetProgrami(program, GL20.GL_LINK_STATUS) == GL20.GL_FALSE) {
            System.err.println("Shader linking failed: " + GL20.glGetProgramInfoLog(program, 1024));
            return 0;
        }

        return program;
    }

    private static int compileShader(String path, int type) {
        String source = readResourceAsString(path);
        int shader = GL20.glCreateShader(type);
        GL20.glShaderSource(shader, source);
        GL20.glCompileShader(shader);

        if (GL20.glGetShaderi(shader, GL20.GL_COMPILE_STATUS) == GL20.GL_FALSE) {
            System.err.println("Shader compile failed: " + path + " " + GL20.glGetShaderInfoLog(shader, 1024));
            return 0;
        }

        return shader;
    }

    private static String readResourceAsString(String path) {
        try (InputStream inputStream = ShaderLoader.class.getClassLoader().getResourceAsStream(path)) {
            if (inputStream == null) {
                throw new IllegalArgumentException("Shader file not found: " + path);
            }
            Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8.name());
            return scanner.useDelimiter("\\A").next();
        } catch (Exception e) {
            throw new RuntimeException("Error reading shader file: " + path, e);
        }
    }
}
