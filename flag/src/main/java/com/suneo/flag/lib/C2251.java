package com.suneo.flag.lib;

import java.util.Comparator;

import com.suneo.flag.lib.SegTree.IAccu;
import com.suneo.flag.lib.SegTree.IRangeHelper;

public class C2251 {
	public int[] fullBloomFlowers(int[][] flowers, int[] persons) {
        Comparator<Integer> cmp = new Comparator<Integer>() {
			@Override
			public int compare(Integer o1, Integer o2) {
				return o1-o2;
			}
        };
        Accu accu = new Accu();
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        for(int[] f:flowers) {
        	if(f[0]<min) {
        		min = f[0];
        	}
        	if(f[1]>max) {
        		max=f[1];
        	}
        }
        SegTree<Integer,Integer> root = new SegTree<>(cmp, accu, new RangeHelper(), min, max);
        
        for(int[] flower:flowers) {
        	root.insert(flower[0], flower[1], 1);
        }
        
        int[] ret = new int[persons.length];
        for(int i=0;i<persons.length;i++) {
        	int p = root.sum(persons[i]);
        	ret[i] = p;
        }
        
        return ret;
    }
	
	private static class Accu implements IAccu<Integer> {
		private Integer all;
		public Accu() {
			all = 0;
		}
		@Override
		public void accu(Integer t) {
			all+=t;
		}
		@Override
		public Integer get() {
			return all;
		}
		@Override
		public IAccu<Integer> zero() {
			return new Accu();
		}
		
	}
	
	public static class RangeHelper implements IRangeHelper<Integer> {

		@Override
		public Integer getMid(Integer from, Integer to) {
			return (from + to) >> 1;
		}

		@Override
		public boolean sealed(Integer from, Integer to) {
			return from == to;
		}

		@Override
		public Integer getNext(Integer now) {
			return now + 1;
		}
        
    }
}
