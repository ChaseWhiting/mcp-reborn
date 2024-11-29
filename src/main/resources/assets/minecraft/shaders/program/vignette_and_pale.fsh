#version 110

uniform sampler2D DiffuseSampler;

varying vec2 texCoord;

void main() {
    vec4 color = texture2D(DiffuseSampler, texCoord);

    // Apply vignette effect
    vec2 center = vec2(0.5, 0.5);
    float dist = distance(texCoord, center);
    float vignette = smoothstep(0.4, 0.8, dist);
    color.rgb *= (1.0 - vignette * 0.6);

    // Apply pale (desaturation) effect
    float gray = dot(color.rgb, vec3(0.299, 0.587, 0.114));
    color.rgb = mix(vec3(gray), color.rgb, 0.5);

    // Output final color
    gl_FragColor = color;
}
