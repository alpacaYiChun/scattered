package com.suneo.flag.lib;

import java.util.Comparator;

public class SegTree<T,A> {
	private Comparator<T> cmp;
	
	private IRangeHelper<T> helper;
	
	private IAccu<A> accu;
	
	private T from;
	private T to;
	
	private SegTree<T,A> left;
	private SegTree<T,A> right;
	
	public SegTree(Comparator<T> cmp, IAccu<A> accu, IRangeHelper<T> helper, T from, T to) {
		this.cmp = cmp;
		this.accu = accu;
		this.from = from;
		this.to = to;
		this.helper = helper;
	}
	
	public void insert(T from, T to, A a) {
		if(cmp.compare(to, this.from) < 0) {
			return;
		}
		if(cmp.compare(from, this.to) > 0) {
			return;
		}
		if(cmp.compare(from, this.from) <= 0 && cmp.compare(to, this.to) >= 0) {
			this.accu.accu(a);
			return;
		}
		this.split();
		
		this.left.insert(from, to, a);
		this.right.insert(from, to, a);
	}
	
	public A sum(T index) {
		IAccu<A> sum = this.accu.zero();
		if(cmp.compare(index, this.from) < 0) {
			return sum.get();
		}
		if(cmp.compare(index, this.to) > 0) {
			return sum.get();
		}
		sum.accu(this.accu.get());
		if(left!=null) {
			A aLeft = left.sum(index);
			A aRight = right.sum(index);
			sum.accu(aLeft);
			sum.accu(aRight);
		}
		
		return sum.get();
	}
	
	private void split() {
		if(left!=null) {
			return;
		}
		if(helper.sealed(from, to)) {
			return;
		}
		T mid = helper.getMid(from, to);
		
		this.left = new SegTree<T,A>(cmp, accu.zero(), helper, from, mid);
		this.right = new SegTree<T,A>(cmp, accu.zero(), helper, helper.getNext(mid), to);
	}
	
	public static interface IRangeHelper<T> {
		T getMid(T from, T to);
		boolean sealed(T from, T to);
		T getNext(T now);
	}
	
	public static interface IAccu<T> {
		void accu(T t);
		T get();
		IAccu<T> zero();
	}
}
