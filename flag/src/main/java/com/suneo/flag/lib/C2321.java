package com.suneo.flag.lib;

public class C2321 {
	public int maximumsSplicedArray(int[] nums1, int[] nums2) {
		int n =nums1.length;
		int[] p1 = new int[n];
		p1[n-1]=nums2[n-1]-nums1[n-1]>0?nums2[n-1]-nums1[n-1]:0;
		for(int i=n-2;i>=0;i--) {
			int d = nums2[i]-nums1[i]+p1[i+1];
			p1[i]=d>0?d:0;
		}
		int[] p2 = new int[n];
		p2[n-1]=nums1[n-1]-nums2[n-1]>0?nums1[n-1]-nums2[n-1]:0;
		for(int i=n-2;i>=0;i--) {
			int d = nums1[i]-nums2[i]+p2[i+1];
			p2[i]=d>0?d:0;
		}
		int s1 = 0;
		int s2 = 0;
		int m1 = 0;
		int m2 = 0;
		for(int i=0;i<n;i++) {
			s1+=nums1[i];
			s2+=nums2[i];
			m1=Math.max(m1, p1[i]);
			m2=Math.max(m2, p2[i]);
		}
		return Math.max(m1+s1, m2+s2);
    }
}