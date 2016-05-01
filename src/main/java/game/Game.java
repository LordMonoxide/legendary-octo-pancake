package game;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL32.*;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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
import gfx.shaders.UniformBuffer;
import gfx.textures.Texture;
import gfx.textures.TextureLoader;

public class Game {
  private static final float Z_NEAR =    1f;
  private static final float Z_FAR  = 1000f;
  
  private final Window  window;
  private final Context context;
  
  private final ScheduledThreadPoolExecutor logic = new ScheduledThreadPoolExecutor(4);
  
  private long frame_time = 0;
  private long frame_time_avg = 0;
  private long last_time = 0;
  
  private final Camera camera = new Camera((float)(Math.PI / 2), -(float)(2.03540432), 15);
  //private final Camera camera = new Camera((float)(Math.PI / 2), -(float)(Math.PI / 4), 15);
  
  private final Matrix4f mat_camera     = new Matrix4f();
  private final Matrix4f mat_projection = new Matrix4f();
  
  private final TerrainShader shader;
  
  private boolean depth_clamp = false;
  
  private UniformBuffer ub_transforms;
  private UniformBuffer ub_region;
  
  public Game() {
    this.logic.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
    
    this.window = new Window("Butts", 1280, 720); //$NON-NLS-1$
    this.context = new Context(this.window);
    
    this.window.events.onClose(() -> this.context.stop());
    this.window.events.onResize((w, h) -> this.updateProjection(w, h));
    
    this.ub_transforms = new UniformBuffer(4 * 4 * 4 * 2);
    this.ub_region     = new UniformBuffer(4 * 3);
    
    this.shader = new TerrainShader();
    this.shader.transforms.bind(this.ub_transforms);
    this.shader.region.bind(this.ub_region);
    
    this.updateCamera();
    this.updateProjection(this.window.getWidth(), this.window.getHeight());
    
    @SuppressWarnings("null")
    @NonNull Path texture_dir = Paths.get("gfx", "textures"); //$NON-NLS-1$ //$NON-NLS-2$
    TextureLoader textures = new TextureLoader(texture_dir);
    Texture texture = textures.get("tiles.png"); //$NON-NLS-1$
    
    int i = 0;
    for(int y = 0; y < region_count; y++) {
      for(int x = 0; x < region_count; x++) {
        tile_data[i]     = (byte)(x % tile_count);
        tile_data[i + 1] = (byte)(y % tile_count);
        i += 4;
      }
    }
    
    IntBuffer region = Buffers.of(new int[] {tile_size, tile_count, region_count});
    this.ub_region.set(region);
    
    ByteBuffer tile_buffer = Buffers.of(tile_data);
    Texture tile_data_tex = new Texture(region_count, region_count, tile_buffer);
    
    Drawable map  = new Drawable(this.shader, texture, tile_data_tex, vert_map, ind_map);
    Drawable d_me = new Drawable(this.shader, texture, tile_data_tex, vert_me,  ind_me);
    
    Entity me = new Entity(d_me);
    this.camera.target = me.pos;
    
    this.last_time = System.nanoTime();
    this.frame_time_avg = 1000000000 / 60;
    
    this.context.events.onDraw(delta -> {
      map.draw();
      me.drawable.draw();
      
      this.frame_time = System.nanoTime() - this.last_time;
      this.frame_time_avg = (this.frame_time_avg + this.frame_time) / 2;
      this.last_time = System.nanoTime();
    });
    
    this.logic.scheduleAtFixedRate(() -> {
      this.window.setTitle("Butts - " + 1 / (this.frame_time_avg / 1000000000f) + " FPS"); //$NON-NLS-1$ //$NON-NLS-2$
    }, 0, 1, TimeUnit.SECONDS);
    
    this.context.events.onStop(() -> {
      this.logic.shutdown();
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
  
  private static int region_count = 16;
  private static int tile_size    = 32;
  private static int tile_count   =  8;
  
  private static float[] vert_map = {
     region_count / 2, 0, -region_count / 2,   1, 0,   1, 1, 1, 1,
     region_count / 2, 0,  region_count / 2,   1, 1,   1, 1, 1, 1,
    -region_count / 2, 0,  region_count / 2,   0, 1,   1, 1, 1, 1,
    -region_count / 2, 0, -region_count / 2,   0, 0,   1, 1, 1, 1
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
  
  private static byte[] tile_data = new byte[region_count * region_count * 4];
}
