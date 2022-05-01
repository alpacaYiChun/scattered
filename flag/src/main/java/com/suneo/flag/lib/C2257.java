package com.suneo.flag.lib;

public class C2257 {
    public int countUnguarded(int m, int n, int[][] guards, int[][] walls) {
    	int[][] map = new int[m][n];
    	for(int[] w:walls) {
    		map[w[0]][w[1]]=-1;
    	}

        for(int[] g:guards) {
        	if((map[g[0]][g[1]]&1)==0) {	        	
	        	//horizon
	        	for(int j=g[1];j>=0;j--) {
	        		if(map[g[0]][j]==-1) {
	        			break;
	        		}
	        		map[g[0]][j] |= 1;
	        	}
	        	for(int j=g[1]+1;j<n;j++) {
	        		if(map[g[0]][j]==-1) {
	        			break;
	        		}
	        		map[g[0]][j] |= 1;
	        	}
        	}
        	if((map[g[0]][g[1]]&2)==0) {
	        	// vertical
	        	for(int i=g[0];i>=0;i--) {
	        		if(map[i][g[1]]==-1) {
	        			break;
	        		}
	        		map[i][g[1]] |= 2;
	        	}
	        	for(int i=g[0]+1;i<m;i++) {
	        		if(map[i][g[1]]==-1) {
	        			break;
	        		}
	        		map[i][g[1]] |= 2;
	        	}
        	}
        }
        
        int ret = 0;
        for(int i=0;i<m;i++) {
        	for(int j=0;j<n;j++) {
        		if(map[i][j]==0) {
        			++ret;
        		}
        	}
        }
        
        return ret;
    }
}
