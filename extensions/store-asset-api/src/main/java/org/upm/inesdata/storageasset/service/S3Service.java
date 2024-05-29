package org.upm.inesdata.storageasset.service;

import org.eclipse.edc.spi.EdcException;
import org.eclipse.edc.web.spi.exception.ObjectConflictException;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.transfer.s3.S3TransferManager;
import software.amazon.awssdk.transfer.s3.model.CompletedUpload;
import software.amazon.awssdk.transfer.s3.model.UploadRequest;
import software.amazon.awssdk.core.async.AsyncRequestBody;

import java.io.InputStream;
import java.net.URI;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Servicio para manejar operaciones de almacenamiento en S3.
 */
public class S3Service {
    private final S3AsyncClient s3AsyncClient;
    private final S3TransferManager transferManager;
    private final String bucketName;
    private final ExecutorService executorService;

    public S3Service(String accessKey, String secretKey, String endpointOverride, Region region, String bucketName) {
        this.s3AsyncClient = S3AsyncClient.builder()
            .region(region)
            .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
            .endpointOverride(URI.create(endpointOverride))
            .serviceConfiguration(S3Configuration.builder().pathStyleAccessEnabled(true).build())
            .build();
        this.transferManager = S3TransferManager.builder().s3Client(s3AsyncClient).build();
        this.bucketName = bucketName;
        this.executorService = Executors.newFixedThreadPool(10); // Crear un pool de hilos fijo
    }

    public String uploadFile(String key, InputStream inputStream, long contentLength) {

        // Verificar si el archivo ya existe
        boolean exists = doesObjectExist(bucketName, key).join();
        if (exists) {
            throw new ObjectConflictException("File with key " + key + " already exists.");
        }

        PutObjectRequest objectRequest = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .build();

        AsyncRequestBody requestBody = AsyncRequestBody.fromInputStream(inputStream, contentLength, executorService);

        UploadRequest uploadRequest = UploadRequest.builder()
            .putObjectRequest(objectRequest)
            .requestBody(requestBody)
            .build();

        CompletableFuture<CompletedUpload> upload = transferManager.upload(uploadRequest).completionFuture();
        upload.join(); // Esperar a que la carga se complete

        return key;
    }

    public void deleteFile(String key) {
        // Ajustar la clave para incluir la carpeta
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
            .bucket(bucketName)
            .key(fullKey)
            .build();
        s3AsyncClient.deleteObject(deleteObjectRequest).join(); // Esperar a que se complete la eliminación
    }

    public void close() {
        transferManager.close();
        s3AsyncClient.close();
        executorService.shutdown();
    }

    public CompletableFuture<Boolean> doesObjectExist(String bucketName, String key) {
        HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .build();

        return s3AsyncClient.headObject(headObjectRequest)
            .thenApply(response -> true)
            .exceptionally(ex -> {
                if (ex.getCause() instanceof NoSuchKeyException || (ex.getCause() instanceof S3Exception && ((S3Exception) ex.getCause()).statusCode() == 404)) {
                    return false;
                } else {
                    throw new RuntimeException("Error checking if object exists", ex);
                }
            });
    }
}
