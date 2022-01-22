package com.suneo.flag.lib.proxy;

public class Exam implements IExam {
    public String rev(String str) {
    	char[] ar = str.toCharArray();
    	for(int i=0,j=ar.length-1;i<j;i++,j--) {
    		ar[i]^=ar[j];
    		ar[j]^=ar[i];
    		ar[i]^=ar[j];
    	}
    	return new String(ar);
    }
}
