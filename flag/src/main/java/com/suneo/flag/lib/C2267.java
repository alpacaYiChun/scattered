package com.suneo.flag.lib;

import java.util.HashSet;

public class C2267 {
	public boolean hasValidPath(char[][] grid) {
		int m = grid.length;
		int n = grid[0].length;
		
		int total = m+n-1;
		if(total%2==1) {
			return false;
		}
		
		if(grid[0][0] == ')') {
			return false;
		}
		
		HashSet<Integer>[][] p = new HashSet[m][n];
		
		int corner = 1;
		p[0][0] = new HashSet<Integer>();
		p[0][0].add(1);
		
		for(int i=1;i<m;i++) {
			corner += go(grid[i][0]);
			if(corner < 0) {
				break;
			}
			p[i][0] = new HashSet<Integer>();
			p[i][0].add(corner);
		}
		
		corner = 1;
		for(int j=1;j<n;j++) {
			corner += go(grid[0][j]);
			if(corner < 0) {
				break;
			}
			p[0][j] = new HashSet<Integer>();
			p[0][j].add(corner);
		}
		
		for(int i=1;i<m;i++) {
			for(int j=1;j<n;j++) {
				HashSet<Integer> here = new HashSet<Integer>();
				if(p[i-1][j]!=null) {
					here.addAll(p[i-1][j]);
				}
				if(p[i][j-1]!=null) {
					here.addAll(p[i][j-1]);
				}
				HashSet<Integer> nw = new HashSet<>();
				int inc = go(grid[i][j]);
				for(Integer now : here) {
					int create = now+inc;
					if(create>=0) {
						nw.add(create);
					}
				}
				if(!nw.isEmpty()) {
					p[i][j]=nw;
				}
			}
		}
		
		return p[m-1][n-1] != null && p[m-1][n-1].contains(0);
    }
	
	public int go(char c) {
		if(c=='(') {
			return 1;
		}
		return -1;
	}
}