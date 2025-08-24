#version 120

varying vec4 vertexColor;
varying vec2 texCoord0;

uniform sampler2D Sampler0;

void main() {
    vec4 texColor = texture2D(Sampler0, texCoord0);
    gl_FragColor = texColor * vertexColor * 2.0; // multiply for extra brightness ("glow")
}
