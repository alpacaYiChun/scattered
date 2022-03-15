package com.suneo.flag.lib;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.BoundingPoly;
import com.google.cloud.vision.v1.FaceAnnotation;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.cloud.vision.v1.Vertex;
import com.google.protobuf.ByteString;

public class FaceDetectorAPI {
	private ImageAnnotatorClient client = null;
	
	public FaceDetectorAPI() {
		try {
			this.client = ImageAnnotatorClient.create();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public HashMap<String,List<int[]>> handle(List<String> files) throws FileNotFoundException, IOException {
		List<AnnotateImageRequest> requests = new ArrayList<>();
		List<String> requested = new ArrayList<>();
		for(String file : files) {
			try {
				ByteString imgBytes = ByteString.readFrom(new FileInputStream(file));
				Image img = Image.newBuilder().setContent(imgBytes).build();
			    Feature feat = Feature.newBuilder().setType(Feature.Type.FACE_DETECTION).build();
			    AnnotateImageRequest request =
			        AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();			   
			    requests.add(request);
			    requested.add(file);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	    
		BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
	    List<AnnotateImageResponse> responses = response.getResponsesList();
	    
	    HashMap<String, List<int[]>> answer = new HashMap<>();
	    
	    for (int i=0;i<responses.size();i++) {
	    	AnnotateImageResponse res = responses.get(i);
	        if (res.hasError()) {
	          System.out.format("Error: %s%n", res.getError().getMessage());
	        }

	        String file = requested.get(i);
	        if(!answer.containsKey(file)) {
	        	answer.put(file, new ArrayList<int[]>());
	        }
	        
	        // For full list of available annotations, see http://g.co/cloud/vision/docs
	        for (FaceAnnotation annotation : res.getFaceAnnotationsList()) {
	        	BoundingPoly rect = annotation.getBoundingPoly();
	        	int minX = Integer.MAX_VALUE;
	        	int maxX = Integer.MIN_VALUE;
	        	int minY = Integer.MAX_VALUE;
	        	int maxY = Integer.MIN_VALUE;
	        	for(Vertex v:rect.getVerticesList()) {
	        		if(v.getX()>maxX) {
	        			maxX=v.getX();
	        		}
	        		if(v.getX()<minX) {
	        			minX=v.getX();
	        		}
	        		if(v.getY()>maxY) {
	        			maxY=v.getY();
	        		}
	        		if(v.getY()<minY) {
	        			minY=v.getY();
	        		}
	        	}
	        	answer.get(file).add(new int[] {minX, maxX, minY, maxY});
	        }
	    }
	    
	    return answer;
	}
}
