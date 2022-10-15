package com.suneo.flag.lib;

public class C2412 {
	public long minimumMoney(int[][] transactions) {
		long maxGainCost = -1;
		long totalLoss = 0;
		long maxBackLoss = 0;
		for(int[] deal : transactions) {
			if(deal[0]>deal[1]) {
				long lossHere = deal[0] - deal[1];
				totalLoss += lossHere;
				if(deal[1] > maxBackLoss) {
					maxBackLoss = deal[1];
				}
				
			} else {
				if(deal[0]>maxGainCost) {
					maxGainCost = deal[0];
				}
			}
		}
				
		long max1 = maxGainCost + totalLoss;
		long max2 = totalLoss + maxBackLoss;
		
		return Math.max(max1, max2);
    }
}