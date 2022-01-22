package com.suneo.flag.lib.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class SimpleHandler implements InvocationHandler {
	private ISimple target;
	
	public SimpleHandler(ISimple target) {
		this.target=target;
	}
	
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		if(method.getName().equals("add")) {
			int original = (Integer)method.invoke(target, args);
			return original + 1;
		}

		return null;
	}
}
