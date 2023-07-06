# Physics in Kotlin
A simple physics simulator with ECS architecture (entity component system).

## Keywords
kotlin simulation 2d physics ecs entity component system

## Entity & Components
An entity is basically a composition of components.
  * Shape (Interface) with a color
    * Circle (with a radius)
  * Collider of "merge" or "elastic" type and with a weight - used by collision system. If two objects has the same collider type they are either merged or bounce during a a collision
  * Destructor - if other objects collide with this one they disappear
  * GravitySource with a strength - pull other objects which have Velocity component available
  * Velocity - used by position system to change... the positon of an object
  * Position - the position of an object

## Systems
A system changes the state of an entity. Implemented systems:
  * CollisionSystem (merges or make object bounce from each other after the collision)
  * DestructionSystem (remove objects)
  * GravitationalSystem (pull objects together)
  * PositionSystem (calculates posiotion for moving objects)

![image](https://github.com/cadaver123/kotlin-physics/assets/1839663/fb19f1a4-209a-4ff7-b60e-cd7f178fa024)
