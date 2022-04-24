package com.suneo.flag.lib;

import java.util.HashSet;
import java.util.Stack;

public class NQ {
	public int totalNQueens(int n) {
        Stack<int[]> s = new Stack<>();
        Collect c = new Collect();
        go(s,n,c);
        return c.get();
    }
	
	private static void go(Stack<int[]> s, int n, Collect c) {
		int now = s.size();
		if(now == n) {
			c.inc();
			return;
		}
		int target = now;
		HashSet<Integer> rows = new HashSet<>();
		HashSet<Integer> cols = new HashSet<>();
		HashSet<Integer> sum = new HashSet<>();
		HashSet<Integer> ded = new HashSet<>();
		for(int[] already : s) {
			int rr = already[0];
			int cc = already[1];
			rows.add(rr);
			cols.add(cc);
			sum.add(rr+cc);
			ded.add(rr-cc);
		}
		for(int j=0;j<n;j++) {
			int newRow = target;
			int newCol = j;
			int newSum = newRow+newCol;
			int newDed = newRow-newCol;
			if(rows.contains(newRow)||cols.contains(newCol)||sum.contains(newSum)||ded.contains(newDed)) {
				continue;
			}
			s.push(new int[] {newRow,newCol});
			go(s,n,c);
			s.pop();
		}
	}
	
	private static class Collect {
		int c = 0;
		
		public void inc() {
			++c;
		}
		
		public int get() {
			return c;
		}
	}
}
