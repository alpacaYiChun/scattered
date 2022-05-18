package com.suneo.flag.lib;

public class C2270 {
	public int waysToSplitArray(int[] nums) {
		long sum = 0;
        for(int i=0;i<nums.length;i++) {
        	sum+=nums[i];
        }
        int ret = 0;
        long part = 0;
        for(int i=0;i<nums.length-1;i++) {
        	part+=nums[i];
        	long left = sum-part;
        	if(part>=left) {
        		++ret;
        	}
        }
        
        return ret;
    }
}
