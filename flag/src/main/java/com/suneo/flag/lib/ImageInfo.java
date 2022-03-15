package com.suneo.flag.lib;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

public class ImageInfo {
	public static void main(String[] args) throws IOException {
		//process("C:\\Users\\Think\\Downloads\\gender\\data\\women", "C:\\Users\\Think\\Downloads\\gender\\data\\womenFace\\");
		process("D:/PR/Photo", "D:/PR/PhotoMe");
	}
	
	public static void process(String folder, String outputFolder) throws IOException {
		FaceDetectorAPI detector = new FaceDetectorAPI();
		
		List<String> files = Files.list(Path.of(folder))
				.map(p -> p.toAbsolutePath().toString())
				.collect(Collectors.toList());
		PeevishIterator<String> iter = new PeevishIterator<String>(files);
		int fileId = 0;
		while(iter.hasNext()) {
			List<String> batch = iter.nextBatch(10);
			HashMap<String, List<int[]>> faces = detector.handle(batch);
			for(Map.Entry<String, List<int[]>> e:faces.entrySet()) {
				String file = e.getKey();
				BufferedImage img = null;
				try (FileInputStream fis = new FileInputStream(new File(file))) {
					img = ImageIO.read(fis);
					for(int[] ar:e.getValue()) {
						System.out.println(String.format("%s: %d, %d, %d, %d", file, ar[0],ar[1],ar[2],ar[3]));	
						BufferedImage cropped = cropImage(img, ar[0],ar[2],ar[1]-ar[0],ar[3]-ar[2]);
						String savePath = outputFolder + fileId + ".jpg";
						ImageIO.write(cropped, "jpg", new File(savePath));
						++fileId;
					}
					
				} 
			}
		}	
	}
	
	private static class PeevishIterator<T> {
		List<T> src;
		int now = 0;
		public PeevishIterator(List<T> src) {
			this.src = src;
		}
		
		public boolean hasNext() {
			return now<src.size();
		}
		
		public List<T> nextBatch(int limit) {
			List<T> ret = new ArrayList<T>();
			int i = 0;
			while(i<limit && hasNext()) {
				ret.add(src.get(now));
				++now;
				++i;
			}
			return ret;
		}
	}
	
	private static BufferedImage cropImage(BufferedImage src, int cx, int cy, int w, int h) {
	    BufferedImage dest = src.getSubimage(cx, cy, w, h);
	    return dest; 
	}
}
