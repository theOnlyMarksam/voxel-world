#version 450 core

uniform sampler2DArray tex;

in vec3 interpolatedUv;

void main() {
    vec3 color = texture(tex, interpolatedUv).rgb;
    gl_FragColor = vec4(color, 1.0);
}
