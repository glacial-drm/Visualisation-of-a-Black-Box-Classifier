#version 330 core

layout (location = 0) in vec3 aPos;
layout (location = 1) in vec3 aColour;
layout (location = 2) in vec4 aMVP1;
layout (location = 3) in vec4 aMVP2;
layout (location = 4) in vec4 aMVP3;
layout (location = 5) in vec4 aMVP4;

uniform mat4 transformMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

out vec3 pos;
out vec3 colour;

void main() {
    colour = aColour;
    pos = aPos;
    mat4 aMVP = mat4(aMVP1, aMVP2, aMVP3, aMVP4);

    gl_Position = aMVP * vec4(aPos, 1.0);
}
