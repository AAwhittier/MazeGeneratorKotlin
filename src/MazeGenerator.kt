import java.awt.*
import java.awt.image.BufferedImage
import java.awt.image.RenderedImage
import java.io.File
import java.io.IOException
import java.util.*
import javax.imageio.ImageIO
import kotlin.collections.ArrayDeque


// Cell: Stores flags for the cell walls and visitation.
class Cell2 {
    // Was the cell visited already.
    var visited: Boolean = false

    // Open / closed cell boundaries.
    var northBound: Boolean = true
    var southBound: Boolean = true
    var eastBound: Boolean = true
    var westBound: Boolean = true

    // Store drawing symbol once determined.
    var symbol: String = " "
}

// Maze: Holds the rules for creating and displaying a maze.
class Maze2(height_param: Int, width_param: Int) {

    // Maze height x width
    private val height = height_param
    private val width = width_param
    private val startRow = (0 until height).random()
    private val startColumn = (0 until width).random()

    // Store possible endpoints when the algorithm begins backtracking to create the longest path.
    private var isBacktracking = false
    private var endPath = ArrayDeque<Pair<Pair<Int, Int>, List<Pair<Int, Int>>>>() // Endpoint (x, y), path TODO remove unused endpoint


    // Finished generating when visitedCount = sizeOfMaze.
    private val sizeOfMaze = height * width
    private var visitedCount = 0

    // Drawing positions
    private val spacing = 12
    private val smallMazeOffset = 6

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

        // Initialize the maze to a starting point.
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
                isBacktracking = false
            } else if (moveTo == "South") {
                maze[row][column + 1].visited = true
                maze[row][column].southBound = false
                maze[row][column + 1].northBound = false
                path.add(Pair(row, column + 1))
                isBacktracking = false
            } else if (moveTo == "East") {
                maze[row + 1][column].visited = true
                maze[row + 1][column].westBound = false
                maze[row][column].eastBound = false
                path.add(Pair(row + 1, column))
                isBacktracking = false
            } else if (moveTo == "West") {
                maze[row - 1][column].visited = true
                maze[row - 1][column].eastBound = false
                maze[row][column].westBound = false
                path.add(Pair(row - 1, column))
                isBacktracking = false
            }
            else {
                // Store the longest path through the maze.
                if(endPath.isEmpty() && !isBacktracking){
                    endPath.add(Pair(path.last(), path.toList()))
                }
                else if (!isBacktracking && path.size > endPath.last().second.size){
                    endPath.add(Pair(path.last(), path.toList()))
                }
                // Backtrack until we find a cell with an open direction. Remove from stack.
                path.removeLast()
                isBacktracking = true
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

    fun selectMazeSymbol() {
        for (i in 0 until width) {
            for (j in 0 until height) {
                val north = maze[j][i].northBound
                val east = maze[j][i].eastBound
                val south = maze[j][i].southBound
                val west = maze[j][i].westBound

                // Place unicode blocks based on the cell walls.
                if (south && !east && !north && !west) {
                    maze[j][i].symbol = "\u2569"
                } else if (!south && !east && north && !west) {
                    maze[j][i].symbol = "\u2566"
                } else if (!south && east && !north && !west) {
                    maze[j][i].symbol = "\u2563"
                } else if (!south && !east && !north && west) {
                    maze[j][i].symbol = "\u2560"
                } else if (south && east && !north && west) {
                    maze[j][i].symbol = "\u2568"
                } else if (!south && east && north && west) {
                    maze[j][i].symbol = "\u2565"
                } else if (south && east && north && !west) {
                    maze[j][i].symbol = "\u2561"
                } else if (south && !east && north && west) {
                    maze[j][i].symbol = "\u255e"
                } else if (!south && east && north && !west) {
                    maze[j][i].symbol = "\u2557"
                } else if (south && !east && !north && west) {
                    maze[j][i].symbol = "\u255a"
                } else if (south && east && !north && !west) {
                    maze[j][i].symbol = "\u255d"
                } else if (!south && !east && north && west) {
                    maze[j][i].symbol = "\u2554"
                } else if (!south && east && !north && west) {
                    maze[j][i].symbol = "\u2551"
                } else if (south && !east && north && !west) {
                    maze[j][i].symbol = "\u2550"
                } else {
                    maze[j][i].symbol = "\u256c"
                }
            }
        }
    }

    fun renderImage(renderSolution: Boolean): BufferedImage {
        // Buffered image to save the maze to and its drawing component.
        val mazeImage: BufferedImage = BufferedImage((width * spacing) + width,
            (height * spacing) + height, BufferedImage.TYPE_USHORT_565_RGB) // 2 bytes per pixel

        // Setup the graphics for drawing on the current pc.
        var buffer: Graphics = mazeImage.graphics
        buffer = buffer as Graphics2D
        buffer.color = Color.CYAN
        buffer.font = Font("Dialog", Font.PLAIN, spacing + 4)
        // Get data about current pc environment for rendering.
        var pcEnv = Toolkit.getDefaultToolkit().getDesktopProperty("awt.font.desktophints") as Map<*, *>
        if (pcEnv != null){
            buffer.addRenderingHints(pcEnv)
        }
        buffer.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF)
        buffer.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF)
        buffer.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON)
        buffer.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED)

        // Maze is written to image offset from center divided into a grid based on text size.
        var xPosition = (mazeImage.width / 2) / spacing
        var yPosition = (mazeImage.height / 2) / spacing + smallMazeOffset

        for (i in 0 until width) {
            for (j in 0 until height) {
                // Maze start.
                if(startRow == i && startColumn == j){ // TODO update to pair
                    buffer.color = Color.GREEN
                    buffer.drawString(maze[i][j].symbol, (i * spacing) + xPosition, (j * spacing) + yPosition)
                }
                // Maze end.
                else if (Pair(i, j) == endPath.last().second[endPath.last().second.size - 1]) {
                    buffer.color = Color.MAGENTA
                    buffer.drawString(maze[i][j].symbol, (i * spacing) + xPosition, (j * spacing) + yPosition)
                }
                // Responsible for printing path solution.
                else if (Pair(i, j) in endPath.last().second && renderSolution) {
                    buffer.color = Color.RED
                    buffer.drawString(maze[i][j].symbol, (i * spacing) + xPosition, (j * spacing) + yPosition)
                }
                // All other maze tiles.
                else {
                    buffer.color = Color.CYAN
                    buffer.drawString(maze[i][j].symbol, (i * spacing) + xPosition, (j * spacing) + yPosition)
                }
            }
        }

        return mazeImage
    }

    fun writeImageToFile(img: BufferedImage, fileName: String){
        // Write image to png file.
        try {
            val outputFilePng = File(fileName)
            ImageIO.write(img, "png", outputFilePng)
        } catch (e: IOException) {
            print("Error writing image: $e")
        }
    }
}


fun main() {
    println("Maze Generator")
    print("Enter maze size: ")
    val size = readLine()!!.toInt()
    println()

    // Create the maze data.
    var myMaze = Maze2(Integer.valueOf(size), Integer.valueOf(size));
    myMaze.createMaze()
    myMaze.selectMazeSymbol()

    // Render and write images.
    var finalMaze: BufferedImage = myMaze.renderImage(false)
    myMaze.writeImageToFile(finalMaze, "maze.png")
    finalMaze = myMaze.renderImage(true)
    myMaze.writeImageToFile(finalMaze, "mazesolution.png")

}
