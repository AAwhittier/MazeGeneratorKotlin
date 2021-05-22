# Overview
Welcome to my Kotlin maze generator. I created this project as a learning experience
to learn depth first traversal and the kotlin language.

This maze generator uses depth first traversal to walk through the maze grid until it
reaches a point where it has visited all nearby cells. It will then backtrack using
a stack until it finds a new valid path, until all cells have been visited. The cell
walls will be removed as they are visited, thereby creating a maze.

[Software Demo Video](https://youtu.be/kncwkPHuK7Q)

# Development Environment
This project was developed in IntelliJ Idea using the kotlin plugin.

# Useful Websites
* [Kotlin Documentation](https://kotlinlang.org/docs/home.html)
* [Wikipedia Maze Algorithms](https://en.wikipedia.org/wiki/Maze_generation_algorithm)

# Future Work
* Add start and end points to maze.
* Save maze to an image file.
* Add graphical UI.