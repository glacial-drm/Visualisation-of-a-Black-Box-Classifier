#version 330 core

layout (location = 0) in vec3 aPos;

uniform mat4 transformMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;


void main() {
    gl_Position = viewMatrix * projectionMatrix * transformMatrix * vec4(aPos.x, aPos.y, aPos.z, 1.0);
}