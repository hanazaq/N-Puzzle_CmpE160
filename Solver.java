
package project;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.Stack;

public class Solver {
	static PriorityQueue<PriorityObject> minPQ;
	private int moves;
	static PriorityObject first; // store the initial board
	static PriorityObject last; // store the final board (goal). used in getSolution()
	private PriorityObject winner; // who has the lower f in minPQ

	// priority = moves + manhattan
	// if priority is low, it's good.
	// find a solution to the initial board
	public Solver(Puzzle root) {
		// initialize field
		minPQ = new PriorityQueue<PriorityObject>(new CustomComparator());

		System.out.println("Starting the solver...");
		if (root == null)
			throw new IllegalArgumentException();
		solve(root); // here we go
		System.out.println("Solving is finished...");
	}

	private void findChildren(PriorityObject po) {
//		System.out.println(">>>>>>" + this.checkmoves);
//		System.out.println(po.board);

		// if po is completed we are done
		if (po.board.isCompleted()) {
			last = po;
			return;
		} else {

			// study this new state's adjacents
			Stack<Puzzle> s = (Stack<Puzzle>) po.board.getAdjacents();

			// iterating over all stack elements
			while (!s.empty()) {
				Puzzle optimal = s.pop();
				// prev board is po (the parent) and g values ++
				PriorityObject optimalp = new PriorityObject(optimal, po.g + 1, po);
				// no chance to get to prev state from here
				if (po.g < 1) {
					minPQ.add(optimalp);
				}
				// avoid getting back to prev state again
				else if (Puzzle.checkEqualPuzzle(optimalp.board, po.prev.board) == false) {
					minPQ.add(optimalp);
				}
			}
		}
	}

	private void solve(Puzzle root) {
		// create initial PriorityObject and register it as the first step in the
		// solution
		PriorityObject initial = new PriorityObject(root, 0, null);
		first = initial;

		// start solving with the root
		findChildren(initial);

		// iterate till reaching the solution
		while (last == null) {
			// stores PriorityObject with equal lowest f values
			Stack<PriorityObject> winners = new Stack<PriorityObject>();
			this.winner = minPQ.poll();
			winners.push(this.winner);
			int lessCost = this.winner.f;
			// find all priority objects with f equals to lessCost
			while (!minPQ.isEmpty() && minPQ.peek().f == lessCost && last == null) {
				this.winner = minPQ.poll();
				winners.push(this.winner);
			}
			// now lets solve
			while (!winners.isEmpty()) {
				findChildren(winners.pop());
			}
		}

	}

	// num of steps in the ultimate path
	public int getMoves() {
		getSolution();
		return this.moves;
	}

	static Stack<Puzzle> finalanswer;

	// this method will be called in getMoves and in main method.
	// avoid calculating the answer all over again
	public Iterable<Puzzle> getSolution() {
		if (finalanswer == null) {
			this.moves = -1;
			// used in the middle as a way to Reverse finalanswer Stack
			Stack<Puzzle> answer = new Stack<Puzzle>();
			finalanswer = new Stack<Puzzle>();
			PriorityObject here = last;
			// find solution with help of object.prev from last state to initial state
			while (here != null) {
				this.moves++;
				answer.push(here.board);
				here = here.prev;
			}
			while (!answer.isEmpty()) {
				finalanswer.push(answer.pop());
			}
		}
		return finalanswer;
	}

	private class PriorityObject {

		private Puzzle board;
		private int f;
		PriorityObject prev;
		private int g;

		PriorityObject(Puzzle board, int g, PriorityObject prev) {
			this.board = board;
			this.prev = prev;
			this.g = g;
			this.f = g + board.h();
		}

		public Comparator<PriorityObject> comparator() {
			return new CustomComparator();
		}
	}

	private class CustomComparator implements Comparator<PriorityObject> {

		@Override
		public int compare(PriorityObject o1, PriorityObject o2) {
			if (o1.f > o2.f) {
				return 1;
			} else if (o1.f < o2.f) {
				return -1;
			}
			return 0;
		}

	}

	// return the num of inversions needed in initial board
	// e.g.
	// 8 1 2
	// 0 4 3
	// 7 6 5
	// total inversions neeeded= 7+0+0+1+0+2+1+0= 11
	static int nofInversions(int[][] arr) {
		int len = arr.length;
		ArrayList<Integer> tillNow = new ArrayList<Integer>();
		int invNum = 0;
		for (int i = 0; i < len; i++) {
			for (int j = 0; j < len; j++) {
				int elem = arr[i][j];

				for (int s = 1; s < elem; s++) {
					// expect to find elements 1 to elem-1 before it. other wise an inversion is
					// needed
					if (tillNow.contains(s) == false)
						invNum++;
				}
				tillNow.add(elem);

			}
		}
		return invNum;
	}

	// return true if given puzzle is solvable.
	static boolean solvable(int[][] puzzle) {

		int invNum = nofInversions(puzzle);
//		System.out.println(invNum);
		// puzzle is solvable if inversions number is even
		return (invNum % 2 == 0);
	}

	// test client
	public static void main(String[] args) throws IOException {

		File input = new File("input.txt");
		// Read this file int by int to create
		// the initial board (the Puzzle object) from the file
		Scanner in = null;
		try {
			in = new Scanner(input);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		// read input
		int n = Integer.valueOf(in.nextLine().split(" ")[0]);
		int[][] tiles = new int[n][n];

		for (int i = 0; i < n; i++) {
			if (in.hasNextLine()) {
				String[] line = in.nextLine().split(" ");
				for (int j = 0; j < n; j++) {
					int tile = Integer.valueOf(line[j]);
					tiles[i][j] = tile;

				}

			}
		}

		// solve the puzzle here. Note that the constructor of the Solver class already
		// calls the
		// solve method. So just create a solver object with the Puzzle Object you
		// created above
		// is given as argument, as follows:

		File output = new File("output.txt");
		output.createNewFile();
		PrintStream write = new PrintStream(output);
		// solvable(tiles) can detect unsolvable 3d board
		if (n == 3 && !solvable(tiles)) {
			write.println("Board is unsolvable");
		} else {
			Puzzle initial = new Puzzle(tiles);
			Solver solver = new Solver(initial); // where initial is the Puzzle object created from input file

			// You can use the following part as it is. It creates the output file and fills
			// it accordingly.

			write.println("Minimum number of moves = " + solver.getMoves());
			for (Puzzle board : solver.getSolution())
				write.println(board);

		}
	}
}
