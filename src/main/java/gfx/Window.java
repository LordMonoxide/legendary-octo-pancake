package gfx;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.annotation.Nullable;
import org.lwjgl.BufferUtils;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWCharModsCallback;
import org.lwjgl.glfw.GLFWCursorEnterCallback;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWDropCallback;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;
import org.lwjgl.glfw.GLFWWindowCloseCallback;
import org.lwjgl.glfw.GLFWWindowFocusCallback;
import org.lwjgl.glfw.GLFWWindowIconifyCallback;
import org.lwjgl.glfw.GLFWWindowPosCallback;
import org.lwjgl.glfw.GLFWWindowRefreshCallback;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.system.libffi.Closure;

public class Window {
  @Nullable static GLFWErrorCallback cb_err;
  
  static {
    System.out.println("STARTIN IT UP"); //$NON-NLS-1$
    System.out.println("LWJGL version " + Version.getVersion()); //$NON-NLS-1$
    
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      System.out.println("SHUTTIN IT DOWN"); //$NON-NLS-1$
      
      if(cb_err != null) {
        cb_err.release();
      }
      
      System.out.println("bye now"); //$NON-NLS-1$
    }));
    
    System.out.println("Initialinzing GLFW..."); //$NON-NLS-1$
    if(glfwInit() != GLFW_TRUE) {
      throw new IllegalStateException("Unable to initialize GLFW"); //$NON-NLS-1$
    }
    
    glfwSetErrorCallback(cb_err = GLFWErrorCallback.createPrint(System.err));
  }
  
  private final long ptr;
  
  public final WindowEvents events = new WindowEvents();
  private final List<Closure> callbacks = new ArrayList<>();
  
  public Window(String title, int width, int height) {
    this.ptr = glfwCreateWindow(width, height, title, NULL, NULL);
    this.bindEvents();
    
    System.out.println("Created window " + this.ptr); //$NON-NLS-1$
  }
  
  public void setTitle(String title) {
    glfwSetWindowTitle(this.ptr, title);
  }
  
  public int getWidth() {
    IntBuffer w = BufferUtils.createIntBuffer(1);
    glfwGetFramebufferSize(this.ptr, w, null);
    return w.get(0);
  }
  
  public int getHeight() {
    IntBuffer h = BufferUtils.createIntBuffer(1);
    glfwGetFramebufferSize(this.ptr, null, h);
    return h.get(0);
  }
  
  private void bindEvents() {
    glfwSetWindowPosCallback(this.ptr, addCallback(new GLFWWindowPosCallback() {
      @Override public void invoke(long window, int xpos, int ypos) {
        Window.this.events.onMove(xpos, ypos);
      }
    }));
    
    glfwSetWindowSizeCallback(this.ptr, addCallback(new GLFWWindowSizeCallback() {
      @Override public void invoke(long window, int width, int height) {
        Window.this.events.onResize(width, height);
      }
    }));
    
    glfwSetWindowCloseCallback(this.ptr, addCallback(new GLFWWindowCloseCallback() {
      @Override public void invoke(long window) {
        Window.this.events.onClose();
      }
    }));
    
    glfwSetWindowRefreshCallback(this.ptr, addCallback(new GLFWWindowRefreshCallback() {
      @Override public void invoke(long window) {
        Window.this.events.onRefresh();
      }
    }));
    
    glfwSetWindowFocusCallback(this.ptr, addCallback(new GLFWWindowFocusCallback() {
      @Override public void invoke(long window, int focused) {
        Window.this.events.onFocus(focused);
      }
    }));
    
    glfwSetWindowIconifyCallback(this.ptr, addCallback(new GLFWWindowIconifyCallback() {
      @Override public void invoke(long window, int iconified) {
        Window.this.events.onIconify(iconified);
      }
    }));
    
    glfwSetKeyCallback(this.ptr, addCallback(new GLFWKeyCallback() {
      @Override public void invoke(long window, int key, int scancode, int action, int mods) {
        Window.this.events.onKeyPress(key, scancode, action, mods);
      }
    }));
    
    glfwSetCharModsCallback(this.ptr, addCallback(new GLFWCharModsCallback() {
      @Override public void invoke(long window, int codepoint, int mods) {
        Window.this.events.onCharacter(codepoint, mods);
      }
    }));
    
    glfwSetMouseButtonCallback(this.ptr, addCallback(new GLFWMouseButtonCallback() {
      @Override public void invoke(long window, int button, int action, int mods) {
        Window.this.events.onMouseButton(button, action, mods);
      }
    }));
    
    glfwSetCursorPosCallback(this.ptr, addCallback(new GLFWCursorPosCallback() {
      @Override public void invoke(long window, double xpos, double ypos) {
        Window.this.events.onMouseMove(xpos, ypos);
      }
    }));
    
    glfwSetCursorEnterCallback(this.ptr, addCallback(new GLFWCursorEnterCallback() {
      @Override public void invoke(long window, int entered) {
        Window.this.events.onMouseHover(entered);
      }
    }));
    
    glfwSetScrollCallback(this.ptr, addCallback(new GLFWScrollCallback() {
      @Override public void invoke(long window, double xoffset, double yoffset) {
        Window.this.events.onMouseScroll(xoffset, yoffset);
      }
    }));
    
    glfwSetFramebufferSizeCallback(this.ptr, addCallback(new GLFWFramebufferSizeCallback() {
      @Override public void invoke(long window, int width, int height) {
        Window.this.events.onBufferResize(width, height);
      }
    }));
    
    glfwSetDropCallback(this.ptr, addCallback(new GLFWDropCallback() {
      @Override public void invoke(long window, int count, long names) {
        Window.this.events.onDrop(count, names);
      }
    }));
  }
  
  private <T extends Closure> T addCallback(T callback) {
    this.callbacks.add(callback);
    return callback;
  }
  
  public void close() {
    glfwSetWindowShouldClose(this.ptr, GLFW_TRUE);
  }
  
  public void destroy() {
    System.out.println("Destroying window " + this.ptr); //$NON-NLS-1$
    
    glfwDestroyWindow(this.ptr);
    
    for(Closure callback : this.callbacks) {
      callback.release();
    }
  }
  
  public void makeCurrent() {
    glfwMakeContextCurrent(this.ptr);
  }
  
  public void swap() {
    glfwSwapBuffers(this.ptr);
  }
}
