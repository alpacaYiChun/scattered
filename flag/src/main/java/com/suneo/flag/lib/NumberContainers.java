package com.suneo.flag.lib;

import java.util.HashMap;
import java.util.TreeSet;

public class NumberContainers {
	HashMap<Integer,TreeSet<Integer>> pos = new HashMap<>();
	HashMap<Integer, Integer> map = new HashMap<>();
	
	public NumberContainers() {
        
    }
    
    public void change(int index, int number) {
        if(map.containsKey(index)) {
        	int old = map.get(index);
        	pos.get(old).remove(index);
        }
        
        map.put(index, number);
        if(!pos.containsKey(number)) {
        	pos.put(number, new TreeSet<Integer>());
        }
        pos.get(number).add(index);
    }
    
    public int find(int number) {
        if(pos.containsKey(number) && pos.get(number).size()>0) {
        	return pos.get(number).first();
        }
        
        return -1;
    }
}