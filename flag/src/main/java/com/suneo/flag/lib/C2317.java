package com.suneo.flag.lib;

public class C2317 {
    public int maximumXOR(int[] nums) {
    	int[] count = new int[32];
    	for(int i=0;i<nums.length;i++) {
    		int val = nums[i];
    		for(int b=0;b<32;b++) {
    			int bit = (1<<b);
    			if((val&bit)!=0) {
    				count[b]++;
    			}
    		}
    	}
    	
    	for(int i=0;i<32;i++) {
    		if(count[i]>0&&count[i]%2==0) {
    			count[i]--;
    		}
    	}
    	
    	int ret = 0;
    	for(int i=0;i<32;i++) {
    		if(count[i]%2==1) {
    			ret|=(1<<i);
    		}
    	}
    	
    	return ret;
    }
}