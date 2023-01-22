
package project;

import java.util.Stack;

public class Puzzle {
	private final int[][] tiles;
	private int emptyX, emptyY;

	// constructor
	public Puzzle(int[][] tiles) {
		int n = tiles.length;
		this.tiles = new int[n][n];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				this.tiles[i][j] = tiles[i][j];
			}
		}
	}

	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append(tiles.length + "\n");
		for (int i = 0; i < tiles.length; i++) {
			for (int j = 0; j < tiles[i].length; j++) {
				str.append(" " + tiles[i][j]);
			}
			str.append("\n");
		}
		return str.toString();

	}

	public int dimension() {
		return this.tiles.length;
	}

	// sum of Manhattan distances between tiles and goal
	// The Manhattan distance between a board and the goal board is the sum
	// of the Manhattan distances (sum of the vertical and horizontal distance)
	// from the tiles to their goal positions.
	public int h() {
		int distances = 0;
		for (int i = 0; i < this.dimension(); i++) {
			for (int j = 0; j < this.dimension(); j++) {

				if (tiles[i][j] != 0 && tiles[i][j] != i * this.dimension() + j + 1) {
					int xgoal = (this.tiles[i][j] - 1) / this.dimension();
					int ygoal = this.tiles[i][j] - 1 - (xgoal * this.dimension());
					distances += Math.abs(ygoal - j) + Math.abs(xgoal - i);
				}
			}
		}
		return distances;
	}

	// check if we had reached the goal
	public boolean isCompleted() {

		int truevalue = 0;
		for (int i = 0; i < this.dimension(); i++) {
			for (int j = 0; j < this.dimension(); j++) {
				truevalue++;
				if (tiles[i][j] == 0) {
					continue;
				}

				if (tiles[i][j] != truevalue) {
					return false;
				}
			}
		}

		return true;

	}

	// Returns any kind of collection that implements iterable.
	// For this implementation, I choose stack.
	public Iterable<Puzzle> getAdjacents() {
		int[][] original = this.copy(this.tiles);
		int[][] right = this.copy(this.tiles);
		int[][] left = this.copy(this.tiles);
		int[][] up = this.copy(this.tiles);
		int[][] down = this.copy(this.tiles);
		this.findEmptySquare();
		// right
		if (this.emptyY < this.dimension() - 1) {
			swap(right, this.emptyX, this.emptyY, this.emptyX, this.emptyY + 1);
		}
		// left
		if (this.emptyY > 0) {
			swap(left, this.emptyX, this.emptyY, this.emptyX, this.emptyY - 1);
		}
		// down
		if (this.emptyX < this.dimension() - 1) {
			swap(down, this.emptyX, this.emptyY, this.emptyX + 1, this.emptyY);
		}
		// up
		if (this.emptyX > 0) {
			swap(up, this.emptyX, this.emptyY, this.emptyX - 1, this.emptyY);
		}

		Stack<Puzzle> myAdjacents = new Stack<Puzzle>();

		// make sure we are not adding the same board as neighbour of itself
		if (java.util.Arrays.deepEquals(up, original) == false)
			myAdjacents.push(this.createPuzzle(up));

		if (java.util.Arrays.deepEquals(down, original) == false)
			myAdjacents.push(this.createPuzzle(down));

		if (java.util.Arrays.deepEquals(right, original) == false)
			myAdjacents.push(this.createPuzzle(right));

		if (java.util.Arrays.deepEquals(left, original) == false)
			myAdjacents.push(this.createPuzzle(left));
		return myAdjacents;

	}

	private Puzzle createPuzzle(int[][] copy) {
		Puzzle z = new Puzzle(copy);
		return z;
	}

	// protecting immutability of tiles
	private int[][] copy(int[][] source) {
		int n = this.dimension();
		int[][] myCopy = new int[n][n];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				myCopy[i][j] = source[i][j];
			}
		}

		return myCopy;
	}

	// update value of emptyX and emptyY
	public void findEmptySquare() {

		for (int i = 0; i < this.dimension(); i++) {
			for (int j = 0; j < this.dimension(); j++) {
				if (tiles[i][j] == 0) {
					this.emptyX = i;
					this.emptyY = j;
					return;
				}
			}
		}
	}

	// used when finding the adjacents
	private static void swap(int[][] myCopy, int x, int y, int x1, int y1) {
		int a = myCopy[x][y];
		int b = myCopy[x1][y1];
		myCopy[x][y] = b;
		myCopy[x1][y1] = a;

	}

	// check equality for 2D nested Arrays
	static public boolean checkEqualPuzzle(Puzzle a, Puzzle b) {
		if (java.util.Arrays.deepEquals(a.tiles, b.tiles))
			return true;
		return false;
	}

	// no need for this
//	public void printPuzzle() {
//		for (int i = 0; i < this.dimension(); i++) {
//			for (int j = 0; j < this.dimension(); j++) {
//				System.out.print(this.tiles[i][j] + " ");
//			}
//			System.out.println();
//		}
//	}
	// You can use this main method to see your Puzzle structure.
	// Actual solving operations will be conducted in Solver.main method
//	public static void main(String[] args) {
//		int[][] array = { { 8, 1, 3 }, { 4, 0, 2 }, { 7, 6, 5 } };
//		Puzzle board = new Puzzle(array);
//		System.out.println(board);
//		System.out.println(board.dimension());
//		System.out.println(board.h());
//		System.out.println(board.isCompleted());
//		Iterable<Puzzle> itr = board.getAdjacents();
//		for (Puzzle neighbor : itr) {
//			System.out.println(neighbor);
//			System.out.println(neighbor.equals(board));
//		}
//	}
}
