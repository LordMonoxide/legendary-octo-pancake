package gfx.shaders;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL31.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import gfx.Destroyable;

public abstract class Shader {
  @SuppressWarnings("null")
  private static final Path shader_dir = Paths.get("gfx", "shaders"); //$NON-NLS-1$ //$NON-NLS-2$
  
  private final int id;
  
  public final UniformBlock transforms;
  public final Uniform      model;
  
  public Shader(String vsh, String fsh) {
    System.out.println("Compiling shader..."); //$NON-NLS-1$
    
    String vsh_source;
    String fsh_source;
    
    try {
      vsh_source = new String(Files.readAllBytes(shader_dir.resolve(vsh)));
      fsh_source = new String(Files.readAllBytes(shader_dir.resolve(fsh)));
    } catch(IOException e) {
      e.printStackTrace();
      this.id = GL_INVALID_INDEX;
      this.transforms = this.bindUniformBlock("transforms"); //$NON-NLS-1$
      this.model      = this.bindUniform("model"); //$NON-NLS-1$
      return;
    }
    
    int v_id = createShader(GL_VERTEX_SHADER,   vsh_source);
    int f_id = createShader(GL_FRAGMENT_SHADER, fsh_source);
    this.id = linkShader(v_id, f_id);
    
    System.out.println("Shader compiled to ID " + this.id); //$NON-NLS-1$
    
    glDeleteShader(v_id);
    glDeleteShader(f_id);
    
    this.transforms = this.bindUniformBlock("transforms"); //$NON-NLS-1$
    this.model      = this.bindUniform("model"); //$NON-NLS-1$
    
    Destroyable.registerDestroyable(this, this.id);
  }
  
  public void destroy() {
    glDeleteProgram(this.id);
    
    Destroyable.unregisterDestroyable(this, this.id);
  }
  
  public void use() {
    glUseProgram(this.id);
  }
  
  private int createShader(int type, String source) {
    int s_id = glCreateShader(type);
    
    glShaderSource(s_id, source);
    glCompileShader(s_id);
    
    if(glGetShaderi(s_id, GL_COMPILE_STATUS) == GL_FALSE) {
      int size = glGetShaderi(s_id, GL_INFO_LOG_LENGTH);
      String error = glGetShaderInfoLog(s_id, size);
      System.err.println("Error compiling shader:\n" + error); //$NON-NLS-1$
    }
    
    return s_id;
  }
  
  private int linkShader(int v_id, int f_id) {
    int p_id = glCreateProgram();
    
    glAttachShader(p_id, v_id);
    glAttachShader(p_id, f_id);
    glLinkProgram(p_id);
    
    if(glGetProgrami(p_id, GL_LINK_STATUS) == GL_FALSE) {
      int size = glGetProgrami(p_id, GL_INFO_LOG_LENGTH);
      String error = glGetProgramInfoLog(p_id, size);
      System.err.println("Error linking shader:\n" + error); //$NON-NLS-1$
    }
    
    glDetachShader(p_id, v_id);
    glDetachShader(p_id, f_id);
    
    return p_id;
  }
  
  protected Uniform bindUniform(String name) {
    int uniform_id = glGetUniformLocation(this.id, name);
    
    if(uniform_id == GL_INVALID_INDEX) {
      System.err.println("Uniform " + name + " not found in shader " + this.id); //$NON-NLS-1$ //$NON-NLS-2$
    }
    
    return new Uniform(uniform_id);
  }
  
  protected UniformBlock bindUniformBlock(String name) {
    int block_id = glGetUniformBlockIndex(this.id, name);
    
    if(block_id == GL_INVALID_INDEX) {
      System.err.println("Uniform block " + name + " not found in shader " + this.id); //$NON-NLS-1$ //$NON-NLS-2$
    }
    
    return new UniformBlock(block_id, this.id);
  }
}
