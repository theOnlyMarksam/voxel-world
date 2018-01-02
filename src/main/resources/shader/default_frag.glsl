#version 450 core

uniform sampler2D tex;

in vec2 interpolatedUv;

void main() {
    vec3 color = texture2D(tex, interpolatedUv).rgb;
    gl_FragColor = vec4(color, 1.0);
}
