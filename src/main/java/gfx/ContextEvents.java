package gfx;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

public class ContextEvents {
  ContextEvents() { }
  
  private Deque<StopEvent> stop = new ConcurrentLinkedDeque<>();
  private Deque<DrawEvent> draw = new ConcurrentLinkedDeque<>();
  
  public ContextEvents onStop(StopEvent e) { this.stop.add(e); return this; }
  public ContextEvents onDraw(DrawEvent e) { this.draw.add(e); return this; }
  
  void onStop() {
    for(StopEvent e : this.stop) { e.run(); }
  }
  
  void onDraw(float delta) {
    for(DrawEvent e : this.draw) { e.run(delta); }
  }
  
  public interface StopEvent { public void run(); }
  public interface DrawEvent { public void run(float delta); }
}
