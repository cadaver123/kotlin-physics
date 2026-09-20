#version 330

uniform vec2 uResolution;

in vec2 vLocal;
out vec4 fragColor;

void main() {

    float d = length(vLocal) - 1.0;
    float w = length(vec2(dFdx(d), dFdy(d)));
    float a = 1.0 - smoothstep(.5 * -w, .5 * w, d);


    fragColor = vec4(.0, .0, .0, a);
}