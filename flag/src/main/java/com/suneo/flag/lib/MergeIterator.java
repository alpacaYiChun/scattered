package com.suneo.flag.lib;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class MergeIterator {
	private MergeIterator() {}
	
	private static class MergeItem<T> {
		List<T> list;
		int offset;
		
		public MergeItem(List<T> list) {
			this.list = list;
			this.offset = 0;
		}
		
		public boolean hasNext() {
			return offset < list.size() - 1;
		}
		
		public void moveNext() {
			if(hasNext()) {
				++offset;
			}
		}
		
		public T peek() {
			return list.get(offset);
		}
	}
	
	public static <T> List<T> merge(List<List<T>> input, Comparator<T> cmp) {
		PriorityQueue<MergeItem<T>> queue = new PriorityQueue<>((a, b) -> {
			return cmp.compare(a.peek(), b.peek());
		});
		
		for(List<T> list : input) {
			queue.add(new MergeItem<T>(list));
		}
		
		List<T> ret = new ArrayList<T>();
		while(!queue.isEmpty()) {
			MergeItem<T> fetch = queue.poll();
			T got = fetch.peek();
			if(ret.size()==0||!ret.get(ret.size()-1).equals(got)) {
				ret.add(got);
			}
			if(fetch.hasNext()) {
				fetch.moveNext();
				queue.add(fetch);
			}
		}
		
		return ret;
	}
	
	public static void main(String[] args) {
		List<Integer> a1 = Arrays.asList(2,4,7,16,35);
		List<Integer> a2 = Arrays.asList(1,18,32,35,44);
		List<Integer> a3 = Arrays.asList(3,6,15,16,32,40);
		List<List<Integer>> input = Arrays.asList(a1,a2,a3);
		
	    List<Integer> rs = merge(input, (a,b) -> a-b);
	    
	    for(Integer r : rs) {
	    	System.out.println(r);
	    }
	}
}
