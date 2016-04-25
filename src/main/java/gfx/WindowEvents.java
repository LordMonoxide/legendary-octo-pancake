package gfx;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

import org.lwjgl.PointerBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.*;

public final class WindowEvents {
  WindowEvents() { }
  
  private Deque<MoveEvent>         move = new ConcurrentLinkedDeque<>();
  private Deque<ResizeEvent>       resize = new ConcurrentLinkedDeque<>();
  private Deque<CloseEvent>        close = new ConcurrentLinkedDeque<>();
  private Deque<RefreshEvent>      refresh = new ConcurrentLinkedDeque<>();
  private Deque<FocusEvent>        focus = new ConcurrentLinkedDeque<>();
  private Deque<FocusEvent>        unfocus = new ConcurrentLinkedDeque<>();
  private Deque<IconifyEvent>      iconify = new ConcurrentLinkedDeque<>();
  private Deque<IconifyEvent>      uniconify = new ConcurrentLinkedDeque<>();
  private Deque<KeyDownEvent>      key_down = new ConcurrentLinkedDeque<>();
  private Deque<KeyUpEvent>        key_up = new ConcurrentLinkedDeque<>();
  private Deque<CharacterEvent>    character = new ConcurrentLinkedDeque<>();
  private Deque<MouseButtonEvent>  mouse_down = new ConcurrentLinkedDeque<>();
  private Deque<MouseButtonEvent>  mouse_up = new ConcurrentLinkedDeque<>();
  private Deque<MouseMoveEvent>    mouse_move = new ConcurrentLinkedDeque<>();
  private Deque<MouseHoverEvent>   mouse_enter = new ConcurrentLinkedDeque<>();
  private Deque<MouseHoverEvent>   mouse_leave = new ConcurrentLinkedDeque<>();
  private Deque<MouseScrollEvent>  mouse_scroll = new ConcurrentLinkedDeque<>();
  private Deque<BufferResizeEvent> buffer_resize = new ConcurrentLinkedDeque<>();
  private Deque<DropEvent>         drop = new ConcurrentLinkedDeque<>();
  
  public WindowEvents onMove(MoveEvent e) { this.move.add(e); return this; }
  public WindowEvents onResize(ResizeEvent e) { this.resize.add(e); return this; }
  public WindowEvents onClose(CloseEvent e) { this.close.add(e); return this; }
  public WindowEvents onRefresh(RefreshEvent e) { this.refresh.add(e); return this; }
  public WindowEvents onFocus(FocusEvent e) { this.focus.add(e); return this; }
  public WindowEvents onUnFocus(FocusEvent e) { this.unfocus.add(e); return this; }
  public WindowEvents onIconify(IconifyEvent e) { this.iconify.add(e); return this; }
  public WindowEvents onUniconify(IconifyEvent e) { this.uniconify.add(e); return this; }
  public WindowEvents onKeyDown(KeyDownEvent e) { this.key_down.add(e); return this; }
  public WindowEvents onKeyUp(KeyUpEvent e) { this.key_up.add(e); return this; }
  public WindowEvents onCharacter(CharacterEvent e) { this.character.add(e); return this; }
  public WindowEvents onMouseDown(MouseButtonEvent e) { this.mouse_down.add(e); return this; }
  public WindowEvents onMouseUp(MouseButtonEvent e) { this.mouse_up.add(e); return this; }
  public WindowEvents onMouseMove(MouseMoveEvent e) { this.mouse_move.add(e); return this; }
  public WindowEvents onMouseEnter(MouseHoverEvent e) { this.mouse_enter.add(e); return this; }
  public WindowEvents onMouseLeave(MouseHoverEvent e) { this.mouse_leave.add(e); return this; }
  public WindowEvents onMouseScroll(MouseScrollEvent e) { this.mouse_scroll.add(e); return this; }
  public WindowEvents onBufferResize(BufferResizeEvent e) { this.buffer_resize.add(e); return this; }
  public WindowEvents onDrop(DropEvent e) { this.drop.add(e); return this; }
  
  void onMove(int x, int y) {
    for(MoveEvent e : this.move) { e.run(x, y); }
  }
  
  void onResize(int w, int h) {
    for(ResizeEvent e : this.resize) { e.run(w, h); }
  }
  
  void onClose() {
    for(CloseEvent e : this.close) { e.run(); }
  }
  
  void onRefresh() {
    for(RefreshEvent e : this.refresh) { e.run(); }
  }
  
  void onFocus(int focused) {
    if(focused == GL_FALSE) {
      for(FocusEvent e : this.unfocus) { e.run(); }
    } else {
      for(FocusEvent e : this.focus) { e.run(); }
    }
  }
  
  void onIconify(int iconified) {
    if(iconified == GL_FALSE) {
      for(IconifyEvent e : this.uniconify) { e.run(); }
    } else {
      for(IconifyEvent e : this.iconify) { e.run(); }
    }
  }
  
  void onKeyPress(int key, int scancode, int action, int mods) {
    switch(action) {
      case GLFW_PRESS:
        for(KeyDownEvent e : this.key_down) { e.run(key, mods, false, scancode); }
      break;
      
      case GLFW_REPEAT:
        for(KeyDownEvent e : this.key_down) { e.run(key, mods, true, scancode); }
      break;
      
      case GLFW_RELEASE:
        for(KeyUpEvent e : this.key_up) { e.run(key, mods, scancode); }
      break;
    }
  }
  
  void onCharacter(int codepoint, int mods) {
    for(CharacterEvent e : this.character) { e.run(codepoint, mods); }
  }
  
  void onMouseButton(int button, int action, int mods) {
    switch(action) {
      case GLFW_PRESS:
        for(MouseButtonEvent e : this.mouse_down) { e.run(button, mods); }
      break;
      
      case GLFW_RELEASE:
        for(MouseButtonEvent e : this.mouse_up) { e.run(button, mods); }
      break;
    }
  }
  
  void onMouseMove(double x, double y) {
    for(MouseMoveEvent e : this.mouse_move) { e.run(x, y); }
  }
  
  void onMouseHover(int entered) {
    switch(entered) {
      case GL_TRUE:
        for(MouseHoverEvent e : this.mouse_enter) { e.run(); }
      break;
      
      case GL_FALSE:
        for(MouseHoverEvent e : this.mouse_leave) { e.run(); }
      break;
    }
  }
  
  void onMouseScroll(double xoffset, double yoffset) {
    for(MouseScrollEvent e : this.mouse_scroll) { e.run(xoffset, yoffset); }
  }
  
  void onBufferResize(int w, int h) {
    for(BufferResizeEvent e : this.buffer_resize) { e.run(w, h); }
  }
  
  void onDrop(int count, long names) {
    PointerBuffer nameBuffer = memPointerBuffer(names, count);
    
    String[] name = new String[count];
    for(int i = 0; i < count; i++) {
      name[i] = memDecodeUTF8(memByteBufferNT1(nameBuffer.get(i)));
    }
    
    for(DropEvent e : this.drop) { e.run(name); } 
  }
  
  public interface MoveEvent         { public void run(int x, int y); }
  public interface ResizeEvent       { public void run(int w, int h); }
  public interface CloseEvent        { public void run(); }
  public interface RefreshEvent      { public void run(); }
  public interface FocusEvent        { public void run(); }
  public interface IconifyEvent      { public void run(); }
  public interface KeyDownEvent      { public void run(int key, int mods, boolean repeat, int code); }
  public interface KeyUpEvent        { public void run(int key, int mods, int code); }
  public interface CharacterEvent    { public void run(int codepoint, int mods); }
  public interface MouseButtonEvent  { public void run(int button, int mods); }
  public interface MouseMoveEvent    { public void run(double x, double y); }
  public interface MouseHoverEvent   { public void run(); }
  public interface MouseScrollEvent  { public void run(double x, double y); }
  public interface BufferResizeEvent { public void run(int w, int h); }
  public interface DropEvent         { public void run(String[] names); }
}
