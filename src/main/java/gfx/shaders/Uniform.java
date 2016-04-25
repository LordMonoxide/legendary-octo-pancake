package gfx.shaders;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import gfx.Buffers;

import static org.lwjgl.opengl.GL20.*;

public class Uniform {
  private int id;
  
  public Uniform(int id) {
    this.id = id;
  }
  
  public void set(int val) {
    glUniform1i(this.id, val);
  }
  
  public void set(Vector2f vec) {
    glUniform2f(this.id, vec.x, vec.y);
  }
  
  public void set(Vector3f vec) {
    glUniform3f(this.id, vec.x, vec.y, vec.z);
  }
  
  public void set(Matrix4f mat) {
    glUniformMatrix4fv(this.id, false, Buffers.of(mat));
  }
}
