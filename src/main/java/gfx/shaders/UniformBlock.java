package gfx.shaders;

import static org.lwjgl.opengl.GL31.*;

public class UniformBlock {
  private static int current_binding = 0;
  
  private final int id;
  public  final int binding;
  
  public UniformBlock(int id, int shader_id) {
    this.id = id;
    this.binding = current_binding++;
    
    glUniformBlockBinding(shader_id, this.id, this.binding);
  }
  
  public void bind(UniformBuffer ub) {
    ub.bindTo(this.id);
  }
}
