package com.suneo.flag.lib;

public class C2384 {
	public String largestPalindromic(String num) {
		int[] dig = new int[10];
		char[] ar = num.toCharArray();
		
		for(int i=0;i<ar.length;i++) {
			int index = ar[i]-'0';
			dig[index]++;
		}
		
		int[] use = new int[10];
		int single = -1;
		for(int i=0;i<10;i++) {
			use[i] = dig[i] - dig[i]%2;
		}
		
		for(int i=9;i>=0;i--) {
			if(dig[i]%2==1) {
				single = i;
				break;
			}
		}
		
		StringBuilder sb = new StringBuilder();
		boolean started = false;
		
		for(int i=9;i>=0;i--) {
			if(!started && i==0) {
				continue;
			}
			
			int count = use[i] / 2;
			
			if(count>0) {
				for(int k=0;k<count;k++) {
					sb.append(i);
				}
				started = true;
			}
		}
		
		StringBuilder sb2 = new StringBuilder();
		sb2.append(sb.toString());
		if(single!=-1) {
			sb2.append(single);
		}
		sb2.append(rev(sb.toString()));
		
		String fin = sb2.toString();
		if(fin.length()==0) {
			return "0";
		}
		
		return fin;
    }
	
	private static String rev(String p) {
		char[] ar = p.toCharArray();
		for(int i=0,j=ar.length-1;i<j;i++,j--) {
			ar[i]^=ar[j];
			ar[j]^=ar[i];
			ar[i]^=ar[j];
		}
		return new String(ar);
	}
}