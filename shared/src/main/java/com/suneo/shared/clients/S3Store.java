package com.suneo.shared.clients;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;

import java.io.IOException;

public class S3Store {
    private final AmazonS3 s3client;

    public S3Store(AmazonS3 client) {
        this.s3client = client;
    }

    public String getContentAsString(String bucket, String file) throws IOException {
        S3Object obj = s3client.getObject(bucket, file);

        try (var stream = obj.getObjectContent()) {
            return IOUtils.toString(stream);
        }
    }
}
