package org.krayne.datasource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.krayne.datasource.inmemory.InMemoryPhotoAlbumDataSource;
import org.krayne.model.Album;
import org.krayne.model.AlbumContent;
import org.krayne.model.Photo;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class PhotoAlbumDataSourceTest {
    private static Album TEST_ALBUM = new Album(123L, 456L, "Black Holes");
    private static Photo TEST_PHOTO = new Photo(12345L, 123L, "The Enigma", "http://placehold.it/500/ff0000", Optional.of("http://placehold.it/100/ff0000"));
    private static Photo TEST_PHOTO_2 = new Photo(12346L, 123L, "The 2nd Enigma", "http://placehold.it/500/ffff00", Optional.of("http://placehold.it/100/ffff00"));

    private PhotoAlbumDataSource photoAlbumDataSource;

    @Before
    public void initDataSource() {
        this.photoAlbumDataSource = new InMemoryPhotoAlbumDataSource();
    }

    @Test
    public void createNonExistentAlbum() {
        CompletableFuture<OpResult> createResult = this.photoAlbumDataSource.createAlbum(TEST_ALBUM);
        Assert.assertEquals(OpResult.Type.CREATED, createResult.join().getType());
    }

    @Test
    public void updateExistingAlbumTitle() {
        Album updatedAlbum = new Album(TEST_ALBUM.getId(), TEST_ALBUM.getUserId(), "Wormholes");
        CompletableFuture<OpResult> updateResult = this.photoAlbumDataSource.createAlbum(TEST_ALBUM)
                .thenCompose(albumCreated -> this.photoAlbumDataSource.updateAlbum(updatedAlbum));
        Assert.assertEquals(OpResult.Type.UPDATED, updateResult.join().getType());
    }

    @Test
    public void updateExistingAlbumUser() {
        Album updatedAlbum = new Album(TEST_ALBUM.getId(), 78987L, TEST_ALBUM.getTitle());
        CompletableFuture<OpResult> updateResult = this.photoAlbumDataSource.createAlbum(TEST_ALBUM)
                .thenCompose(albumCreated -> this.photoAlbumDataSource.updateAlbum(updatedAlbum));
        Assert.assertEquals(OpResult.Type.STATE_MISMATCH, updateResult.join().getType());
    }

    @Test
    public void createExistingAlbum() {
        CompletableFuture<OpResult> createResult = this.photoAlbumDataSource.createAlbum(TEST_ALBUM)
                .thenCompose(albumCreated -> this.photoAlbumDataSource.createAlbum(TEST_ALBUM));
        Assert.assertEquals(OpResult.Type.ALREADY_EXISTING, createResult.join().getType());
    }

    @Test
    public void updateNonExistentAlbum() {
        CompletableFuture<OpResult> updateResult = this.photoAlbumDataSource.updateAlbum(TEST_ALBUM);
        Assert.assertEquals(OpResult.Type.NON_EXISTENT, updateResult.join().getType());
    }

    @Test
    public void deleteEmptyAlbum() {
        CompletableFuture<OpResult> deleteResult = this.photoAlbumDataSource.createAlbum(TEST_ALBUM)
                .thenCompose(albumCreated -> this.photoAlbumDataSource.deleteAlbum(TEST_ALBUM.getId()));
        Assert.assertEquals(OpResult.Type.DELETED, deleteResult.join().getType());
    }

    @Test
    public void deleteNonEmptyAlbum() {
        CompletableFuture<OpResult> deleteResult = this.photoAlbumDataSource.createAlbum(TEST_ALBUM)
                .thenCompose(createAlbumResult -> this.photoAlbumDataSource.createPhoto(TEST_PHOTO))
                .thenCompose(createPhotoResult -> this.photoAlbumDataSource.deleteAlbum(TEST_ALBUM.getId()));
        Assert.assertEquals(OpResult.Type.STATE_MISMATCH, deleteResult.join().getType());
    }

    @Test
    public void createPhotoInExistingAlbum() {
        CompletableFuture<OpResult> createResult = this.photoAlbumDataSource.createAlbum(TEST_ALBUM)
                .thenCompose(albumCreated -> this.photoAlbumDataSource.createPhoto(TEST_PHOTO));
        Assert.assertEquals(OpResult.Type.CREATED, createResult.join().getType());
    }

    @Test
    public void createPhotoInNonExistentAlbum() {
        CompletableFuture<OpResult> createResult = this.photoAlbumDataSource.createPhoto(TEST_PHOTO);
        Assert.assertEquals(OpResult.Type.STATE_MISMATCH, createResult.join().getType());
    }

    @Test
    public void updateNonExistentPhoto() {
        CompletableFuture<OpResult> updateResult = this.photoAlbumDataSource.updatePhoto(TEST_PHOTO);
        Assert.assertEquals(OpResult.Type.NON_EXISTENT, updateResult.join().getType());
    }

    @Test
    public void updateExistingPhotoToExistingAlbum() {
        Photo updatedPhoto = new Photo(TEST_PHOTO.getId(), TEST_PHOTO.getAlbumId(), "The Enigmatic Black Hole", TEST_PHOTO.getUrl(), TEST_PHOTO.getThumbnailUrl());
        CompletableFuture<OpResult> updateResult = this.photoAlbumDataSource.createAlbum(TEST_ALBUM)
                .thenCompose(createAlbumResult -> this.photoAlbumDataSource.createPhoto(TEST_PHOTO))
                .thenCompose(createPhotoResult -> this.photoAlbumDataSource.updatePhoto(updatedPhoto));
        Assert.assertEquals(OpResult.Type.UPDATED, updateResult.join().getType());
    }

    @Test
    public void updateExistingPhotoToNonExistentAlbum() {
        Photo updatedPhoto = new Photo(TEST_PHOTO.getId(), 9879122L, TEST_PHOTO.getTitle(), TEST_PHOTO.getUrl(), TEST_PHOTO.getThumbnailUrl());
        CompletableFuture<OpResult> updateResult = this.photoAlbumDataSource.createAlbum(TEST_ALBUM)
                .thenCompose(createAlbumResult -> this.photoAlbumDataSource.createPhoto(TEST_PHOTO))
                .thenCompose(createPhotoResult -> this.photoAlbumDataSource.updatePhoto(updatedPhoto));
        Assert.assertEquals(OpResult.Type.STATE_MISMATCH, updateResult.join().getType());
    }

    @Test
    public void deleteExistingPhoto() {
        CompletableFuture<OpResult> deleteResult = this.photoAlbumDataSource.createAlbum(TEST_ALBUM)
                .thenCompose(createAlbumResult -> this.photoAlbumDataSource.createPhoto(TEST_PHOTO))
                .thenCompose(createPhotoResult -> this.photoAlbumDataSource.deletePhoto(TEST_PHOTO.getId()));
        Assert.assertEquals(OpResult.Type.DELETED, deleteResult.join().getType());
    }

    @Test
    public void deleteNonExistentPhoto() {
        CompletableFuture<OpResult> deleteResult = this.photoAlbumDataSource.deletePhoto(5437856L);
        Assert.assertEquals(OpResult.Type.NON_EXISTENT, deleteResult.join().getType());
    }

    @Test
    public void albumPhotosShouldBeRetrievable() {
        Optional<AlbumContent> albumContent = this.photoAlbumDataSource.createAlbum(TEST_ALBUM)
                .thenCompose(albumCreated -> this.photoAlbumDataSource.createPhoto(TEST_PHOTO))
                .thenCompose(photoCreated -> this.photoAlbumDataSource.createPhoto(TEST_PHOTO_2))
                .thenCompose(photoCreated -> this.photoAlbumDataSource.getAlbumContent(TEST_ALBUM.getId()))
                .join();
        Assert.assertTrue(albumContent.isPresent());
        Assert.assertTrue(albumContent.get().getPhotos().contains(TEST_PHOTO));
        Assert.assertTrue(albumContent.get().getPhotos().contains(TEST_PHOTO_2));
    }

    @Test
    public void albumShouldBeUpdatedAfterDeletedPhoto() {
        Optional<AlbumContent> albumContent = this.photoAlbumDataSource.createAlbum(TEST_ALBUM)
                .thenCompose(albumCreated -> this.photoAlbumDataSource.createPhoto(TEST_PHOTO))
                .thenCompose(photoCreated -> this.photoAlbumDataSource.createPhoto(TEST_PHOTO_2))
                .thenCompose(photoCreated -> this.photoAlbumDataSource.deletePhoto(TEST_PHOTO.getId()))
                .thenCompose(photoCreated -> this.photoAlbumDataSource.getAlbumContent(TEST_ALBUM.getId()))
                .join();
        Assert.assertTrue(albumContent.isPresent());
        Assert.assertFalse(albumContent.get().getPhotos().contains(TEST_PHOTO));
        Assert.assertTrue(albumContent.get().getPhotos().contains(TEST_PHOTO_2));
    }
}
