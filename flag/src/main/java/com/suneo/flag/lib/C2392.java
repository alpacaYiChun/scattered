package com.suneo.flag.lib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class C2392 {
	public int[][] buildMatrix(int k, int[][] rowConditions, int[][] colConditions) {
		List<Integer> rowWise = sort(rowConditions, k);
		if(rowWise == null) {
			return new int[0][0];
		}
		
		List<Integer> colWise = sort(colConditions, k);
		if(colWise == null) {
			return new int[0][0];
		}
		
		int[][] ret = new int[k][k];
		
		int[][] index = new int[k][2];
		for(int i=0;i<rowWise.size();i++) {
			index[rowWise.get(i)][0] = i;
		}
		for(int i=0;i<colWise.size();i++) {
			index[colWise.get(i)][1] = i;
		}
		
		for(int i=0;i<k;i++) {
			int ii = index[i][0];
			int jj = index[i][1];
			ret[ii][jj] = i+1;
		}
		
		return ret;
    }
	
	private List<Integer> sort(int[][] c, int n) {
		HashMap<Integer, List<Integer>> outlink = new HashMap<Integer, List<Integer>>();
		int[] in = new int[n];
		for(int[] link : c) {
			int from = link[0]-1;
			int to = link[1]-1;
			if(!outlink.containsKey(from)) {
				outlink.put(from, new ArrayList<Integer>());
			}
			outlink.get(from).add(to);
			in[to]++;
		}
		LinkedList<Integer> queue = new LinkedList<>();
		for(int i=0;i<n;i++) {
			if(in[i]==0) {
				queue.add(i);
			}
		}
		List<Integer> ret = new  ArrayList<>();
		while(!queue.isEmpty()) {
			int fetch = queue.poll();
			ret.add(fetch);
			if(outlink.containsKey(fetch)) {
				for(Integer o : outlink.get(fetch)) {
					in[o]--;
					if(in[o]==0) {
						queue.add(o);
					}
				}
			}
		}
		
		for(int i=0;i<n;i++) {
			if(in[i]!=0) {
				return null;
			}
		}
		
		return ret;
	}
}
