package com.suneo.flag.lib;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Reg1 {
	private static final String REG = "(?<department>[a-zA-Z]+)[-:\\s]?(?<code>[0-9]+)\\s(((?<year>[0-9]{2}|[0-9]{4})\\s?(?<season>F|W|S|Su|Summer|Fall|Winter|Spring))|((?<season2>F|W|S|Su|Summer|Fall|Winter|Spring)\\s?(?<year2>[0-9]{2}|[0-9]{4})))";

	public static void main(String[] args) {
		Pattern pattern = Pattern.compile(REG, Pattern.CASE_INSENSITIVE);
		String[] examples = new String[] {
			"CS111 2016 Fall",
			"CS-111 Fall 2016",
			"CS 111 F2016"
		};
		for(String example:examples) {
			Matcher matcher = pattern.matcher(example);
			System.out.println(matcher.matches());
			System.out.println(matcher.groupCount());
			//System.out.println(matcher.group(1));
			//System.out.println(matcher.group(2));
			System.out.println(matcher.group("department"));
			System.out.println(matcher.group("code"));
			System.out.println(matcher.group("year2"));
			System.out.println(matcher.group("season2"));
			for(int i=1;i<=matcher.groupCount();i++) {

			}
		}
		
	}
}
