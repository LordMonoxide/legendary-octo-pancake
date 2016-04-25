package gfx.textures;

import java.nio.ByteBuffer;

public class TextureNotFound extends Texture {
  @SuppressWarnings("all")
  public TextureNotFound() {
    super(0, 0, ByteBuffer.allocateDirect(0));
  }
  
  @Override public void use() {
    
  }
  
  @Override public void destroy() {
    
  }
}
