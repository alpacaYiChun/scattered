package com.suneo.flag.lib;

import java.util.TreeSet;

public class C2336 {
	int extend = 0;
	
	TreeSet<Integer> dot = new TreeSet<>();
	
	public C2336() {
        
    }
    
    public int popSmallest() {
    	if(dot.isEmpty()) {
    		extend++;
    		return extend;
    	}
    	
    	return dot.pollFirst();
    }
    
    public void addBack(int num) {
        if(num<=extend) {
        	dot.add(num);
        }
    }
}