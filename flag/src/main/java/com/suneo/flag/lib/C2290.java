package com.suneo.flag.lib;

import java.util.PriorityQueue;

public class C2290 {
	private static int[][] dirs = new int[][] {{0,1},{1,0},{0,-1},{-1,0}};
	
	private static class State {
		int i;
		int j;
		int step;
		
		public State(int i, int j, int step) {
			this.i = i;
			this.j = j;
			this.step = step;
		}
	}
	
	public int minimumObstacles(int[][] grid) {   
        int m = grid.length;
        int n = grid[0].length;
        
        PriorityQueue<State> queue = new PriorityQueue<>((e1, e2) -> e1.step - e2.step);
        queue.add(new State(0,0,0));
        
        int[][] map = new int[m][n];
        for(int i=0;i<m;i++) {
        	for(int j=0;j<n;j++) {
        		map[i][j] = Integer.MAX_VALUE;
        	}
        }
        map[0][0] = 0;
        
        while(!queue.isEmpty()) {
        	State fetch = queue.poll();
        	if(fetch.i==m-1&&fetch.j==n-1) {
        		return fetch.step;
        	}
        	if(map[fetch.i][fetch.j]<fetch.step) {
        		continue;
        	}
        	for(int d=0;d<4;d++) {
        		int ii = fetch.i+dirs[d][0];
        		int jj= fetch.j+dirs[d][1];
        		if(ii<0||jj<0||ii>=m||jj>=n) {
        			continue;
        		}
        		
        		int attempt = grid[ii][jj] + fetch.step;
        		if(map[ii][jj] <= attempt) {
        			continue;
        		}
        		
        		queue.add(new State(ii,jj,attempt));
        		map[ii][jj] = attempt;
        	}
        }
        
        return -1;
    }
}
