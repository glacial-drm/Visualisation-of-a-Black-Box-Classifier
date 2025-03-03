#version 330 core

out vec4 FragColor;
in vec3 pos;
in vec3 colour;

float radius = 1;
float smoothing = radius * 0.1;

void main() {
    vec2 center = vec2(0);

    vec2 d = pos.xy - center;
    float alpha = 1 - smoothstep(
        radius - smoothing,
        radius + smoothing,
        dot(d, d) * 5
    );

    FragColor = vec4(colour, alpha);
}
