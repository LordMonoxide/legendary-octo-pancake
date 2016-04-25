package gfx;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL15;

import gfx.shaders.Shader;
import gfx.textures.Texture;

public class Drawable {
  public final Matrix4f transform = new Matrix4f();
  
  private final Shader shader;
  private final Texture texture;
  private final Texture texture2;
  
  private final int vb;
  private final int ib;
  private final int va;
  
  private final int indices_count;
  
  public Drawable(Shader shader, Texture texture, Texture texture2, float[] vertices, byte[] indices) {
    this.transform.identity();
    this.shader = shader;
    this.texture = texture;
    this.texture2 = texture2;
    this.indices_count = indices.length;
    
    FloatBuffer vertices_buffer = Buffers.of(vertices);
    ByteBuffer  indices_buffer  = Buffers.of(indices);
    
    this.vb = glGenBuffers();
    glBindBuffer(GL_ARRAY_BUFFER, this.vb);
    glBufferData(GL_ARRAY_BUFFER, vertices_buffer, GL_STATIC_DRAW);
    glBindBuffer(GL_ARRAY_BUFFER, 0);
    
    this.ib = glGenBuffers();
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, this.ib);
    glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices_buffer, GL_STATIC_DRAW);
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    
    this.va = glGenVertexArrays();
    glBindVertexArray(this.va);
    glBindBuffer(GL_ARRAY_BUFFER, this.vb);
    glEnableVertexAttribArray(0);
    glEnableVertexAttribArray(1);
    glEnableVertexAttribArray(2);
    glVertexAttribPointer(0, 3, GL_FLOAT, false, 9 * 4, 0);
    glVertexAttribPointer(1, 2, GL_FLOAT, false, 9 * 4, 3 * 4);
    glVertexAttribPointer(2, 4, GL_FLOAT, false, 9 * 4, 5 * 4);
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, this.ib);
    glBindVertexArray(0);
    
    Destroyable.registerDestroyable(this, this.va);
  }
  
  public void destroy() {
    GL15.glDeleteBuffers(this.vb);
    GL15.glDeleteBuffers(this.ib);
    GL15.glDeleteBuffers(this.va);
    
    Destroyable.unregisterDestroyable(this, this.va);
  }
  
  public void draw() {
    this.shader.use();
    this.shader.model.set(this.transform);
    glActiveTexture(GL_TEXTURE0);
    this.texture.use();
    glActiveTexture(GL_TEXTURE1);
    this.texture2.use();
    glBindVertexArray(this.va);
    glDrawElements(GL_TRIANGLES, this.indices_count, GL_UNSIGNED_BYTE, 0);
    glBindVertexArray(0);
  }
}
