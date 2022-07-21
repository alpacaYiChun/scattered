package com.suneo.flag.lib;

import java.util.ArrayList;
import java.util.List;

public class C2337 {
	public boolean canChange(String start, String target) {
		List<Integer> a = new ArrayList<>();
		List<Integer> b = new ArrayList<>();
		char[] sr = start.toCharArray();
		char[] tr = target.toCharArray();
		int ii = 0;
		int jj = 0;
		while(true) {
			while(ii<sr.length&&sr[ii]=='_') {
				++ii;
			}
			while(jj<tr.length&&tr[jj]=='_') {
				++jj;
			}
			if(ii<sr.length&&jj<tr.length) {
				if(sr[ii]!=tr[jj]) {
					return false;
				}
				a.add(ii);
				b.add(jj);
				++ii;
                ++jj;
			} else {
				break;
			}
		}
		while(ii<sr.length&&sr[ii]=='_') {
			++ii;
		}
		while(jj<tr.length&&tr[jj]=='_') {
			++jj;
		}
		if(ii<sr.length||jj<tr.length) {
			return false;
		}
		int n = a.size();
		for(int i=0;i<n;i++) {
			int pos1 = a.get(i);
			int pos2 = b.get(i);
			char c = sr[pos1];
			if(c=='L' && pos2>pos1) {
				return false;
			}
			if(c=='R' && pos2<pos1) {
				return false;
			}
		}
		
		return true;
    }
}