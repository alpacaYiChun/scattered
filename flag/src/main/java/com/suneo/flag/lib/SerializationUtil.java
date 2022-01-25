package com.suneo.flag.lib;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.common.io.ByteSource;

public class SerializationUtil {
    private SerializationUtil() {}
    
    public static <T> T deserialize(byte[] raw, Class<T> clazz) throws IOException, ClassNotFoundException {
    	T ret = null;
    	try (ObjectInputStream ois = new ObjectInputStream(ByteSource.wrap(raw).openStream())) {
    		ret = (T)ois.readObject();
    	}
    	return ret;
    }
    
    public static byte[] serialize(Object obj) throws IOException {
    	byte[] ret = null;
    	try (ByteArrayOutputStream bas = new ByteArrayOutputStream()) {
	    	try (ObjectOutputStream oos = new ObjectOutputStream(bas)) {
	    		oos.writeObject(obj);
	    		ret = bas.toByteArray();
	    	}
    	}
    	return ret;
    }
    
    private static class Person implements Serializable{
    	public int age;
    	public String name;
    	public Person(int age, String name) {
    		this.age = age;
    		this.name = name;
    	}
    }
    public static void main(String[] args) throws IOException, ClassNotFoundException {
    	List<Person> list = new ArrayList<Person>();
    	list.add(new Person(10, "Alpaca"));
    	list.add(new Person(20, "Llama"));
    	byte[] raw = serialize(list);
    	ArrayList<Person> back = deserialize(raw, ArrayList.class);
    	for(Person p:back) {
    		System.out.println(p.age+","+p.name);
    	}
    }
}
