package com.suneo.flag.lib;

import java.util.Arrays;

public class C2279 {
	public int maximumBags(int[] capacity, int[] rocks, int additionalRocks) {
		int n = capacity.length;
		int[] left = new int[n];
		for(int i=0;i<n;i++) {
			left[i]=capacity[i]-rocks[i];
		}
		Arrays.sort(left);
		
		int have = additionalRocks;
		int got = 0;
		
		for(int i=0;i<n&&have>=left[i];i++) {
			have-=left[i];
			++got;
		}
		
		return got;
    }
}