package com.suneo.flag.lib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class C2316 {
	public long countPairs(int n, int[][] edges) {
		HashMap<Integer,List<Integer>> e = new HashMap<>();
		for(int[] ee : edges) {
			if(!e.containsKey(ee[0])) {
				e.put(ee[0], new ArrayList<>());
			}
			if(!e.containsKey(ee[1])) {
				e.put(ee[1], new ArrayList<>());
			}
			e.get(ee[0]).add(ee[1]);
			e.get(ee[1]).add(ee[0]);
		}
		List<Integer> groups = new ArrayList<>();
        boolean[] step = new boolean[n];
        for(int i=0;i<n;i++) {
        	if(step[i]) {
        		continue;
        	}
        	LinkedList<Integer> queue = new LinkedList<>();
        	queue.add(i);
        	step[i]=true;
        	int count = 1;
        	while(!queue.isEmpty()) {
        		int fetch=queue.poll();
        		if(e.containsKey(fetch)) {
        			for(Integer out:e.get(fetch)) {
        				if(step[out]) {
        					continue;
        				}
        				queue.add(out);
        				step[out]=true;
        				++count;
        			}
        		}
        	}
        	groups.add(count);
        }
        
        long sum = 0;
        long[] tail = new long[groups.size()];
        tail[0] = groups.get(0);
        for(int i=1;i<groups.size();i++) {
        	tail[i]=tail[i-1]+groups.get(i);
        }
        
        for(int i=1;i<tail.length;i++) {
        	sum+= tail[i-1]*groups.get(i);
        }
        
        return sum;
     }
}