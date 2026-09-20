#version 330

layout(location = 0) in vec2 aPos;
layout(location = 1) in vec2 aCenter;
layout(location = 2) in float aRadius;

out vec2 vLocal;

uniform float uMinX;
uniform float uMaxX;
uniform float uMinY;
uniform float uMaxY;
void main() {
    mat4 ortoProj = mat4(vec4(2.0 / (uMaxX - uMinX), .0, .0, .0), vec4(.0, 2.0 / (uMaxY - uMinY), .0, .0), vec4(.0, .0, 1.0, .0), vec4(-(uMaxX + uMinX)/(uMaxX - uMinX), -(uMaxY + uMinY)/(uMaxY - uMinY), .0, 1.0));

    vLocal = aPos;
    gl_Position = ortoProj * vec4(aRadius*aPos + aCenter, .0 ,1.0);
}