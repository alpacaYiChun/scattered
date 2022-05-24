package com.suneo.flag.lib;

import java.util.Arrays;

public class C2280 {
	public int minimumLines(int[][] stockPrices) {
		int n = stockPrices.length;
		if (n < 2) {
			return 0;
		}
		
		Arrays.sort(stockPrices, (e1,e2) -> e1[0] - e2[0]);
		
		Step prev = null;
		int seg = 0;
		
		for(int i=1;i<n;i++) {
			Step now = new Step(stockPrices[i][0]-stockPrices[i-1][0], stockPrices[i][1]-stockPrices[i-1][1]);
			if(prev == null) {
				prev = now;
				++seg;
			} else {
				if(!now.equals(prev)) {
					prev = now;
					++seg;
				}
			}
		}
		
		return seg;
    }
	
	public static class Step{
		long daysDiff;
		long priceDiff;
		
		public Step(long daysDiff, long priceDiff) {
			this.daysDiff = daysDiff;
			this.priceDiff = priceDiff;
		}
		
		@Override
		public int hashCode() {
			return (int)priceDiff;
		}
		
		@Override
		public boolean equals(Object o) {
			if(!(o instanceof Step)) {
				return false;
			}
			
			if (o == this) {
				return true;
			}
						
			Step oo = (Step)o;
			return this.daysDiff*oo.priceDiff==this.priceDiff*oo.daysDiff;
		}
	}
}
