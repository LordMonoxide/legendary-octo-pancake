package gfx;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.opengl.GL;

public class Context {
  public final ContextEvents events = new ContextEvents();
  
  private final Window window;
  private boolean running;
  
  public Context(Window window) {
    this.window = window;
    
    this.window.makeCurrent();
    GL.createCapabilities();
    
    glfwSwapInterval(1);
    
    glEnable(GL_BLEND);
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    
    glEnable(GL_DEPTH_TEST);
    glDepthMask(true);
    glDepthFunc(GL_LEQUAL);
    glDepthRange(0.0f, 1.0f);
    
    glEnable(GL_CULL_FACE);
    glCullFace(GL_BACK);
    glFrontFace(GL_CW);
    
    this.window.events.onResize((w, h) -> {
      glViewport(0, 0, w, h);
    });
  }
  
  public void start() {
    this.running = true;
    
    long last_draw = System.nanoTime();
    float desired_time = 1000000000f / 60f;
    float delta = 1;
    
    while(this.running) {
      glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
      
      delta = (System.nanoTime() - last_draw) / desired_time;
      this.events.onDraw(delta);
      last_draw = System.nanoTime();
      
      this.window.swap();
      glfwPollEvents();
    }
    
    this.events.onStop();
  }
  
  public void stop() {
    this.running = false;
  }
}
