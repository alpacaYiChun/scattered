package com.suneo.flag.lib;

import java.util.Stack;

public class C2334 {
	public int validSubarraySize(int[] nums, int threshold) {
		int n = nums.length;
		int[] back = new int[n];
		int[] front = new int[n];
		Stack<Integer> s= new Stack<>();
		for(int i=0;i<n;i++) {
			while(!s.isEmpty()&&nums[s.peek()]>=nums[i]) {
				s.pop();
			}
			if(s.isEmpty()) {
				back[i] = -1;
			} else {
				back[i] = s.peek();
			}
			s.push(i);
		}
		s.clear();
		for(int i=n-1;i>=0;i--) {
			while(!s.isEmpty()&&nums[s.peek()]>=nums[i]) {
				s.pop();
			}
			if(s.isEmpty()) {
				front[i] = n;
			} else {
				front[i] = s.peek();
			}
			s.push(i);
		}
		long th = threshold;
		for(int i=0;i<n;i++) {
			long span = front[i]-back[i]-1;
			long test = nums[i] * span;
			if(test > th) {
				return (int)span;
			}
		}
		return -1;
    }
}