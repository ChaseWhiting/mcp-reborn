#version 120

attribute vec3 Position;
attribute vec4 Color;
attribute vec2 UV0;

varying vec4 vertexColor;
varying vec2 texCoord0;

void main() {
    gl_Position = gl_ModelViewProjectionMatrix * vec4(Position, 1.0);
    vertexColor = Color;
    texCoord0 = UV0;
}
