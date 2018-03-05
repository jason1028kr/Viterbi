import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

public class Viterbi {
	
	private double[] pi;
	private double[][] stateTrans;
	private double[][] emissionProb;
	private double[] tScoreArray;
	private LinkedList<LinkedList<State>> tempSeq;
	private ArrayList<Integer> bestSeq;
	private ArrayList<Integer> worstSeq;
	private List<List<Integer>> bestSeqSet;
	
	
	private class State {
		State predl;
		State predr;
		int index;
		double score;
		PriorityQueue<Double> recordQueue;
		State(State predl, State predr, int index,
				double score, PriorityQueue<Double> queue) {		
			this.predl = predl;	
			this.predr = predr;
			this.index = index;
			this.score = score;
			recordQueue = queue;
		}
	}
	public Viterbi(double[] pi, double[][] A, double[][] B) {
		if (A == null || B == null || pi == null) {
			throw new IllegalArgumentException();
		}
		for (int i = 0; i < A.length; i++) {
			double sum = 0;
			for (int j = 0; j < A[i].length; j ++) {
				sum = sum + A[i][j];
			}
			if (sum != 1.0) {
				throw new IllegalArgumentException();
			}
		}
		for (int i = 0; i < B.length; i++) {
			double sum = 0;
			for (int j = 0; j < B[i].length; j ++) {
				sum = sum + B[i][j];
			}
			if (sum != 1.0) {
				throw new IllegalArgumentException();
			}
		}
		
		double sum = 0;
		for (int i = 0; i < pi.length; i++) {
			sum = sum + pi[i];
		}
		if (sum != 1.0) {
			throw new IllegalArgumentException();
		}
		
		if (pi.length != A.length) {
			throw new IllegalArgumentException();
		}
				
		this.pi = pi;
		stateTrans = A;
		emissionProb = B;
		tScoreArray = new double[pi.length];
		tempSeq = new LinkedList<LinkedList<State>>();
		bestSeq = new ArrayList<Integer>();
		worstSeq = new ArrayList<Integer>();
		bestSeqSet = new LinkedList<List<Integer>>();
	}
	
	public double probOfSeq(int[] omega, 
			List<Integer> seq, boolean underflow) {
		if (seq == null || seq.size() == 0) {
			throw new IllegalArgumentException();
		}
		if (omega == null || omega.length == 0) {
			throw new IllegalArgumentException();
		}	
		double pr = pi[seq.get(0)] 
				* emissionProb[seq.get(0)][omega[0]];
		for (int i = 1; i < omega.length; i++) {
			pr = pr * (stateTrans[seq.get(i - 1)][seq.get(i)] 
						* emissionProb[seq.get(i)][omega[i]]);		
		}
		return pr;
	}	
	public List<Integer> bestSeq(int[] omega,
			boolean underflow) {
		
		
		// put appropriate values when t = 0
		LinkedList<State> list = new LinkedList<State>();
		for (int i = 0; i < pi.length; i++) {
			if (underflow) {
				if (pi[i] == 0 || emissionProb[i][omega[0]] == 0) {
					tScoreArray[i] = Double.NEGATIVE_INFINITY;
				}
				else {
					tScoreArray[i] = Math.log(pi[i]) + 
							Math.log(emissionProb[i][omega[0]]);
				}				
			}
			else {
				tScoreArray[i] = pi[i] * emissionProb[i][omega[0]];				
			}
			list.add(new State(null, null, i, tScoreArray[i], null));
		}
		tempSeq.add(list);

		
		// updating the last scores, also update tempSeq list of list	
		for (int i = 1; i < omega.length; i++) {
			tScoreArray = bestScoreArray(tScoreArray,
					omega, i, tempSeq, underflow);		
		}
		
		// find the final states predl and predr
		int max = 0;
		double maxVal = tScoreArray[0];
		for (int i = 0; i < pi.length; i++) {
			if (tScoreArray[i] >= maxVal) {
				max = i;
				maxVal = tScoreArray[i];
			}
		}
		int leftMaxIndex = 0;
		for (int k = 0; k < tScoreArray.length; k++) {
			if (tScoreArray[k] == maxVal) {
				leftMaxIndex = k;
				if (leftMaxIndex == max) {
					max = leftMaxIndex;
				}
				break;
			}
		}
		
		// back track to create the best sequence
		bestSeq.add(leftMaxIndex);
		State finalState = tempSeq.getLast().get(leftMaxIndex);
		State predl = finalState.predl;
		while (predl != null) {
			bestSeq.add(0, predl.index);
			predl = predl.predl;
		}
		
		// print the result
		for (int i = 0; i < bestSeq.size(); i++) {
			if (i == bestSeq.size() - 1) {
				System.out.println(bestSeq.get(i));
			}
			else {
			System.out.print(bestSeq.get(i) + ",");
			}
		}
		return bestSeq;
	}
	private double[] bestScoreArray(double[] prev,
			int[] omega, int time, LinkedList<LinkedList<State>> tempSeq,
			boolean underflow) {
		
		double[] prevCopy = prev.clone();
		double[] tScoreArray = new double[prev.length];
		LinkedList<State> list = new LinkedList<State>();
		double tScore;
		
		
		for (int i = 0; i < prev.length; i++) {
			
			// recordQueue which will be added to each state in i
			PriorityQueue<Double> recordQueue = new PriorityQueue<Double>();
			
			for (int j = 0; j < prev.length; j++) {
				if (underflow) {
					
					// tempQueue is the recordQueue of the previous t
					PriorityQueue<Double> tempQueue = 
							tempSeq.get(time - 1).get(j).recordQueue;
					
					// put negative infinty of 0 for underflow case
					if (stateTrans[j][i] == 0 ||
							emissionProb[i][omega[time]] == 0) {
						tScore = Double.NEGATIVE_INFINITY;
						
						for (int k = 0; k < prev.length; k++) {
							recordQueue.add(Double.NEGATIVE_INFINITY);
						}
					}
					else {
						tScore = prevCopy[j] + Math.log(stateTrans[j][i])
						+ Math.log(emissionProb[i][omega[time]]);
						
						if (tempQueue != null) {
	
							Double[] tempQueueArray = tempQueue.toArray(new Double[tempQueue.size()]);
							for (int k = 0; k < tempQueueArray.length; k++) {
								recordQueue.add(tempQueueArray[k] * stateTrans[j][i]
									* emissionProb[i][omega[time]]);
							}			
						}
						
						// if it is t=1 simply add calculated tscores to the queue
						else {
							recordQueue.add(tScore);
						}										
					}
				}
				else {
					tScore = prevCopy[j] * stateTrans[j][i]
							* emissionProb[i][omega[time]];	
					
					// create tempQueue which stores the tscore records
					PriorityQueue<Double> tempQueue = 
							tempSeq.get(time - 1).get(j).recordQueue;
					if (tempQueue != null) {
						while (!tempQueue.isEmpty())
						recordQueue.add(tempQueue.remove() * stateTrans[j][i]
								* emissionProb[i][omega[time]]);						
					}
					
					// if it is t=1 simply add calculated tscores to the queue
					else {
						recordQueue.add(tScore);
					}				
				}
				tScoreArray[j] = tScore;			
			}
			
			
			
			
			// getting max indices left and right and tscore
			int maxIndex = 0;
			double maxVal = tScoreArray[0];
			for (int k = 0; k < tScoreArray.length; k++) {
				if (tScoreArray[k] >= maxVal) {
					maxIndex = k;
					maxVal = tScoreArray[k];
				}				
			}
			int leftMaxIndex = 0;
			for (int k = 0; k < tScoreArray.length; k++) {
				if (tScoreArray[k] == maxVal) {
					leftMaxIndex = k;
					if (leftMaxIndex == maxIndex) {
						maxIndex = leftMaxIndex;
					}
					break;
				}
			}		
			
			
			/*
			 * creating and inserting State objects 
			 * with two preds into t = time
			 */
			list.add(new State(tempSeq.get(time - 1).get(leftMaxIndex),
					tempSeq.get(time - 1).get(maxIndex), i, maxVal, recordQueue));
			prev[i] = maxVal;
		}
		// for each t, add list of up to date state list
		tempSeq.add(list);
		return prev;
	}
	
	public List<Integer> worstSeq(int[] omega,
			boolean underflow) {

		// put appropriate values when t = 0
		LinkedList<State> list = new LinkedList<State>();
		for (int i = 0; i < pi.length; i++) {
			if (underflow) {
				if (pi[i] == 0 || emissionProb[i][omega[0]] == 0) {
					tScoreArray[i] = Double.NEGATIVE_INFINITY;
				}
				else {
					tScoreArray[i] = Math.log(pi[i]) + 
							Math.log(emissionProb[i][omega[0]]);
				}	
			}
			else {
				tScoreArray[i] = pi[i] * emissionProb[i][omega[0]];
			}
			list.add(new State(null, null, i, tScoreArray[i], null));
		}
		tempSeq.add(list);
		
		// updating the last scores, also update tempBestSeq	
		for (int i = 1; i < omega.length; i++) {
			tScoreArray = worstScoreArray(tScoreArray,
					omega, i, tempSeq, underflow);	
		}
		// find the final state
		int min = 0;
		double minVal = tScoreArray[0];
		for (int i = 0; i < pi.length; i++) {
			if (tScoreArray[i] > minVal) {
				min = i;
				minVal = tScoreArray[i];
			}
		}
		worstSeq.add(min);
		State finalState = tempSeq.getLast().get(min);
		State predl = finalState.predl;
		while (predl != null) {
			worstSeq.add(0, predl.index);
			predl = predl.predl;
		}
		for (int i = 0; i < worstSeq.size(); i++) {
			if (i == worstSeq.size() - 1) {
				System.out.print(worstSeq.get(i));
			}
			else {
				System.out.print(worstSeq.get(i) + ",");
			}
		}
		return worstSeq;
	}
	private double[] worstScoreArray(double[] prev,
			int[] omega, int time, LinkedList<LinkedList<State>> tempSeq,
			boolean underflow) {
		
		double[] prevCopy = prev.clone();
		double[] tScoreArray = new double[prev.length];
		LinkedList<State> list = new LinkedList<State>();
		double tScore;
		for (int i = 0; i < prev.length; i++) {
			for (int j = 0; j < prev.length; j++) {
				if (underflow) {
					if (stateTrans[j][i] == 0 ||
							emissionProb[i][omega[time]] == 0) {
						tScore = Double.NEGATIVE_INFINITY;
					}
					else {
						tScore = prevCopy[j] + Math.log(stateTrans[j][i])
						+ Math.log(emissionProb[i][omega[time]]);
					}
				}
				else {
					tScore = prevCopy[j] * stateTrans[j][i]
							* emissionProb[i][omega[time]];
				}				
				tScoreArray[j] = tScore;			
			}
			
			// getting min index and tscore
			int minIndex = 0;
			double minVal = tScoreArray[0];
			for (int k = 0; k < tScoreArray.length; k++) {
				if (tScoreArray[k] < minVal) {
					minIndex = k;
					minVal = tScoreArray[k];
				}				
			}
			// creating and inserting State objects into t = time			
			list.add(new State(tempSeq.get(time - 1).get(minIndex),
					tempSeq.get(time - 1).get(minIndex), i, minVal, null));
			prev[i] = minVal;
		}
		tempSeq.add(list);
		return prev;
	}
	
	
	public List<List<Integer>> bestSeqSet(int[] omega,
			boolean underflow) {

		// put appropriate values when t = 0
		LinkedList<State> list = new LinkedList<State>();
		for (int i = 0; i < pi.length; i++) {
			if (underflow) {
				if (pi[i] == 0 || emissionProb[i][omega[0]] == 0) {
					tScoreArray[i] = Double.NEGATIVE_INFINITY;
				}
				else {
					tScoreArray[i] = Math.log(pi[i]) + 
							Math.log(emissionProb[i][omega[0]]);
				}
			}
			else {
				tScoreArray[i] = pi[i] * emissionProb[i][omega[0]];
			}			
			list.add(new State(null, null, i, tScoreArray[i], null));
		}
		tempSeq.add(list);

		// updating the last scores, also update tempBestSeq	
		for (int i = 1; i < omega.length; i++) {
			tScoreArray = bestScoreArray(tScoreArray,
					omega, i, tempSeq, underflow);		
		}
		
		// find the final states
		int max = 0;
		double maxVal = tScoreArray[0];
		for (int i = 0; i < pi.length; i++) {
			if (tScoreArray[i] >= maxVal) {
				max = i;
				maxVal = tScoreArray[i];
			}
		}
		int leftMaxIndex = 0;
		for (int k = 0; k < tScoreArray.length; k++) {
			if (tScoreArray[k] == maxVal) {
				leftMaxIndex = k;
				if (leftMaxIndex == max) {
					max = leftMaxIndex;
				}
				break;
			}
		}
		// back track to create the best sequence
		LinkedList<Integer> predlList = new LinkedList<Integer>();
		LinkedList<Integer> predrList = new LinkedList<Integer>();
		
		// form the predl list
		predlList.add(leftMaxIndex);
		State finalState = tempSeq.getLast().get(leftMaxIndex);
		State predl = finalState.predl;
		while (predl != null) {
			predlList.add(0, predl.index);
			predl = predl.predl;
		}
		
		// form the predr list
		predrList.add(max);
		State finalState2 = tempSeq.getLast().get(max);
		State predr = finalState2.predr;
		while (predr != null) {
			predrList.add(0, predr.index);
			predr = predr.predr;
		}
		// only one sequence if only one distinct final state
		if (max == leftMaxIndex) {
			predlList = predrList;
		}
		
		bestSeqSet.add(predlList);
		bestSeqSet.add(predrList);
		// print the result
		for (int i = 0; i < omega.length; i++) {
			if (i == omega.length - 1) {
				System.out.println(predlList.get(i));
			}
			else {
				System.out.print(predlList.get(i) + ",");
			}
		}
		for (int i = 0; i < omega.length; i++) {
			if (i == omega.length - 1) {
				System.out.println(predrList.get(i));
			}
			else {
				System.out.print(predrList.get(i) + ",");
			}
		}
		return bestSeqSet;		
	}
	
	public double maxScore(int[] omega, boolean underflow) {
		// put appropriate values when t = 0
		LinkedList<State> list = new LinkedList<State>();
		for (int i = 0; i < pi.length; i++) {
			if (underflow) {
				if (pi[i] == 0 || emissionProb[i][omega[0]] == 0) {
					tScoreArray[i] = Double.NEGATIVE_INFINITY;
				}
				else {
					tScoreArray[i] = Math.log(pi[i]) + 
							Math.log(emissionProb[i][omega[0]]);
				}
			}
			else {
				tScoreArray[i] = pi[i] * emissionProb[i][omega[0]];
			}
			list.add(new State(null, null, i, tScoreArray[i], null));
		}
		tempSeq.add(list);

				
		// updating the last scores, also update tempBestSeq	
		for (int i = 1; i < omega.length; i++) {
			tScoreArray = bestScoreArray(tScoreArray,
					omega, i, tempSeq, underflow);		
		}
		
		double maxVal = tScoreArray[0];
		for (int i = 0; i < pi.length; i++) {
			if (tScoreArray[i] >= maxVal) {
				maxVal = tScoreArray[i];
			}
		}
		System.out.println(maxVal);
		return maxVal;
	}
	
	public double[] maxScoreSet(int[] omega, int k,
			boolean underflow) {
		if (k < 0 || k > emissionProb.length 
				* emissionProb[0].length) {
			throw new IllegalArgumentException();
		}
		
		// put appropriate values when t = 0
		LinkedList<State> list = new LinkedList<State>();
		for (int i = 0; i < pi.length; i++) {
			if (underflow) {
				if (pi[i] == 0 || emissionProb[i][omega[0]] == 0) {
					tScoreArray[i] = Double.NEGATIVE_INFINITY;
				}
				else {
					tScoreArray[i] = Math.log(pi[i]) + 
							Math.log(emissionProb[i][omega[0]]);
				}
			}
			else {
				tScoreArray[i] = pi[i] * emissionProb[i][omega[0]];
			}
			list.add(new State(null, null, i, tScoreArray[i], null));
		}
		tempSeq.add(list);

						
		// updating the last scores, also update tempSeq	
		for (int i = 1; i < omega.length; i++) {
			tScoreArray = bestScoreArray(tScoreArray,
					omega, i, tempSeq, underflow);		
		}
		
		Arrays.sort(tScoreArray);
		
		PriorityQueue<Double> queue = 
				new PriorityQueue<Double>(Collections.reverseOrder());
		
		for (int i = 0; i < tScoreArray.length; i++) {
			if (!queue.contains(tScoreArray[i])) {
				queue.add(tScoreArray[i]);
			}
		}
		
		// also add the scores stored in the queue which might have been ignored
		for (int i = 0; i < tempSeq.getLast().size(); i++) {
			while (!tempSeq.getLast().get(i).recordQueue.isEmpty()) {
				double x = tempSeq.getLast().get(i).recordQueue.remove();
				if (!queue.contains(x))
				queue.add(x);
			}
		}
		
		
		/*
		 * if k is greater than the number of distinct probabilities
		 * throw exception
		 */
		if (k > queue.size()) {
			throw new IllegalArgumentException();
		}
		
		
		
		double[] maxScoreArray = new double[k];
		for (int i = 0; i < k; i++) {
			maxScoreArray[i] = queue.remove();
		}
		
		for (int i = 0; i < maxScoreArray.length; i++) {
			System.out.println(maxScoreArray[i]);
		}
		return maxScoreArray;
		
	}
	public static void main(String args[]) {
		
	}
}
