#version 330

layout(location = 0) in vec4 position;
layout(location = 1) in vec2 uv;
layout(location = 2) in vec4 colour;

smooth out vec2 tex_coord;
smooth out vec4 frag_colour;

layout(std140) uniform transforms {
  mat4 camera;
  mat4 projection;
};

uniform mat4 model;

void main() {
  gl_Position = projection * camera * model * position;
  frag_colour = colour;
  tex_coord = uv;
}
