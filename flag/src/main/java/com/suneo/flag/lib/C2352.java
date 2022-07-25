package com.suneo.flag.lib;

import java.util.HashMap;
import java.util.HashSet;

public class C2352 {
	public static class Trie {
		HashMap<Integer,Trie> map = new HashMap<>();
		int cols;
		int rows;
		public Trie go(int p, boolean isRow) {
			if(!map.containsKey(p)) {
				map.put(p, new Trie());
			}
			Trie c = map.get(p);
			if(isRow) {
				c.rows++;
			} else {
				c.cols++;
			}
			return c;
		}
		
		public static Trie insert(Trie root, int[] vec, boolean isRow) {
			Trie now = root;
			for(int i=0;i<vec.length;i++) {
				now = now.go(vec[i], isRow);
			}
			
			return now;
		}	
	}
	public int equalPairs(int[][] grid) {
		int n = grid.length;
		Trie root = new Trie();
		int sum = 0;
		HashSet<Trie> set = new HashSet<>();
		for(int i=0;i<n;i++) {
			Trie t = Trie.insert(root, grid[i], true);
			set.add(t);
		}
		
		for(int j=0;j<n;j++) {
			Trie  now = root;
			for(int i=0;i<n;i++) {
				now = now.go(grid[i][j], false);
			}
			set.add(now);
		}
		
		for(Trie t:set) {
			sum+=t.cols*t.rows;
		}
		
		return sum;
    }
}