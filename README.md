# Ancient Ruins Explorer: The Lost Paths of the Relic
# TUGAS KECIL 3 - STRATEGI ALGORITMA
## Description
Deep beneath the dust of forgotten temples lies a labyrinth of corridors, traps, and relic markers. In this project, a traveler known as the Ancient Ruins Explorer must navigate a grid-shaped ruin, avoid walls and hazards, collect ordered target digits when present, and reach the sacred goal tile.

The program compares multiple pathfinding strategies, including UCS, GBFS, A*, BFS, and DFS, to reveal how each algorithm behaves when the route is narrow, costly, or full of misleading shortcuts. A console mode and a graphical mode are provided so the journey can be inspected step by step.

## Team Members
- Geraldo Artemius - 13524005
- Junior Natra Situmorang - 13524055

## How to Run the Program
This project uses Java. The commands below assume you run them from the `src` folder so the relative paths to `../test/input`, `../test/iteration`, and `../test/output` work correctly.

### 1. Compile the source files
```bash
cd src
javac *.java
```

### 2. Run the console version
```bash
java Main
```

### 3. Run the graphical version
```bash
java GUIMain
```

### 4. Provide input when prompted
When the program asks for an input file, type only the filename located in `test/input/`.

Example:
```text
test1.txt
```

### 5. Optional output files
- Iteration logs are written to `test/iteration/`
- Saved solutions are written to `test/output/`

## Project Structure
```text
/
├── src/
│   ├── Algorithm.java
│   ├── GUIMain.java
│   ├── Main.java
│   ├── Map.java
│   └── State.java
├── test/
│   ├── input/
│   ├── iteration/
│   └── output/
├── README.md
└── LICENSE
```

## File Descriptions
- `src/Main.java` - Console entry point. Handles input selection, algorithm selection, playback, and saving solutions.
- `src/GUIMain.java` - Swing-based graphical interface for visualizing the map, algorithm result, and step-by-step playback.
- `src/Algorithm.java` - Core pathfinding logic for UCS, GBFS, A*, BFS, and DFS.
- `src/Map.java` - Input parser and validator for board dimensions, tiles, targets, and cost matrix.
- `src/State.java` - Search node representation used by the algorithms.
- `test/input/` - Input test cases for the ruin maps.
- `test/iteration/` - Generated iteration traces during solving.
- `test/output/` - Saved solution files.
- `README.md` - Project overview and usage guide.

## Input and Output

### Input
The program expects a text file with the following format:
1. First line: two integers `N M` representing the number of rows and columns.
2. Next `N` lines: the grid map.
	- `X` = wall
	- `*` = open path
	- `Z` = start position, exactly one
	- `O` = goal position, exactly one
	- `0`, `1`, `2`, ... = optional ordered digit targets
3. Next `N` lines: the cost matrix with `N x M` integers.

### Output
The program prints:
- the solution path, if one is found
- the total cost of the solution
- the board state for each step
- execution time
- total number of iterations

If the user chooses to save the result, the program also writes a solution file containing the path and step-by-step board snapshots.

### Example Input
```text
5 8
XXXXXXXX
XZ**0**X
X*XX*X*X
X***1*OX
XXXXXXXX
999 999 999 999 999 999 999 999
999 1 2 3 4 5 6 999
999 2 999 999 3 999 4 999
999 2 2 2 3 4 2 999
999 999 999 999 999 999 999 999
```

## Additional Notes
- The map must contain exactly one `Z` and one `O`.
- If digits are used, they must appear in order without skipping values.
- `X` tiles should use a very large cost value such as `999` in the cost matrix.
- The program supports both manual playback in the console and interactive visualization in the GUI.
- Relative paths are important: run the program from `src` so the input and output folders are found correctly.