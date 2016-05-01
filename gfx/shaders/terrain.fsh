#version 330
#extension GL_ARB_arrays_of_arrays : require

uniform sampler2D tex;
uniform sampler2D tiles;

smooth in vec2 tex_coord;
smooth in vec4 frag_colour;

layout(std140) uniform region {
  int tile_size;
  int tile_count;
  int region_count;
};

out vec4 colour;

void main() {
  int types[3][3];
  
  //float c = texture2D(tiles, tex_coord).r;
  int x = int(tex_coord.x * region_count);
  int y = int(tex_coord.y * region_count);
  
  for(int x1 = -1; x1 <= 1; x1++) {
    for(int y1 = -1; y1 <= 1; y1++) {
      float c = texelFetch(tiles, ivec2(x + x1, y + y1), 0).r;
      types[x1 + 1][y1 + 1] = int(round(c * 255));
    }
  }
  
  vec2 offset = mod(tex_coord * region_count, 1);
  vec2 mapped;
  
  switch(types[1][1]) {
    case 1: // Grass
      mapped = vec2(0, 0);
      
      // Grass to water right
      if(types[2][1] == 2) {
        if(offset.x >= 0.5f) {
          mapped = vec2(1.5, 5);
        }
      }
    break;
    
    case 2: // Water
      mapped = vec2(1, 5);
      
      // Water to grass left
      if(types[0][1] == 1) {
        if(offset.x < 0.5f) {
          mapped = vec2(2.5, 5);
        }
      }
    break;
    
    default:
      discard;
  }
  
  //vec2 mapped = round(texture2D(tiles, tex_coord).rg * tile_size * region_count) / region_count;
  colour = texture2D(tex, (mapped + offset) / tile_count) * frag_colour;
}
