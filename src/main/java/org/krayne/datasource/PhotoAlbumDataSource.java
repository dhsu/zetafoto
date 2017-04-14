package org.krayne.datasource;

import com.google.common.collect.ImmutableMap;
import org.krayne.model.Album;
import org.krayne.model.AlbumContent;
import org.krayne.model.Photo;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface PhotoAlbumDataSource {
    CompletableFuture<Optional<AlbumContent>> getAlbumContent(Long albumId);
    CompletableFuture<ImmutableMap<Album, AlbumContent>> getAll();
    CompletableFuture<Optional<Album>> getAlbum(Long albumId);
    CompletableFuture<OpResult> createAlbum(Album album);
    CompletableFuture<OpResult> updateAlbum(Album album);
    CompletableFuture<OpResult> deleteAlbum(Long albumId);
    CompletableFuture<Optional<Photo>> getPhoto(Long photoId);
    CompletableFuture<OpResult> createPhoto(Photo photo);
    CompletableFuture<OpResult> updatePhoto(Photo photo);
    CompletableFuture<OpResult> deletePhoto(Long photoId);
}
