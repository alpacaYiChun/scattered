package com.suneo.flag.lib;

public class C2226 {
	public int maximumCandies(int[] candies, long k) {
        long sum = 0;
        for(int i=0;i<candies.length;i++) {
        	sum+=candies[i];
        }
        int ideal = (int)(sum/k);
        int from = 1;
        int to = ideal;
        int ret = 0;
        while(from<to) {
        	int mid = (from+to) >> 1;
        	if(ok(candies,mid,k)) {
        		ret = mid;
        		from=mid+1;
        	} else {
        		to = mid;
        	}
        }
        
        if(ok(candies, from, k)) {
        	ret = from;
        }
        
        return ret;
        
    }
	private boolean ok(int[] candies, int pile, long k) {
		long sum = 0;
		for(int i=0;i<candies.length;i++) {
			int get = candies[i]/pile;
			sum+=get;
		}
		return sum >= k;
	}
}
