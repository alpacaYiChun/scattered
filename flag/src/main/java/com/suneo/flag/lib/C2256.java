package com.suneo.flag.lib;

public class C2256 {
    public int minimumAverageDifference(int[] nums) {
    	long sum = 0;
    	for(int i=0;i<nums.length;i++) {
    		sum+=nums[i];
    	}
    	long front = 0;
    	int ret = -1;
    	long min = Long.MAX_VALUE;
    	for(int i=0;i<nums.length;i++) {
    		front+=nums[i];
    		long rear = sum-front;
    		long avgFront = front/(i+1);
    		long avgRear = rear == 0? 0: rear/(nums.length - i -1);
    		long abs = Math.abs(avgFront - avgRear);
    		if(abs<min) {
    			min = abs;
    			ret = i;
    		}
    	}
    	return ret;
    }
}
