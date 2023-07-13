# Physics in Kotlin
A simple physics simulation with ECS architecture (entity component system).

## Keywords
kotlin simulation 2d physics ecs entity component system

## Entity & Components
An entity is basically a composition of components.
  * Shape (Interface) with a color
    * Circle (with a radius)
  * Collider of "merge" or "elastic" type and with a weight - used by collision system. If two objects has the same collider type they are either merged or bounce during a a collision
  * Destructor - if other objects collide with this one they will disappear
  * GravitySource with a strength - pulls other objects (if they have Velocity) towards this one
  * Velocity - used by position system to change... the positon of the object
  * Position - position of the object

## Systems
A system changes the state of an entity. Implemented systems:
  * CollisionSystem (merges or makes objects bounce from each other after the collision)
  * DestructionSystem (remove objects)
  * GravitationalSystem (pull objects together)
  * PositionSystem (calculates position for moving objects)

![image](https://github.com/cadaver123/kotlin-physics/assets/1839663/fb19f1a4-209a-4ff7-b60e-cd7f178fa024)
