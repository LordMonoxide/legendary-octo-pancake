package gfx.shaders;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL31.*;

import java.nio.IntBuffer;

import org.joml.Matrix4f;

import gfx.Buffers;

public class UniformBuffer {
  private final int id;
  private final int size;
  
  public UniformBuffer(int size) {
    this.id = glGenBuffers();
    this.size = size;
    glBindBuffer(GL_UNIFORM_BUFFER, this.id);
    glBufferData(GL_UNIFORM_BUFFER, size, GL_STREAM_DRAW);
    glBindBuffer(GL_UNIFORM_BUFFER, 0);
  }
  
  public void set(Matrix4f mat) {
    this.set(0, mat);
  }
  
  public void set(int offset, Matrix4f mat) {
    glBindBuffer(GL_UNIFORM_BUFFER, this.id);
    glBufferSubData(GL_UNIFORM_BUFFER, offset, Buffers.of(mat));
    glBindBuffer(GL_UNIFORM_BUFFER, 0);
  }
  
  public void set(IntBuffer data) {
    this.set(0, data);
  }
  
  public void set(int offset, IntBuffer data) {
    glBindBuffer(GL_UNIFORM_BUFFER, this.id);
    glBufferSubData(GL_UNIFORM_BUFFER, offset, data);
    glBindBuffer(GL_UNIFORM_BUFFER, 0);
  }
  
  void bindTo(int block_id) {
    glBindBufferRange(GL_UNIFORM_BUFFER, block_id, this.id, 0, this.size);
  }
}
