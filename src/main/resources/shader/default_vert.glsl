#version 450 core

in vec3 position;
in vec3 uv;

out vec3 interpolatedUv;

uniform mat4 projection;
uniform mat4 view;

void main() {
    gl_Position = projection * view * vec4(position, 1.0);

    interpolatedUv = uv;
}
