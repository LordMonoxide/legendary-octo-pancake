#version 330

uniform sampler2D tex;
uniform sampler2D tiles;

in vec2 tex_coord;

smooth in vec4 frag_colour;

out vec4 colour;

void main() {
  vec2 mapped = round(texture2D(tiles, tex_coord).rg * 32 * 16) / 16;
  colour = texture2D(tex, mapped + mod(tex_coord * 512f, 32f) / 256f) * frag_colour;
}
