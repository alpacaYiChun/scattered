package com.suneo.flag.lib;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class Customers {
	public static class Customer {
     private int id;
     private int refId;
     private int amount;
     
     public Customer(int id, int amount) {
    	 this(id, -1, amount);
     }
     
     public Customer(int id, int refId, int amount) {
    	 this.id = id;
    	 this.refId = refId;
    	 this.amount = amount;
     }
    }
	
	private final HashMap<Integer, Customer> customerDB;
	private int incrId;
	
	private final Comparator<Customer> byAmount = new Comparator<Customer>() {
		@Override
		public int compare(Customer a, Customer b) {
			return a.amount - b.amount;
		}
	};
	
	public Customers() {
		this.customerDB = new HashMap<>();
	    this.incrId = 0;
	}
	
	public int create_customer(int amount) {
		int idToUse = incrId;
		customerDB.put(idToUse, new Customer(idToUse,amount));
		++incrId;
		return idToUse;
	}
	
	public int create_customer_with_referer(int amount, int refer) {
		if(!customerDB.containsKey(refer)) {
			throw new IllegalArgumentException("Unable to find: " + refer);
		}
		
		int idToUse = incrId;
		customerDB.put(idToUse, new Customer(idToUse, refer, amount));
		++incrId;
		
		return idToUse;
	}
	
	// N customer top K, N*log(K)
	
	// Sorted list by known customers
	// log(N) cut by min total
	// log(N) + K
	// List/Heap delta
	public List<Integer> list_customers_by_total_amount(int n, int min_total_amount) {
		PriorityQueue<Customer> heap = new PriorityQueue<Customer>(byAmount);
		
		for(Map.Entry<Integer, Customer> e:customerDB.entrySet()) {
			Customer customer = e.getValue();
			if(customer.amount<min_total_amount) {
				continue;
			}
			System.out.println(min_total_amount+","+customer.amount);
			if(heap.size()<n) {
				//System.out.println(customer.id+" added directly");
				heap.add(customer);
			} else {
				Customer worst = heap.peek();
				if(customer.amount>worst.amount) {
					//System.out.println("Replaced: "+customer.id);
					heap.poll();
					heap.add(customer);
				}
			}
		}
		
		Customer[] reversed = new Customer[heap.size()];
		int p=0;
		while(!heap.isEmpty()) {
			reversed[p++]=heap.poll();
		}
		
		List<Integer> ret = new ArrayList<Integer>(p);
		for(int i=p-1;i>=0;i--) {
			ret.add(reversed[i].id);
		}
		
		//System.out.println("Got:"+ret.size());
		return ret;
	}
	
	public static void main(String[] args) {
		Customers cts = new Customers();
		List<Integer> list1 = cts.list_customers_by_total_amount(5,1);
		assert(list1.size()==0);
		int id1 = cts.create_customer(1);
		int id2 = cts.create_customer(2);
		int id3 = cts.create_customer(3);
		int id4 = cts.create_customer(4);
		int id5 = cts.create_customer(5);
		int id6 = cts.create_customer(6);
		int id7 = cts.create_customer(7);
		int id8 = cts.create_customer(8);
		int id9 = cts.create_customer(9);
		
		assert(id9 == 8);
		
		List<Integer> list2 = cts.list_customers_by_total_amount(5,10);
		assert(list2.size()==0);
		
		List<Integer> list3 = cts.list_customers_by_total_amount(5,7);
		for(Integer i:list3) {
			System.out.println(">7:" + i);
		}
		
		List<Integer> list4 = cts.list_customers_by_total_amount(5,2);
		for(Integer i:list4) {
			System.out.println(">2:" + i);
		}
		
		System.out.println(cts.create_customer_with_referer(100, 0));
		
		cts.create_customer_with_referer(178, 11);
		
	}
}
