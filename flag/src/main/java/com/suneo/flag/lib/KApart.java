package com.suneo.flag.lib;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class KApart {
	public String rearrangeString(String s, int k) {
		if(k<2) {
			return s;
		}
		char[] ar = s.toCharArray();
		int[] count = new int[26];
		for(int i=0;i<ar.length;i++) {
			count[ar[i]-'a']++;
		}
		int max=0;
		for(int i=0;i<26;i++) {
			if(count[i]>max) {
				max=count[i];
			}
		}
		List<Character> maxChars = new ArrayList<Character>();
		for(int i=0;i<26;i++) {
			if(count[i]==max) {
				maxChars.add((char)('a'+i));
			}
		}
		
		int leftOver = ar.length-maxChars.size()*max;
		
		int gapToFillAtOnePlace = (k-1)-(maxChars.size()-1);
		if(leftOver<(max-1)*gapToFillAtOnePlace) {
			return "";
		}
		
		List<List<Character>> boxes = new ArrayList<List<Character>>();
		for(int i=0;i<max-1;i++) {
			List<Character> start = new ArrayList<Character>();
			for(Character c:maxChars) {
				start.add(c);
			}
			boxes.add(start);
		}
		
		List<int[]> others = new ArrayList<int[]>();
		for(int i=0;i<26;i++) {
			if(count[i]!=max) {
				others.add(new int[] {count[i],i});
			}
		}
		Collections.sort(others, new Comparator<int[]>() {
			public int compare(int[] a, int[] b) {
				return b[0]-a[0];
			}
		});
		
		int at=0;
		for(int[] o:others) {
			char p = (char)('a'+o[1]);
			for(int j=0;j<o[0];j++) {
				boxes.get(at).add(p);
				at=(at+1)%(max-1);
			}
		}
		
	    StringBuilder sb = new StringBuilder();
	    for(List<Character> list:boxes) {
	    	for(Character c:list) {
	    		sb.append(c);
	    	}
	    }
		
	    for(Character c:maxChars) {
	    	sb.append(c);
	    }
	    
	    return sb.toString();
	}
}
