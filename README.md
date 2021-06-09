# CounterStrike2D
A 2D local multiplayer game developed in Java for school.
It's playable, but not very good.

# Design
**It's pretty terrible**
The codebase was split into 3 main parts:
- Client - resposible for rendering and game logic(should really be moved to the server to prevent cheating)
- Server - pretty much a dumb relay between the clients with very little validation
- MapEditor - map editor for easy creation of maps
