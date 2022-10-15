package com.suneo.flag.lib;

import java.util.LinkedList;

public class C2398 {
	public static class MStack {
		LinkedList<int[]> list = new LinkedList<>();
		
		public void push(int id, int k) {
			while(!list.isEmpty() && list.getLast()[1] < k) {
				list.removeLast();
			}
			list.add(new int[] {id, k});
		}
		
		public void progress(int id) {
			if(!list.isEmpty() && list.getFirst()[0] == id) {
				list.removeFirst();
			}
		}
		
		public int getMax() {
			if(!list.isEmpty()) {
				return list.getFirst()[1];
			}
			return 0;
		}
	}
	
	public int maximumRobots(int[] chargeTimes, int[] runningCosts, long budget) {
		MStack stack = new MStack();
		int n = chargeTimes.length;
		long sum = 0;
		int k = 0;
		int start = -1;
		
		int max = 0;
		
		for(int i=0;i<n;i++) {
			sum+=runningCosts[i];
			k++;
			stack.push(i, chargeTimes[i]);
			
			do {
				long examine = k*sum + stack.getMax();
				if(examine <= budget) {
					if(k>max) {
						max = k;
					}
					break;
				}
				++start;
				sum -= runningCosts[start];
				k--;
				stack.progress(start);
			} while(true);
		}
		
		return max;
    }
}