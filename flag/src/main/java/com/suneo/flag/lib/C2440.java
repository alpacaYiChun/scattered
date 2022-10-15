package com.suneo.flag.lib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class C2440 {
		public int componentValue(int[] nums, int[][] edges) {
		int n = nums.length;
		HashMap<Integer, List<Integer>> outlink = new HashMap<>();
		for (int[] e : edges) {
			int from = e[0];
			int to = e[1];
			if(!outlink.containsKey(from)) {
				outlink.put(from, new ArrayList<Integer>());
			}
			if(!outlink.containsKey(to)) {
				outlink.put(to, new ArrayList<Integer>());
			}
			outlink.get(from).add(to);
			outlink.get(to).add(from);
		}
		
		int total = 0;
		for(int i=0;i<n;i++) {
			total += nums[i];
		}
		
		for(int k=1;k<=total;k++) {
			if(total%k!=0) {
				continue;
			}
			int target = total / k;
			int[] rs = dfs(-1, 0, k, nums, outlink);
			if(rs[1] == target) {
				return target-1;
			}
		}
		
		return 0;
	}
	
	public int[] dfs(int parent, int start, int aim, int[] nums, HashMap<Integer, List<Integer>> outlink) {
		int sum = nums[start];
		int totalAimed = 0;
		
	    if(outlink.containsKey(start)) {
	        for(Integer link : outlink.get(start)) {
	            if(link == parent) {
	                continue;
	            }
	            int[] part = dfs(start, link, aim, nums, outlink);
	            if(part[2] == 1) {
                    return new int[]{-1,-1,1};
                }
	            sum += part[0];
	            totalAimed += part[1];
	        }
	    }
		int leftOver = sum - aim * totalAimed;
		if(leftOver == aim) {
			totalAimed++;
		}
		
		int fail = 0;
        if(leftOver > aim) {
            fail = 1;
        }
		
	    //System.out.println(start+","+aim+","+sum+","+totalAimed);
		return new int[] {sum, totalAimed, fail};
	}
}