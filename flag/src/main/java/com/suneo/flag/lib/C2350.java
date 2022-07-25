package com.suneo.flag.lib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class C2350 {
	
	public static void main(String[] args) {
		int[] data = new int[]{2,2,2,2,2,2,1,2,2,2,1,1,1,2,2,2,2,1,2,1,1,2,2,2,2,1,1,1,1,2,1,1,2,1,1,2,2,1,1,1,2,1,1,1,2,2,1,2,1,2,1,1,1,1,1,1,2,1,1,1,2,1,1,1,1,1,2,2,1,2,1,2,1,2,2,1,1,2,1,1,1,1,2,2,2,2,1,2,1,1,2,1,2,1,1,2,2,1,2,1,1,2,2,2,1,2,2,1,1,2,2,1,2,1,1,2,1,1,1,1,2,2,1,2,2,1,2,2,2,2,1,1,2,2,2,2,1,1,2,2,1,1,2,1,1,1,1,2,1,1,2,2,2,2,2,2,2,1,2,2,2,2,2,2,1,2,1,1,2,1,2,1,2,2,2,2,2,2,1,1,2,1,2,2,2,2,1,2,1,2,1,2,1,1,1,2,1,1,1,2,1,1,2,2,1,1,1,1,2,2,2,2,1,2,1,1,1,1,1,2,1,1,1,1,2,2,1,1,1,2,2,1,2,1,2,1,1,2,2,2,1,1,2,1,2,1,2,2,1,1,1,1,2,2,2,1,1,2,2,1,1,1,1,1,1,2,1,1,2,2,2,1,1,2,1,2,2,2,2,2};
		System.out.println(new C2350().shortestSequence(data, 2));
	}
	
	public int shortestSequence(int[] rolls, int k) {
        HashMap<Integer, List<Integer>> map = new HashMap<>();
        for(int i=0;i<rolls.length;i++) {
        	int val = rolls[i];
        	if(!map.containsKey(val)) {
        		map.put(val, new ArrayList<>());
        	}
        	map.get(val).add(i);
        }
        
        int rear = -1;
        int accu = 0;
        
        boolean fail = false;
        do {
        	int rr = -1;
        	for(int i=1;i<=k;i++) {
        		int f = -1;
        		if(!map.containsKey(i) || (f=firstGreater(map.get(i), rear)) == -1) {
        			fail = true;
        			break;
        		}
        		if(f>rr) {
        			rr = f;
        		}
        	}
        	if(!fail) {
        		rear = rr;
        		accu++;
        	}
        } while(!fail);
        
        return accu+1;
    }
	
	private static int firstGreater(List<Integer> all, int target) {
		int from = 0;
		int to = all.size() - 1;
		int ret = -1;
		while(from!=to) {
			int mid = (from+to) >> 1;
			int val = all.get(mid);
			if(val > target) {
				ret=val;
				to = mid;
			} else {
				from = mid+1;
			}
		}
		if(all.get(from) > target) {
			ret = all.get(from);
		}
		return ret;
	}
}