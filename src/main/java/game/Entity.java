package game;

import org.joml.Vector3f;

import gfx.Drawable;

public class Entity {
  public final Vector3f pos = new Vector3f();
  public final Drawable drawable;
  
  public Entity(Drawable drawable) {
    this.drawable = drawable;
  }
}
