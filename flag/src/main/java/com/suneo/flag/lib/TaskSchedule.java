package com.suneo.flag.lib;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class TaskSchedule {
	public static void main(String[] args) throws IOException {
		TaskSchedule ts = new TaskSchedule();
		ts.test("C:\\Users\\Think\\Downloads\\input.txt");
	}
	
	private static enum EventType {
		SUBMIT,
		RELEASE
	}
	
	private static class Event {
		EventType type;
		int id;
		int time;
		public Event(EventType type, int id, int time) {
			this.type = type;
			this.id = id;
			this.time = time;		
		}
	}
	
	private PriorityQueue<Event> eventQueue = new PriorityQueue<>((e1, e2) -> {
		if (e1.time!=e2.time) {
			return e1.time - e2.time;
		}
		if (e1.type.equals(EventType.RELEASE)) {
			return -1;
		}
		return 1;
	});
	
	private PriorityQueue<int[]> logs = new PriorityQueue<>((e1,e2) -> {
		return e1[0] - e2[0];
	});
	
	private PriorityQueue<Integer> workerPool = new PriorityQueue<>((e1, e2) -> {
		return e1-e2;
	});
	
	private HashMap<Integer, Integer> taskWorkerMap = new HashMap<>();
	
	private AtomicInteger jobId = new AtomicInteger(0);
	private AtomicInteger workerId = new AtomicInteger(0);
	
	public void test(String input) throws IOException {
		loadEvents(input);
		processAllEvents();
		System.out.println(workerId.get());
		while(!logs.isEmpty()) {
			int[] log = logs.poll();
			System.out.println("J"+log[0]+" "+"W"+log[1]);
		}
	}
	
	private void processEvent(Event e) {
		switch(e.type) {
			case SUBMIT:{
			    int jid = e.id;
				int wid = -1;
				if(!workerPool.isEmpty()) {
					wid = workerPool.poll();
				} else {
					wid = workerId.getAndIncrement();
				}
				taskWorkerMap.put(jid, wid);
				logs.add(new int[] {jid, wid});
				
				break;
			}
			case RELEASE: {
				int jid = e.id;
				int wid = taskWorkerMap.get(jid);
				taskWorkerMap.remove(jid);
				workerPool.add(wid);
				break;
			}
		}
	}
	
	private void processAllEvents() {
		while(!eventQueue.isEmpty()) {
			Event event = eventQueue.poll();
			processEvent(event);
		}
	}
	
	private void loadEvents(String filePath) throws IOException {
		Files
		.readAllLines(Path.of(filePath))
		.stream()
		.forEach(e -> {
			String[] parts = e.split(" ");
			if(parts.length!=2) {
				return;
			}
			int start = minutes(parts[0]);
			int release = start + Integer.parseInt(parts[1]) + 1;
			int jid = jobId.getAndIncrement();
			eventQueue.add(new Event(EventType.SUBMIT, jid, start));
			eventQueue.add(new Event(EventType.RELEASE, jid, release));
		});
	}
		
	private static int minutes(String p) {
		char[] ar = p.toCharArray();
		int hour = 10*(ar[0]-'0')+(ar[1]-'0');
		int minute = 10*(ar[2]-'0')+(ar[3]-'0');
		return 60*hour+minute;
	}
}
