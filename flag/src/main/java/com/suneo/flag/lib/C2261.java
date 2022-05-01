package com.suneo.flag.lib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class C2261 {
	public static class Trie {
		boolean fin;
		HashMap<Integer,Trie> children;
		
		public Trie go(int t) {
			if(children==null) {
				children = new HashMap<Integer,Trie>();
			}
			if(!children.containsKey(t)) {
				children.put(t, new Trie());
			}
			return children.get(t);
		}
		
		public void mark() {
			this.fin = true;
		}
	}
	
	
	public int countDistinct(int[] nums, int k, int p) {
		int ret = 0;
		
		Trie root = new Trie();
		
        List<Integer> all = new ArrayList<>();
        int t = -1;
        for(int i=0;i<nums.length;i++) {
        	if(nums[i]%p==0) {
        		all.add(i);
        		++t;
        	}
        	
        	int seek = t-k;
        	int seekIndex = 0;
        	if(seek>=0) {
        		seekIndex = all.get(seek) + 1;
        	}
        	
        	Trie here = root;
        	for(int j=i;j>=seekIndex;j--) {
        		here = here.go(nums[j]);
        		if(!here.fin) {
        			ret++;
        		}
        		here.mark();
        	}
        }
        
        return ret;
        
    }
}
