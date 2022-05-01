package com.suneo.flag.lib;

import java.util.HashMap;

public class C2260 {
	 public int minimumCardPickup(int[] cards) {
		 int ret = -1;
	     HashMap<Integer,Integer> last = new HashMap<>();
	     for(int i=0;i<cards.length;i++) {
	    	 int c = cards[i];
	    	 if(last.containsKey(c)) {
	    	 	 int dist = i-last.get(c)+1;
	    	 	 if(ret==-1||dist<ret) {
	    	 		 ret=dist;
	    	 	 }
	    	 }
	    	 last.put(c, i);
	     }
	     return ret;
	 }
}
