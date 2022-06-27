package com.suneo.flag.lib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class C2318 {
	public static void main(String[] args) {
		C2318 c = new C2318();
		int rs = c.distinctSequences(4);
		System.out.println(rs);
	}
	
	public static final long BIG = 1000000007;
	private static class S {
		int x1;
		int x2;
		
		public S(int x1, int x2) {
			this.x1=x1;
			this.x2=x2;
		}
		
		@Override
		public int hashCode() {
			return (x1<<3) + x2; 
		}
		
		@Override
		public boolean equals(Object o) {
			if(o == null) {
				return false;
			}
			if(!(o instanceof S)) {
				return false;
			}
			if(o==this) {
				return true;
			}
			S another = (S)o;
			return x1==another.x1 && x2==another.x2;
		}
	}
	public boolean coPrime(int a, int b) {
		int x = a;
		int y = b;
		int z = a%b;
		while(z!=0) {
			x=y;
			y=z;
			z=x%y;
		}
		return y==1;
	}
    public int distinctSequences(int n) {
    	if(n==1) {
    		return 6;
    	}
        List<S> all= new ArrayList<S>(36);
        for(int i=1;i<=6;i++) {
        	for(int j=1;j<=6;j++) {
        		if(i!=j&&coPrime(i,j)) {
        			all.add(new S(i,j));
        		}
        	}
        }
        int m = all.size();
        if(n==2) {
            return m;
        }
        HashMap<Integer,List<Integer>> comply = new HashMap<>();
        for(int i=0;i<all.size();i++) {
        	for(int j=0;j<all.size();j++) {
        		// front
        		S s1 = all.get(i);
        		// rear
        		S s2 = all.get(j);
        		if(s2.x1!=s1.x1&&s2.x1!=s1.x2&&s2.x2!=s1.x2&&coPrime(s2.x1,s1.x2)) {
        			if(!comply.containsKey(j)) {
        				comply.put(j, new ArrayList<>());
        			}
        			comply.get(j).add(i);
        		}
        	}
        }
        int[][] op = new int[n+1][m];
        for(int i=0;i<m;i++) {
        	op[2][i]=1;
        }
        for(int i=0;i<m;i++) {
        	S s = all.get(i);
        	int count = 0;
        	for(int k=1;k<=6;k++) {
        		if(k!=s.x1&&k!=s.x2&&coPrime(k,s.x1)) {
        			++count;
        		}
        	}
        	op[3][i]=count;
        }
        for(int i=4;i<=n;i++) {
        	for(int j=0;j<m;j++) {
        		long p = 0;
        		if(comply.containsKey(j)) {
        			for(Integer k:comply.get(j)) {
        				p+=op[i-2][k];
        				p%=BIG;
        			}
        		}
        		op[i][j]=(int)p;
        	}
        }
        long fin = 0;
        for(int j=0;j<m;j++) {
        	fin+=op[n][j];
        	fin%=BIG;
        }
        
        return (int)fin;
    }
}