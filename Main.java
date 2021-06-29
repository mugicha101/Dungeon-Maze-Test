import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

class Main {
    public static void main(String[] args) {
        Maze maze = new Maze(); // maze object
        System.out.println(maze);
    }
}

class Maze {
    private int[] dim; // dimensions of the maze
    private List<List<Integer>> graph; // adjacency list representation of graph
    private List<Integer> deadEnds; // indexes of dead ends
    private int startIndex;

    public Maze(int width, int height) { // constructor
        // initialize instance vars
        this.dim = new int[] {width, height};
        this.graph = new ArrayList<List<Integer>>();
        this.deadEnds = new ArrayList<Integer>();

        // populate adjacency list
        int listCount = width*height;
        for (int i = 0; i < listCount; i++)
            this.graph.add(new ArrayList<Integer>());

        // set starting point
        this.startIndex = width/2;

        // generate maze
        boolean[] visited = new boolean[width*height];
        this.mazeGen(this.startIndex, visited);
    }

    // recursive DFS maze generator method
    private boolean mazeGen(int index, boolean[] visited) {
        // mark as visited
        visited[index] = true;

        // get neighbors
        int[] neighbors = new int[] {-1, -1, -1, -1};
        if (index % this.dim[1] > 0) // left neighbor
            neighbors[0] = index-1;
        if (index % this.dim[1] < this.dim[0]-1) // right neighbor
            neighbors[1] = index+1;
        if (index >= this.dim[1]) // top neighbor
            neighbors[2] = index-this.dim[0];
        if (index < this.dim[0]*(this.dim[1]-1)) // bottom neighbor
            neighbors[3] = index+this.dim[0];
        
        // continue recursion through neighbors
        boolean hasNeighbors = false;
        Random rand = new Random();
        for (int i = 0; i < 4; i++) {
            // choose random neighbor
            int nIndex;
            if (i == 3) nIndex = neighbors[0];
            else {
                int rIndex = rand.nextInt(4-i);
                nIndex = neighbors[rIndex];
                neighbors[rIndex] = neighbors[3-i];
                neighbors[3-i] = nIndex;
            }

            // skip conditions
            if (nIndex == -1) continue;
            if (visited[nIndex]) continue;
            hasNeighbors = true;

            // update graph
            this.graph.get(index).add(nIndex);
            this.graph.get(nIndex).add(index);

            // recursion
            if (!this.mazeGen(nIndex, visited))
                this.deadEnds.add(nIndex);
        }

        return hasNeighbors;
    }

    public Maze() { // default constructor
        this(5, 5);
    }

    public String toString() { // convert maze into a display string
        // initialize output matrix
        List<List<Character>> outputMatrix = new ArrayList<List<Character>>();
        int width = this.dim[0]*2+1;
        int height = this.dim[1]*2+1;
        for (int i = 0; i < height; i++) {
            List row = new ArrayList<Character>();
            for (int j = 0; j < width; j++) {
                row.add(i % 2 == 1 && j % 2 == 1? ' ' : '#');
            }
            outputMatrix.add(row);
        }
        outputMatrix.get(0).set(this.startIndex * 2 + 1, ' ');

        // add edges
        int rowLen = this.dim[0];
        for (int i = 0; i < this.graph.size(); i++) {
            List<Integer> edges = this.graph.get(i);
            int x = (i % rowLen) * 2 + 1;
            int y = (i / rowLen) * 2 + 1;
            for (Integer j : edges) {
                int diff = j - i;
                if (diff == 1) // right
                    outputMatrix.get(y).set(x+1,' ');
                else if (diff == -1) // left
                    outputMatrix.get(y).set(x-1,' ');
                else if (diff == -rowLen) // top
                    outputMatrix.get(y-1).set(x,' ');
                else if (diff == rowLen) // bottom
                    outputMatrix.get(y+1).set(x,' ');
            }
        }

        // add dead ends
        for (int i : this.deadEnds) {
            int x = (i % rowLen) * 2 + 1;
            int y = (i / rowLen) * 2 + 1;
            outputMatrix.get(y).set(x, '+');
        }

        // convert output matrix to string
        String output = "";
        for (List<Character> row : outputMatrix) {
            for (Character c : row) {
                output += c.toString() + " ";
            }
            output += "\n";
        }
        return output;
    }

    public String toAdjacencyListString() { // convert adjacency list to string
        String output = "";
        for (int i = 0; i < this.graph.size(); i++) {
            output += i + "->" + this.graph.get(i).toString() + "\n";
        }
        return output;
    }
}