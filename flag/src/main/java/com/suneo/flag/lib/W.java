package com.suneo.flag.lib;

import java.util.*;

public class W {

public static void main(String[] args) {
    String test = "attract";
    find(test);    
    
}

public static void find(String str) {
    char[] ar = str.toCharArray();
    int n = ar.length;
    HashMap<Character,List<Integer>> indexes = new HashMap<Character, List<Integer>>();
    
    for(int i=0;i<n;i++){
        if(!indexes.containsKey(ar[i])) {
            indexes.put(ar[i],new ArrayList<Integer>());
        }
        indexes.get(ar[i]).add(i);
    }
    
    for(Map.Entry<Character,List<Integer>> e: indexes.entrySet()) {
        System.out.println(e.getKey());
        for(Integer idx:e.getValue()){
            System.out.println("at:"+idx);
        }
    }
    
    HashMap<Character,Integer> progress = new HashMap<Character,Integer>();
    List<List<String>> gains = new ArrayList<List<String>>(n);
    
    for(int i=0;i<n;i++){
        gains.add(new ArrayList<String>());
        int prog = -1;
        if(progress.containsKey(ar[i])) {
            prog=progress.get(ar[i]);
        }
        
        if(prog==-1){
            gains.get(gains.size()-1).add(ar[i]+"");
        }
        
        if(prog==0){
            gains.get(gains.size()-1).add(""+ar[i]+ar[i]);
        }
        
        if(prog!=-1){
            int lastTime = indexes.get(ar[i]).get(prog);
            for(int k=lastTime+1;k<i;k++) {
                for(String already:gains.get(k)) {
                    
    
                    gains.get(gains.size()-1).add(ar[i]+already+ar[i]);
                }
            }
        }
        
        
        System.out.println("for:"+i);
        for(String g : gains.get(gains.size()-1)){
            System.out.println("#"+g);
        }
           
        progress.put(ar[i],prog+1);    
    }
    
    for(int i=0;i<n;i++){
        for(String gain:gains.get(i)){
            System.out.print(gain);
            System.out.println();
        }
    }
}
}
