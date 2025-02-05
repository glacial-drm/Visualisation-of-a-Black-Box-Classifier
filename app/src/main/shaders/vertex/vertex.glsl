#version 330 core

layout(location = 0) in vec3 inPos;
layout(location = 1) in vec3 inColour;
layout(location = 2) in vec4 inMVP1;
layout(location = 3) in vec4 inMVP2;
layout(location = 4) in vec4 inMVP3;
layout(location = 5) in vec4 inMVP4;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

out vec3 pos;
out vec3 colour;

void main() {
    colour = inColour;
    pos = inPos;
    mat4 model = mat4(inMVP1, inMVP2, inMVP3, inMVP4);

    gl_Position = projectionMatrix * viewMatrix * model * vec4(inPos, 1.0);
}
