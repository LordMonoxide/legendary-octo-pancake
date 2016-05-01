package gfx.shaders;

public class TerrainShader extends Shader {
  public final Uniform texture;
  public final Uniform tiles;
  public final UniformBlock region;
  
  public TerrainShader() {
    super("terrain.vsh", "terrain.fsh"); //$NON-NLS-1$ //$NON-NLS-2$
    
    this.texture = this.bindUniform("tex"); //$NON-NLS-1$
    this.tiles   = this.bindUniform("tiles"); //$NON-NLS-1$
    this.region  = this.bindUniformBlock("region"); //$NON-NLS-1$
    
    this.use();
    this.texture.set(0);
    this.tiles.set(1);
  }
}
