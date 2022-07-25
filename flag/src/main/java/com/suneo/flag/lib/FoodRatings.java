package com.suneo.flag.lib;

import java.util.HashMap;
import java.util.TreeMap;
import java.util.TreeSet;

public class FoodRatings {

	HashMap<String, HashMap<Integer, TreeSet<String>>> names = new HashMap<>();
	HashMap<String, TreeMap<Integer,Integer>> p = new HashMap<>();
	HashMap<String, String> foodin = new HashMap<>();
	HashMap<String, Integer> rate = new HashMap<>();
	
    public FoodRatings(String[] foods, String[] cuisines, int[] ratings) {
        int n = foods.length;
        for(int i=0;i<n;i++) {
        	String c = cuisines[i];
        	String food = foods[i];
        	int r = ratings[i];
        	
        	foodin.put(food, c);
        	rate.put(food, ratings[i]);
        	
        	if(!p.containsKey(c)) {
        		p.put(c, new TreeMap<Integer,Integer>((e1,e2)->e2-e1));
        	}
        	int already = 0;
        	if(p.get(c).containsKey(r)) {
        		already = p.get(c).get(r);
        	}
        	p.get(c).put(r,already+1);
        	
        	if(!names.containsKey(c)) {
        		names.put(c, new HashMap<>());
        	}
        	if(!names.get(c).containsKey(r)) {
        		names.get(c).put(r, new TreeSet<String>());
        	}
        	names.get(c).get(r).add(food);
        }       
    }
    
    public void changeRating(String food, int newRating) {
        int old = rate.get(food);
        String where = foodin.get(food);
        rate.put(food, newRating);
        
        int already = p.get(where).get(old);
        --already;
        if(already==0) {
        	p.get(where).remove(old);
        } else {
        	p.get(where).put(old, already);
        }
        already = 0;
        if(p.get(where).containsKey(newRating)) {
        	already = p.get(where).get(newRating);
        }
        p.get(where).put(newRating, already+1);
        
        names.get(where).get(old).remove(food);
        if(!names.get(where).containsKey(newRating)) {
        	names.get(where).put(newRating, new TreeSet<String>());
        }
        names.get(where).get(newRating).add(food);
    }
    
    public String highestRated(String cuisine) {
        int top = p.get(cuisine).firstEntry().getKey();
        return names.get(cuisine).get(top).first();
    }
}