package com.suneo.flag.lib;

import java.util.HashSet;

public class C2275 {
	public int largestCombination(int[] candidates) {
        int n = candidates.length;
        HashSet<Integer>[] hit = new HashSet[32];
        for(int i=0;i<32;i++) {
            hit[i]=new HashSet<Integer>();
        }
        for(int i=0;i<n;i++) {
            for(int j=0;j<32;j++) {
                if((candidates[i]&(1<<j))!=0) {
                    hit[j].add(i);
                }
            }
        }
        int max = -1;
        for(HashSet<Integer> p:hit) {
            if(p.size()>max) {
                max = p.size();
            }
        }
        return max;
    }
}
