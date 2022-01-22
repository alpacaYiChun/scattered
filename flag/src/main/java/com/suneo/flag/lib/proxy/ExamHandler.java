package com.suneo.flag.lib.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class ExamHandler implements InvocationHandler {
    private IExam exam;
	
    public ExamHandler(IExam exam) {
    	this.exam = exam;
    }
	
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		if(method.getName().equals("rev")) {
			if(args[0].toString().equals("fuck")) {
				return "fuck";
			}
			return method.invoke(exam, args);
		}
		
		return "Unknown";
	}

}
