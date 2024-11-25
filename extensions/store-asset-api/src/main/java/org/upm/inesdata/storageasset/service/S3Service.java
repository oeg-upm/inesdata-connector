package org.upm.inesdata.storageasset.service;

import org.eclipse.edc.spi.monitor.Monitor;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class S3Service {
    private final S3AsyncClient s3AsyncClient;
    private final String bucketName;
    private final ConcurrentMap<String, MultipartUploadState> multipartUploadStates = new ConcurrentHashMap<>();
    private final Monitor monitor;

    private static final long PART_SIZE = 50 * 1024 * 1024;

    public S3Service(String accessKey, String secretKey, String endpointOverride, Region region, String bucketName, Monitor monitor) {
        this.s3AsyncClient = S3AsyncClient.builder()
                .region(region)
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
                .endpointOverride(URI.create(endpointOverride))
                .serviceConfiguration(S3Configuration.builder().pathStyleAccessEnabled(true).build())
                .build();
        this.bucketName = bucketName;
        this.monitor = monitor;
    }

    public void uploadChunk(String key, InputStream inputStream, int chunkIndex, int totalChunks) throws IOException {
        MultipartUploadState uploadState = multipartUploadStates.computeIfAbsent(key, k -> initMultipartUpload(key));

        byte[] buffer = inputStream.readAllBytes();
        long contentLength = buffer.length;

        if (contentLength < PART_SIZE && chunkIndex < totalChunks - 1) {
            throw new IllegalArgumentException("Each chunk (except the last one) must be at least " + PART_SIZE + " bytes");
        }

        UploadPartRequest uploadPartRequest = UploadPartRequest.builder()
                .bucket(bucketName)
                .key(key)
                .uploadId(uploadState.uploadId)
                .partNumber(chunkIndex + 1)
                .contentLength(contentLength)
                .build();

        CompletableFuture<UploadPartResponse> uploadFuture = s3AsyncClient.uploadPart(uploadPartRequest, AsyncRequestBody.fromBytes(buffer));
        uploadFuture.thenAccept(uploadPartResponse -> {
            uploadState.completedParts.add(CompletedPart.builder().partNumber(chunkIndex + 1).eTag(uploadPartResponse.eTag()).build());

            if (chunkIndex == totalChunks - 1) {
                completeMultipartUpload(key, uploadState);
                multipartUploadStates.remove(key);
            }
        }).exceptionally(e -> {
            abortMultipartUpload(key, uploadState);
            multipartUploadStates.remove(key);
            monitor.warning("Error uploading chunk " + chunkIndex + " for file " + key, e);
            throw new RuntimeException("Error uploading chunk " + chunkIndex + " for file " + key, e);
        });
    }

    private MultipartUploadState initMultipartUpload(String key) {
        CreateMultipartUploadRequest createRequest = CreateMultipartUploadRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
        CreateMultipartUploadResponse createResponse = s3AsyncClient.createMultipartUpload(createRequest).join();
        monitor.info(" Upload started");
        return new MultipartUploadState(createResponse.uploadId());
    }

    private void completeMultipartUpload(String key, MultipartUploadState uploadState) {
        CompleteMultipartUploadRequest completeRequest = CompleteMultipartUploadRequest.builder()
                .bucket(bucketName)
                .key(key)
                .uploadId(uploadState.uploadId)
                .multipartUpload(CompletedMultipartUpload.builder().parts(uploadState.completedParts).build())
                .build();
        s3AsyncClient.completeMultipartUpload(completeRequest).join();
        monitor.info(" Upload completed");
    }

    private void abortMultipartUpload(String key, MultipartUploadState uploadState) {
        AbortMultipartUploadRequest abortRequest = AbortMultipartUploadRequest.builder()
                .bucket(bucketName)
                .key(key)
                .uploadId(uploadState.uploadId)
                .build();
        s3AsyncClient.abortMultipartUpload(abortRequest).join();
        monitor.info("Upload aborted");
    }

    private static class MultipartUploadState {
        private final String uploadId;
        private final List<CompletedPart> completedParts;

        MultipartUploadState(String uploadId) {
            this.uploadId = uploadId;
            this.completedParts = new ArrayList<>();
        }
    }

    public void deleteFile(String key) {
        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();
            s3AsyncClient.deleteObject(deleteObjectRequest).join();
        } catch (Exception e) {
            monitor.severe("Error deleting file " + key + ": " + e.getMessage());
        }
    }

    public void close() {
        s3AsyncClient.close();
    }
}
