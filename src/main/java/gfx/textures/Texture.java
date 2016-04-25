package gfx.textures;

import static org.lwjgl.opengl.GL11.*;

import java.nio.ByteBuffer;

import org.joml.Vector2f;

public class Texture {
  public final int id;
  public final Vector2f size = new Vector2f();
  
  public Texture(int w, int h, ByteBuffer data) {
    this.id = glGenTextures();
    this.size.set(w, h);
    
    this.use();
    
    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, w, h, 0, GL_RGBA, GL_UNSIGNED_BYTE, data);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
  }
  
  public void use() {
    glBindTexture(GL_TEXTURE_2D, this.id);
  }
  
  public void destroy() {
    glDeleteTextures(this.id);
  }
}
