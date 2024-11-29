#version 110

uniform sampler2D DiffuseSampler;
uniform vec3 RadialConvergeX;
uniform vec3 RadialConvergeY;

varying vec2 texCoord;

void main() {
    // Radial distortion with subtle convergence
    vec3 CoordX = texCoord.x * RadialConvergeX;
    vec3 CoordY = texCoord.y * RadialConvergeY;

    // Sample color from the diffuse texture with a slight distortion
    float RedValue   = texture2D(DiffuseSampler, vec2(CoordX.x, CoordY.x)).r;
    float GreenValue = texture2D(DiffuseSampler, vec2(CoordX.y, CoordY.y)).g;
    float BlueValue  = texture2D(DiffuseSampler, vec2(CoordX.z, CoordY.z)).b;

    // Output the final color
    gl_FragColor = vec4(RedValue, GreenValue, BlueValue, 1.0);
}
