package shader;

import files.TextFiles;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL11.*;

public class Shader {
    private static final Logger log = LogManager.getLogger(Shader.class);

    int program;
    private int vertexShader;
    private int fragmentShader;

    private FloatBuffer matBuffer = BufferUtils.createFloatBuffer(16);
    private FloatBuffer vecBuffer = BufferUtils.createFloatBuffer(3);

    public Shader(String shaderName) {
        vertexShader = compileShader(shaderName, GL_VERTEX_SHADER);
        fragmentShader = compileShader(shaderName, GL_FRAGMENT_SHADER);

        program = glCreateProgram();
        glAttachShader(program, vertexShader);
        glAttachShader(program, fragmentShader);

        glLinkProgram(program);
        glValidateProgram(program);

        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);

        log.debug("New " + shaderName + " shader program created.");
    }

    public void uniform3v(String name, Vector3f vector) {
        int loc = getUniformLocation(name);

        vector.get(vecBuffer);
        glUniform3fv(loc, vecBuffer);
    }

    public void uniform4fv(String name, Matrix4f matrix) {
        int loc = getUniformLocation(name);

        matrix.get(matBuffer);
        glUniformMatrix4fv(loc, false, matBuffer);
    }

    public int getProgram() {
        return program;
    }

    public void clean() {
        glDeleteProgram(program);
        log.debug("Shader program deleted.");
    }

    private int getUniformLocation(String name) {
        int loc = glGetUniformLocation(program, name);
        if (loc == -1) {
            log.error("Location not found in shader program for '" + name + "'");
            throw new RuntimeException("No such uniform found");
        }
        return loc;
    }

    private int compileShader(String name, int type) {
        String suffix;

        if (type == GL_VERTEX_SHADER) {
            suffix = "_vert.glsl";
            log.debug("Starting to compile vertex shader.");
        } else if (type == GL_FRAGMENT_SHADER) {
            suffix = "_frag.glsl";
            log.debug("Starting to compile fragment shader.");
        } else {
            log.error("Wrong shader type");
            throw new RuntimeException("Wrong shader type");
        }

        int shader = glCreateShader(type);
        glShaderSource(shader, TextFiles.readTextResource(name + suffix, this.getClass()));
        glCompileShader(shader);

        if (glGetShaderi(shader, GL_COMPILE_STATUS) == GL_FALSE) {
            String error = glGetShaderInfoLog(shader, 500);
            log.error(error);
            throw new RuntimeException("Problem with shader");
        }

        log.debug("Shader compilation was successful.");
        return shader;
    }
}
