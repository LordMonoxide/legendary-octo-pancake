package game;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL32.*;

import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.jdt.annotation.NonNull;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import gfx.Buffers;
import gfx.Camera;
import gfx.Context;
import gfx.Destroyable;
import gfx.Drawable;
import gfx.Window;
import gfx.shaders.TerrainShader;
import gfx.shaders.Shader;
import gfx.shaders.UniformBuffer;
import gfx.textures.Texture;
import gfx.textures.TextureLoader;

public class Game {
  private static final float Z_NEAR =    1f;
  private static final float Z_FAR  = 1000f;
  
  private final Window  window;
  private final Context context;
  
  private final Camera camera = new Camera((float)(Math.PI / 2), -(float)(2.03540432), 15);
  //private final Camera camera = new Camera((float)(Math.PI / 2), -(float)(Math.PI / 4), 15);
  
  private final Matrix4f mat_camera     = new Matrix4f();
  private final Matrix4f mat_projection = new Matrix4f();
  
  private final Shader shader;
  
  private boolean depth_clamp = false;
  
  private UniformBuffer ub_transforms;
  
  public Game() {
    this.window = new Window("Butts", 1280, 720); //$NON-NLS-1$
    this.context = new Context(this.window);
    
    this.window.events.onClose(() -> this.context.stop());
    this.window.events.onResize((w, h) -> this.updateProjection(w, h));
    
    this.ub_transforms = new UniformBuffer(4 * 4 * 4 * 2);
    
    this.shader = new TerrainShader();
    this.shader.transforms.bind(this.ub_transforms);
    
    this.updateCamera();
    this.updateProjection(1280, 720);
    
    @SuppressWarnings("null")
    @NonNull Path texture_dir = Paths.get("gfx", "textures"); //$NON-NLS-1$ //$NON-NLS-2$
    TextureLoader textures = new TextureLoader(texture_dir);
    Texture texture = textures.get("tiles.png"); //$NON-NLS-1$
    
    /*int i = 0;
    for(byte y = 0; y < size; y++) {
      for(byte x = 0; x < size; x++) {
        tile_data[i]     = (byte)(x % 8);
        tile_data[i + 1] = (byte)(y % 8);
        i += 4;
      }
    }*/
    
    ByteBuffer tile_buffer = Buffers.of(tile_data);
    Texture tile_data_tex = new Texture(size, size, tile_buffer);
    
    Drawable map  = new Drawable(this.shader, texture, tile_data_tex, vert_map, ind_map);
    Drawable d_me = new Drawable(this.shader, texture, tile_data_tex, vert_me,  ind_me);
    
    Entity me = new Entity(d_me);
    this.camera.target = me.pos;
    
    this.context.events.onDraw(delta -> {
      map.draw();
      me.drawable.draw();
    });
    
    this.context.events.onStop(() -> {
      map.destroy();
      d_me.destroy();
      this.shader.destroy();
      this.window.destroy();
    });
    
    this.window.events.onKeyDown((key, mods, repeat, code) -> {
      switch(key) {
        case GLFW_KEY_W:
          me.pos.z -= 0.1f;
        break;
        
        case GLFW_KEY_S:
          me.pos.z += 0.1f;
        break;
        
        case GLFW_KEY_A:
          me.pos.x -= 0.1f;
        break;
        
        case GLFW_KEY_D:
          me.pos.x += 0.1f;
        break;
        
        case GLFW_KEY_SPACE:
          this.depth_clamp = !this.depth_clamp;
          
          if(this.depth_clamp) {
            System.out.println("Depth clamping enabled"); //$NON-NLS-1$
            glEnable(GL_DEPTH_CLAMP);
          } else {
            System.out.println("Depth clamping disabled"); //$NON-NLS-1$
            glDisable(GL_DEPTH_CLAMP);
          }
        break;
      }
      
      me.drawable.transform.setTranslation(me.pos.x, me.pos.y, me.pos.z);
      this.updateCamera();
    });
  }
  
  public void start() {
    this.context.start();
    
    Destroyable.checkForMemoryLeaks();
  }
  
  private void updateCamera() {
    this.mat_camera.setLookAt(this.camera.euclidean(), this.camera.target, new Vector3f(0, 1, 0));
    this.ub_transforms.set(this.mat_camera);
  }
  
  private void updateProjection(int w, int h) {
    this.mat_projection.setPerspective((float)(Math.PI / 3), (float)w / (float)h, Z_NEAR, Z_FAR);
    this.ub_transforms.set(4 * 4 * 4, this.mat_projection);
  }
  
  private static int size = 16;
  
  private static float[] vert_map = {
     size / 2, 0, -size / 2,   1, 0,   1, 1, 1, 1,
     size / 2, 0,  size / 2,   1, 1,   1, 1, 1, 1,
    -size / 2, 0,  size / 2,   0, 1,   1, 1, 1, 1,
    -size / 2, 0, -size / 2,   0, 0,   1, 1, 1, 1
  };
  
  private static byte[] ind_map = {
      0, 1, 2,
      2, 3, 0
  };
  
  private static float[] vert_me = {
    0.5f, 1.5f, 0, 1, 1, 1.0f, 0.0f, 1.0f, 1.0f,
    0.5f,   0,  0, 1, 0, 1.0f, 0.0f, 1.0f, 1.0f,
   -0.5f,   0,  0, 0, 0, 1.0f, 0.0f, 1.0f, 1.0f,
   -0.5f, 1.5f, 0, 0, 1, 1.0f, 0.0f, 1.0f, 1.0f,
  };
  
  private static byte[] ind_me = {
      0, 1, 2,
      2, 3, 0
  };
  
  private static byte[] tile_data = new byte[size * size * 4];
}
