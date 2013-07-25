import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * The class <code>Solver</code> is an implementation of a greedy algorithm to
 * solve the knapsack problem.
 * 
 */
public class Solver {

	/**
	 * The main class
	 */
	public static void main(String[] args) {
		try {
			solve(args);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Read the instance, solve it, and print the solution in the standard
	 * output
	 */
	public static void solve(String[] args) throws IOException {
		String fileName = null;

		// get the temp file name
		for (String arg : args) {
			if (arg.startsWith("-file=")) {
				fileName = arg.substring(6);
			}
		}
		if (fileName == null)
			return;

		// read the lines out of the file
		List<String> lines = new ArrayList<String>();

		BufferedReader input = new BufferedReader(new FileReader(fileName));
		try {
			String line = null;
			while ((line = input.readLine()) != null) {
				lines.add(line);
			}
		} finally {
			input.close();
		}

		// parse the data in the file
		String[] firstLine = lines.get(0).split("\\s+");
		int items = Integer.parseInt(firstLine[0]);
		int capacity = Integer.parseInt(firstLine[1]);

		int[] values = new int[items];
		int[] weights = new int[items];

		for (int i = 1; i < items + 1; i++) {
			String line = lines.get(i);
			String[] parts = line.split("\\s+");

			values[i - 1] = Integer.parseInt(parts[0]);
			weights[i - 1] = Integer.parseInt(parts[1]);
		}

		Solution solution = new Solution(items, capacity, values, weights);
		solution.run();
//		System.out.println("iterations ["+ solution.iter +"]");
		int value = solution.value;
		int[] taken = solution.taken;

		// prepare the solution in the specified output format
		System.out.println(value + " 0");
		for (int i = 0; i < items; i++) {
			System.out.print(taken[i] + " ");
		}
		System.out.println("");
	}

	private static class Solution {
		int items;
		int capacity;

		int value = 0;
		int[] taken;
		final int[] values;
		final int[] weights;
		int minWeight;
		final Quicksort quicksort;
		long iter = 0;

		private Solution(int items, int capacity, int[] values, int[] weights) {
			this.items = items;
			this.capacity = capacity;
			taken = new int[items];
			this.values = values;
			this.weights = weights;
			quicksort = new Quicksort();
		}

		private void run() {
			BranchAndBound bab = new BranchAndBound();
			bab.run();
			taken = bab.taken;
			value = bab.maxValue;
		}

//		class DynamicProgramming {
//			// tips: how much space do you need?
//			void run() {
//			}
//		}

		class BranchAndBound {
			int maxValue = 0;
			float[] valuePerWeight = new float[items];
			int[] taken = new int[items];
			int[] index = new int[items]; 
			int[] values = new int[items];
			int[] weights = new int[items];

			private void run() {
				computeSortedValuePerWeight();
				initValuesAndWeights();
//				System.out.println();
//				for (int v : values) {
//					System.out.print(String.format("%d ", v));
//				}
//				System.out.println();
				float[] fractions = new float[items];
				float initialBound = fillBound(0, capacity, 0, fractions);
				runRec(0, 0, capacity, initialBound, fractions);
			}
			
			private void initValuesAndWeights() {
				minWeight = this.weights[0];
				for(int i = 0;i<items;i++){
					this.values[i] = Solution.this.values[index[i]];
					this.weights[i] = Solution.this.weights[index[i]];
					minWeight  = Math.min(minWeight, weights[i]);
				}
			}

			private void computeSortedValuePerWeight(){
				for (int i = 0; i < items; i++) {
					index[i] = i;
					valuePerWeight[i] = (float)Solution.this.values[i] / Solution.this.weights[i];
				}
				quicksort.quicksort(valuePerWeight, index);
				//descending order
				for(int i = 0; i < items / 2;i++){
					int tmp = index[items-i-1];
					index[items-i-1] = index[i];
					index[i] = tmp;
					
					float tmp2 = valuePerWeight[items-i-1];
					valuePerWeight[items-i-1] = valuePerWeight[i];
					valuePerWeight[i] = tmp2;
				}
			}

			private float runRec(int item, int value, int room,
					float bound, float[] fractions) {
				iter++;
				if (room >= weights[item]) {// there is enough room for the item
					fractions[item] = 1;
//					printFraction(fractions);
					value += values[item];
					room -= weights[item];
					if (room >= minWeight//there is still room after taking item
						&& item + 1 < items//there are still items left
						&& bound > maxValue//the bound says what is the most achievable from the subtree
					) {//move to the next item
//						System.out.println("bound1 "+bound);
						bound = runRec(item + 1, value, room, bound/*bound estimate is not changed*/, fractions);
//						System.out.println("bound2 "+bound);
					}
					
					if (value > maxValue) {
						maxValue = value;
						this.taken = new int[items];
						for (int i = 0; i <= item; i++) {
							this.taken[index[i]] = fractions[i] == 1 ? 1 : 0;
						}
					}
					
					value -= values[item];
					room += weights[item];
				}
				
				bound = bound - fractions[item]*values[item];				
				fractions[item] = 0;
				if (item + 1 < items) {
					//item will not be used anymore
//					printFraction(fractions);
					bound = fillBound(item+1, room, bound, fractions);
					
//					checkIncrementalBoundCalculation(bound, fractions);
					if (bound > maxValue){
//						System.out.println("bound3 "+bound);
						bound = runRec(item + 1, value, room, bound, fractions);
//						System.out.println("bound4 "+bound);
					}
				}
				
				

				return bound;
			}

			private void printFraction(float[] fractions) {
				System.out.println();
				for (float f : fractions) {
					System.out.print(String.format("%e ", f));
				}
				System.out.println();
			}

			//create estimate using a previous estimate and the additional items starting from "from"
			private float fillBound(int from, int room, float bound,
					float[] fractions) {
				for(int i = from;i<items;i++){
					bound -= fractions[i]*values[i];//if this item's fraction was already used, clean it up first
					//fill the space with the next one or it's fraction, repeat until there is no more room
					if (room > weights[i]){
						fractions[i] = 1;
//						printFraction(fractions);
						bound += values[i];
						room -= weights[i];
					} else {
						//store the value fractions
						fractions[i] = (float)room/weights[i];
//						printFraction(fractions);
						bound += fractions[i]*values[i];
						break;
					}
				}
				
				return bound;
			}

			private void checkIncrementalBoundCalculation(float actual, float[] fractions) {
				float expected = 0;
				int room = capacity;
				for(int i = 0;i<items;i++){
					if (fractions[i] > 0){
						if (room > weights[i]){
							expected += values[i];
							room -= weights[i];
						} else {
							expected += values[i]*(float)room/weights[i];
							break;
						}
					}
				}
				
				if (Math.abs(actual - expected) > 0.0001){
					throw new AssertionError(String.format("expected:%e got:%e", expected, actual));
				}
			}

		}

	}
	
	static class Quicksort {
		public void quicksort(float[] main, int[] index) {
			for(int i = 0;i<index.length;i++){
				index[i] = i;
			}
			quicksort(main, index, 0, index.length - 1);
		}

		// quicksort a[left] to a[right]
		public void quicksort(float[] a, int[] index, int left, int right) {
			if (right <= left)
				return;
			int i = partition(a, index, left, right);
			quicksort(a, index, left, i - 1);
			quicksort(a, index, i + 1, right);
		}

		// partition a[left] to a[right], assumes left < right
		private int partition(float[] a, int[] index, int left, int right) {
			int i = left - 1;
			int j = right;
			while (true) {
				while (less(a[++i], a[right]))
					// find item on left to swap
					; // a[right] acts as sentinel
				while (less(a[right], a[--j]))
					// find item on right to swap
					if (j == left)
						break; // don't go out-of-bounds
				if (i >= j)
					break; // check if pointers cross
				exch(a, index, i, j); // swap two elements into place
			}
			exch(a, index, i, right); // swap with partition element
			return i;
		}

		// is x < y ?
		private boolean less(float x, float y) {
			return (x < y);
		}

		// exchange a[i] and a[j]
		private void exch(float[] a, int[] index, int i, int j) {
			float swap = a[i];
			a[i] = a[j];
			a[j] = swap;
			int b = index[i];
			index[i] = index[j];
			index[j] = b;
		}
	}
}
