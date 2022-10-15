package com.suneo.flag.lib;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class C2421 {
	public static class Node {
		int id;
		int val;
		public Node(int id, int val) {
			this.id = id;
			this.val = val;
		}
	}
    public int numberOfGoodPaths(int[] vals, int[][] edges) {
        int n = vals.length;
        Node[] nodes = new Node[n];
        for(int i=0;i<n;i++) {
        	nodes[i] = new Node(i, vals[i]);
        }
        Arrays.sort(nodes, (e1, e2)-> e1.val-e2.val);
        HashMap<Integer,Integer> order = new HashMap<Integer, Integer>();
        for(int i=0;i<n;i++) {
        	order.put(nodes[i].id, i);
        }
        HashMap<Integer,List<Integer>> link = new HashMap<>();
        for(int[] e:edges) {
        	int from = order.get(e[0]);
        	int to = order.get(e[1]);
        	if(!link.containsKey(from)) {
        		link.put(from, new ArrayList<Integer>());
        	}
        	if(!link.containsKey(to)) {
        		link.put(to, new ArrayList<Integer>());
        	}
        	link.get(from).add(to);
        	link.get(to).add(from);
        }
        
        int[] root = new int[n];
        for(int i=0;i<n;i++) {
        	root[i] = i;
        }
        
        int testVal = nodes[0].val;
        int start = 0;
        
        int ret = 0;
        
        while(start<n) {
        	int p = start+1;
        	while(p<n&&nodes[p].val == testVal) {
        		p++;
        	}
        	p--;
        	ret += go(root, start, p, link);
        	
        	start = p+1;
        	if(start<n) {
        		testVal = nodes[start].val;
        	}
        }
        
        return ret + n;
    }
    
    private int go(int[] root, int from, int to, HashMap<Integer,List<Integer>> link) {
    	HashMap<Integer, Integer> group = new HashMap<>();
    	
    	for(int i=from;i<=to;i++) {
    		if(link.containsKey(i)) {
    			// scan
    			int min = i;
    			for(Integer outlink:link.get(i)) {
    				if(outlink > i) {
    					continue;
    				}
    				int goRoot = compact(root, outlink);
    				if(goRoot<min) {
    					min = goRoot;
    				}
    			}
    			// link other parts
    			for(Integer outlink:link.get(i)) {
    				root[outlink] = min;
    			}
    			// link self
    			root[i] = min;
    		}
    	}
    	
    	// final link
    	for(int i=from;i<=to;i++) {
    		int t = compact(root, i);
    		root[i] = t;
    		
    		int already = 0;
    		if(group.containsKey(t)) {
    			already = group.get(t);
    		}
    		group.put(t, already+1);
    	}
    	
    	// accumulate
    	int ret = 0;
    	for(Map.Entry<Integer, Integer> e : group.entrySet()) {
    		int count = e.getValue();
    		ret += count*(count-1)/2;
    	}
    	return ret;
    }
    
    private int compact(int[] root, int i) {
    	int now = root[i];
    	do {
    		int t = root[now];
    		if(t==now) {
    			break;
    		}
    		now = t;
    	} while(true);
    	root[i] = now;
    	
    	return now;
    }
}