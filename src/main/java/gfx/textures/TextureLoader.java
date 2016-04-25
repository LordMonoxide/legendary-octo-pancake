package gfx.textures;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

import de.matthiasmann.twl.utils.PNGDecoder;

public class TextureLoader {
  private final Path textures;
  
  public TextureLoader(Path textures) {
    this.textures = textures;
  }
  
  public Texture get(String file) {
    Path f = this.textures.resolve(file);
    
    try(InputStream in = Files.newInputStream(f)) {
      PNGDecoder png = new PNGDecoder(in);
      
      int w = png.getWidth();
      int h = png.getHeight();
      
      ByteBuffer data = ByteBuffer.allocateDirect(4 * w * h);
      png.decode(data, w * 4, PNGDecoder.Format.RGBA);
      data.flip();
      
      return new Texture(w, h, data);
    } catch(@SuppressWarnings("unused") NoSuchFileException e) {
      System.err.println("Couldn't find texture \"" + file + '\"'); //$NON-NLS-1$
    } catch(IOException e) {
      System.err.println("Error loading texture \"" + file + '\"'); //$NON-NLS-1$
      e.printStackTrace();
    }

    return new TextureNotFound();
  }
}
