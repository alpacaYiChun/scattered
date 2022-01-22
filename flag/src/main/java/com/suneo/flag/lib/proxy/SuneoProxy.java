package com.suneo.flag.lib.proxy;

import java.lang.reflect.Proxy;

public class SuneoProxy {
    public static void main(String[] args) {
    	Simple simple = new Simple();
    	ISimple pSimple = (ISimple)Proxy.newProxyInstance(
    			SuneoProxy.class.getClassLoader(),
    			new Class[] {ISimple.class},
    			new SimpleHandler(simple));
  
    	System.out.println(pSimple.add(3, 6));
    	
    	Exam exam = new Exam();
    	IExam pExam = (IExam)Proxy.newProxyInstance(
    			SuneoProxy.class.getClassLoader(),
    			new Class[] {IExam.class},
    			new ExamHandler(exam));
    			
    	System.out.println(pExam.rev("wocao"));
    	System.out.println(pExam.rev("fuck"));
    	System.out.println(pExam.rev("zhubo"));
    }
}
