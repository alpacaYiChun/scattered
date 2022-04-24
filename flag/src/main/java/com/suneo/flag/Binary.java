package com.suneo.flag;

public class Binary {
	
	
	public static int getIndex(int[] array, int elem) {
		if(array.length==0) {
			return -1;
		}
		int from = 0;
		int to = array.length - 1;
		
		while(from!=to) {
			
			int mid = (from+to) >> 1;
			if(array[mid]==elem) {
				return mid;
			}
			// 0 1
			// 0-0, 1-1
			if(array[mid]>elem) {
				to = mid;
			} else {
				from=mid+1;
			}
		}
		
		if(array[from]==elem) {
			return from;
		}
		
		return -1;
	}
}
