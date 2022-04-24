package com.suneo.flag.lib;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class C2250 {
	public int[] countRectangles(int[][] rectangles, int[][] points) {
		HashMap<Integer,List<Integer>> yIndex = new HashMap<Integer,List<Integer>>();
        int maxY = -1;
		for(int i=0;i<rectangles.length;i++) {
			int y = rectangles[i][1];
            if(y>maxY) {
                maxY=y;
            }
			if(!yIndex.containsKey(y)) {
				yIndex.put(y, new ArrayList<Integer>());
			}
			yIndex.get(y).add(rectangles[i][0]);
		}
		for(Map.Entry<Integer, List<Integer>> e:yIndex.entrySet()) {
			Collections.sort(e.getValue());
		}
		
		int[] ret = new int[points.length];
		int l = 0;
		for(int[] point : points) {
			int sum = 0;
			for(int j=point[1];j<=maxY;j++) {
				if(yIndex.containsKey(j)) {
                    List<Integer> sub = yIndex.get(j);
					int pick = firstGreater(sub, point[0]);
					sum+=sub.size()-pick;
				}
				
			}
            ret[l++]=sum;
		}
		
		return ret;
    }
	
	private int firstGreater(List<Integer> a, int lower) {
		int from = 0;
		int to = a.size() - 1;
		int pick = a.size();
		while(from!=to) {
			int mid = (from+to) >> 1;
			int at = a.get(mid);
			if(at>=lower) {
				to=mid;
				pick = mid;
			} else {
				from=mid+1;
			}
		}
		if(a.get(from)>=lower) {
			pick = from;
		}
		return pick;
	}
}
