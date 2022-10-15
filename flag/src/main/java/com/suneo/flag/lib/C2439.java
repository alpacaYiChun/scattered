package com.suneo.flag.lib;

public class C2439 {
	public int minimizeArrayValue(int[] nums) {
		int n = nums.length;
		int max = 0;
		int min = Integer.MAX_VALUE;
		for(int i=0;i<n;i++) {
			if(nums[i] > max) {
				max = nums[i];
			}
			if(nums[i] < min) {
				min = nums[i];
			}
		}
		int ret = max;
		while(max != min) {
			int mid = (max + min)/2;
			if(ok(nums, mid)) {
				max = mid;
				ret = mid;
			} else {
				min = mid + 1;
			}
		}
		if(ok(nums, max)) {
			ret = max;
		}
		
		return ret;
    }
	
	private boolean ok(int[] nums, int limit) {
		int n = nums.length;
		long[] c = new long[n];
		for(int i=0;i<n;i++) {
			c[i] = nums[i];
		}
		for(int i=n-1;i>0;i--) {
			if(c[i] > limit) {
				long sur = c[i] - limit;
				c[i] = limit;
				c[i-1] += sur;
			}
		}
		
		return c[0] <= limit;
	}
}