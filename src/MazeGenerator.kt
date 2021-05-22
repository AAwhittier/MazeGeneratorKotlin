// Cell: Stores flags for the cell walls and visitation.
class Cell2 {
    // Was the cell visited already.
    var visited: Boolean = false

    // Open / closed cell boundaries.
    var northBound: Boolean = true
    var southBound: Boolean = true
    var eastBound: Boolean = true
    var westBound: Boolean = true
}

// Maze: Holds the rules for creating and displaying a maze.
class Maze2(height_param: Int, width_param: Int) {
    // Green - Maze start - Blue - Maze Path - Red - Maze End -- future
    val RED: String? = "\u001B[31m"
    val GREEN = "\u001B[32m"
    val BLUE = "\u001b[0;94m"

    // Maze height x width
    private val height = height_param
    private val width = width_param

    // Finished generating when visitedCount = sizeOfMaze.
    private val sizeOfMaze = height * width
    private var visitedCount = 0
    private val startRow = (0 until height).random()
    private val startColumn = (0 until width).random()

    // The maze built from cells.
    var maze: MutableList<MutableList<Cell2>> = mutableListOf()

    // Stack to track our pathway through the maze until visitedCount = sizeOfMaze
    private var path = ArrayDeque<Pair<Int, Int>>()

    init {
        // Fill the maze with indexed cells.
        for (i in 0 until width) {
            var cells: MutableList<Cell2> = mutableListOf()
            for (j in 0 until height) {
                cells.add(Cell2())
            }
            maze.add(cells)
        }

        // Initialize the maze to a staring point.
        path.add(Pair(startRow, startColumn))
        visitedCount += 1
        maze[startRow][startColumn].visited = true
    }

    // Create the maze.
    fun createMaze() {
        while (visitedCount < sizeOfMaze) {
            val row = path.last().first
            val column = path.last().second
            val moveTo = findUnvisitedCells(row, column)

            // Move to a new unvisited cell, remove walls between current cell and the target cell.
            // Add the new location to the stack.
            if (moveTo == "North") {
                maze[row][column - 1].visited = true
                maze[row][column - 1].southBound = false
                maze[row][column].northBound = false
                path.add(Pair(row, column - 1))
            } else if (moveTo == "South") {
                maze[row][column + 1].visited = true
                maze[row][column].southBound = false
                maze[row][column + 1].northBound = false
                path.add(Pair(row, column + 1))
            } else if (moveTo == "East") {
                maze[row + 1][column].visited = true
                maze[row + 1][column].westBound = false
                maze[row][column].eastBound = false
                path.add(Pair(row + 1, column))
            } else if (moveTo == "West") {
                maze[row - 1][column].visited = true
                maze[row - 1][column].eastBound = false
                maze[row][column].westBound = false
                path.add(Pair(row - 1, column))
            }
            // Backtrack until we find a cell with an open direction. Remove from stack.
            else {
                path.removeLast()
            }
        }
    }


    private fun findUnvisitedCells(row: Int, column: Int): String {
        // Map of nearby cells to decide where to go next.
        var direction: String = "None"
        var unvisited = mutableMapOf("North" to false, "South" to false, "East" to false, "West" to false)

        // North - Only move to if y is in bounds. Follow this rule to stay in bounds.
        if (column > 0 && !maze[row][column - 1].visited) {
            unvisited["North"] = true
        }
        if (row < width - 1 && !maze[row + 1][column].visited) {
            unvisited["East"] = true
        }
        if (column < height - 1 && !maze[row][column + 1].visited) {
            unvisited["South"] = true
        }
        if (row > 0 && !maze[row - 1][column].visited) {
            unvisited["West"] = true
        }

        // Move to nearby unvisited cell. Shuffle to get a random adjacent cell.
        if (unvisited.values.any()) {
            // Select a random direction to move from unvisited cells.
            val possibleDirections = mutableListOf<String>()
            for (key in unvisited) {
                if (key.value) {
                    possibleDirections.add(key.key)
                }
            }
            // Assign a random direction if one is available.
            // If no direction is available the algorithm will backtrack.
            if (possibleDirections.size > 0) {
                direction = possibleDirections.random()
                visitedCount += 1
            }
        }
        return direction
    }

    fun drawMaze() {
        for (i in 0 until width) {
            println()
            for (j in 0 until height) {
                val north = maze[j][i].northBound
                val east = maze[j][i].eastBound
                val south = maze[j][i].southBound
                val west = maze[j][i].westBound

                // Change color of maze start cell and maze end cell. -- Future
                print(BLUE)

                // Place unicode blocks based on the cell walls.
                if (south && !east && !north && !west) {
                    print("\u2569")        // \u2569 - ╩
                } else if (!south && !east && north && !west) {
                    print("\u2566")        // \u2566 - ╦
                } else if (!south && east && !north && !west) {
                    print("\u2563")        // \u2563 - ╣
                } else if (!south && !east && !north && west) {
                    print("\u2560")        // \u2560 - ╠
                } else if (south && east && !north && west) {
                    print("\u2568")        // \u2568 - ╨
                } else if (!south && east && north && west) {
                    print("\u2565")        // \u2565 - ╥
                } else if (south && east && north && !west) {
                    print("\u2561")        // \u2561 - ╡
                } else if (south && !east && north && west) {
                    print("\u255e")        // \u255e - ╞
                } else if (!south && east && north && !west) {
                    print("\u2557")        // \u2557 - ╗
                } else if (south && !east && !north && west) {
                    print("\u255a")        // \u255a - ╚
                } else if (south && east && !north && !west) {
                    print("\u255d")        // \u255d - ╝
                } else if (!south && !east && north && west) {
                    print("\u2554")        // \u2554 - ╔
                } else if (!south && east && !north && west) {
                    print("\u2551")        // \u2551 - ║
                } else if (south && !east && north && !west) {
                    print("\u2550")        // \u2550 - ═
                } else {
                    print("\u256c")        // \u256c - ╬
                }
            }
        }
    }
}

fun main() {
    println("Maze Generator")
    print("Enter maze size: ")
    val size = readLine()
    println()

    var myMaze = Maze2(Integer.valueOf(size), Integer.valueOf(size));
    myMaze.createMaze()
    myMaze.drawMaze()
}
