package org.krayne.init;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.krayne.datasource.PhotoAlbumDataSource;
import org.krayne.model.Album;
import org.krayne.model.Photo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

public class Initializer {
    private static final Logger LOGGER = LoggerFactory.getLogger(Initializer.class);
    private static final ResponseHandler<String> RESPONSE_HANDLER;

    static {
        RESPONSE_HANDLER = response -> {
            int status = response.getStatusLine().getStatusCode();
            if (status >= 200 && status < 300) {
                HttpEntity entity = response.getEntity();
                return entity != null ? EntityUtils.toString(entity) : "";
            } else {
                throw new ClientProtocolException("Unexpected response, " + status);
            }
        };
    }

    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;
    private final PhotoAlbumDataSource photoAlbumDataSource;

    public Initializer(ObjectMapper objectMapper, PhotoAlbumDataSource photoAlbumDataSource, HttpClient httpClient) {
        this.objectMapper = objectMapper;
        this.photoAlbumDataSource = photoAlbumDataSource;
        this.httpClient = httpClient;
    }

    public CompletableFuture<Void> init(String albumsUrl, String photosUrl) {
        return this.initAlbums(albumsUrl).thenCompose(v -> this.initPhotos(photosUrl));
    }

    private CompletableFuture<Void> initAlbums(String albumsUrl) {
        CompletableFuture<Album[]> albumsFuture = CompletableFuture.supplyAsync(() -> {
            try {
                HttpGet albumsGet = new HttpGet(albumsUrl);
                String albumsJson = this.httpClient.execute(albumsGet, RESPONSE_HANDLER);
                Album[] albums = this.objectMapper.readValue(albumsJson, Album[].class);
                return albums;
            } catch (IOException e) {
                String message = "Initializer failed to fetch albums from " + albumsUrl;
                LOGGER.error(message, e);
                throw new RuntimeException(message, e);
            }
        });

        return albumsFuture.thenCompose(albums -> {
            CompletableFuture[] albumCreates = Arrays.stream(albums)
                    .map(this.photoAlbumDataSource::createAlbum)
                    .toArray(CompletableFuture[]::new);
            return CompletableFuture.allOf(albumCreates);
        });
    }

    private CompletableFuture<Void> initPhotos(String photosUrl) {
        CompletableFuture<Photo[]> photosFuture = CompletableFuture.supplyAsync(() -> {
            try {
                HttpGet photosGet = new HttpGet(photosUrl);
                String photosJson = this.httpClient.execute(photosGet, RESPONSE_HANDLER);
                Photo[] photos = this.objectMapper.readValue(photosJson, Photo[].class);
                return photos;
            } catch (IOException e) {
                String message = "Initializer failed to fetch photos from " + photosUrl;
                LOGGER.error(message, e);
                throw new RuntimeException(message, e);
            }
        });

        return photosFuture.thenCompose(photos -> {
            CompletableFuture[] photoCreates = Arrays.stream(photos)
                    .map(this.photoAlbumDataSource::createPhoto)
                    .toArray(CompletableFuture[]::new);
            return CompletableFuture.allOf(photoCreates);
        });
    }
}
