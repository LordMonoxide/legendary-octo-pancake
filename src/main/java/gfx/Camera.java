package gfx;

import org.joml.Vector3f;

public class Camera {
  public final Vector3f pos = new Vector3f();
  public Vector3f target = new Vector3f();
  
  public Camera(float phi, float theta, float radius) {
    this.pos.set(phi, theta, radius);
  }
  
  public Vector3f euclidean() {
    float phi   = this.pos.x;
    float theta = this.pos.y + 90;
    
    float sin_theta = (float)Math.sin(theta);
    float cos_theta = (float)Math.cos(theta);
    float cos_phi   = (float)Math.cos(phi);
    float sin_phi   = (float)Math.sin(phi);
    
    Vector3f dir_to_camera = new Vector3f(sin_theta * cos_phi, cos_theta, sin_theta * sin_phi);
    dir_to_camera.mul(this.pos.z).add(this.target);
    
    return dir_to_camera;
  }
}
