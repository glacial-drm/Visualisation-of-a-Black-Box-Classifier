#version 330 core

layout (location = 0) in vec3 aPos;
layout (location = 1) in vec3 aColour;

uniform mat4 transformMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

out vec3 pos;
out vec3 colour;

void main() {
    colour = aColour;
    pos = aPos;
    gl_Position = projectionMatrix * viewMatrix * transformMatrix * vec4(aPos, 1.0);
}
