package org.krayne.datasource.inmemory;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.SortedSetMultimap;
import com.google.common.collect.TreeMultimap;
import org.krayne.datasource.OpResult;
import org.krayne.datasource.PhotoAlbumDataSource;
import org.krayne.model.Album;
import org.krayne.model.AlbumContent;
import org.krayne.model.Photo;
import org.krayne.util.ImmutableCollectors;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class InMemoryPhotoAlbumDataSource implements PhotoAlbumDataSource {
    private final Map<Long, Album> albums; // albumId -> album
    private final Map<Long, Photo> photos; // photoId -> photo
    private final SortedSetMultimap<Long, Long> albumPhotoIds; // albumId -> photoId
    private final ReadWriteLock lock;

    public InMemoryPhotoAlbumDataSource() {
        this.albums = Maps.newHashMap();
        this.photos = Maps.newHashMap();
        this.albumPhotoIds = TreeMultimap.create();
        this.lock = new ReentrantReadWriteLock();
    }

    @Override
    public CompletableFuture<Optional<AlbumContent>> getAlbumContent(Long albumId) {
        this.lock.readLock().lock();
        try {
            return CompletableFuture.completedFuture(this.getContent(albumId));
        } finally {
            this.lock.readLock().unlock();
        }
    }

    @Override
    public CompletableFuture<ImmutableList<Album>> getAlbums() {
        this.lock.readLock().lock();
        try {
            return CompletableFuture.completedFuture(ImmutableList.copyOf(this.albums.values()));
        } finally {
            this.lock.readLock().unlock();
        }
    }

    @Override
    public CompletableFuture<Optional<Album>> getAlbum(Long albumId) {
        this.lock.readLock().lock();
        try {
            Optional<Album> album = Optional.ofNullable(this.albums.get(albumId));
            return CompletableFuture.completedFuture(album);
        } finally {
            this.lock.readLock().unlock();
        }
    }

    @Override
    public CompletableFuture<OpResult> createAlbum(Album album) {
        this.lock.writeLock().lock();
        try {
            boolean albumExists = this.albums.containsKey(album.getId());

            if (albumExists) {
                return CompletableFuture.completedFuture(OpResult.alreadyExisting());
            } else {
                this.albums.put(album.getId(), album);
                return CompletableFuture.completedFuture(OpResult.created());
            }
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    @Override
    public CompletableFuture<OpResult> updateAlbum(Album album) {
        this.lock.writeLock().lock();
        try {
            Album existingAlbum = this.albums.get(album.getId());
            boolean albumExists = existingAlbum != null;
            boolean userIdsMatch = albumExists && album.getUserId().equals(existingAlbum.getUserId());

            if (!albumExists) {
                return CompletableFuture.completedFuture(OpResult.nonExistent());
            } else if (!userIdsMatch) {
                return CompletableFuture.completedFuture(OpResult.stateMismatch("An album's user id may not be updated"));
            } else {
                this.albums.put(album.getId(), album);
                return CompletableFuture.completedFuture(OpResult.updated());
            }
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    @Override
    public CompletableFuture<OpResult> deleteAlbum(Long albumId) {
        this.lock.writeLock().lock();
        try {
            boolean albumExists = this.albums.containsKey(albumId);
            boolean isAlbumEmpty = !this.albumPhotoIds.containsKey(albumId);

            if (!albumExists) {
                return CompletableFuture.completedFuture(OpResult.nonExistent());
            } else if (!isAlbumEmpty) {
                return CompletableFuture.completedFuture(OpResult.stateMismatch("Album must be empty before deletion"));
            } else {
                this.albums.remove(albumId);
                return CompletableFuture.completedFuture(OpResult.deleted());
            }
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    @Override
    public CompletableFuture<Optional<Photo>> getPhoto(Long photoId) {
        this.lock.readLock().lock();
        try {
            Optional<Photo> photo = Optional.ofNullable(this.photos.get(photoId));
            return CompletableFuture.completedFuture(photo);
        } finally {
            this.lock.readLock().unlock();
        }
    }

    @Override
    public CompletableFuture<OpResult> createPhoto(Photo photo) {
        this.lock.writeLock().lock();
        try {
            boolean albumExists = this.albums.containsKey(photo.getAlbumId());
            boolean photoExists = this.photos.containsKey(photo.getId());

            if (photoExists) {
                return CompletableFuture.completedFuture(OpResult.alreadyExisting());
            } else if (!albumExists) {
                return CompletableFuture.completedFuture(OpResult.stateMismatch("Cannot create photo in non-existent album"));
            } else {
                this.photos.put(photo.getId(), photo);
                this.albumPhotoIds.put(photo.getAlbumId(), photo.getId());
                return CompletableFuture.completedFuture(OpResult.created());
            }
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    @Override
    public CompletableFuture<OpResult> updatePhoto(Photo photo) {
        this.lock.writeLock().lock();
        try {
            Photo existingPhoto = this.photos.get(photo.getId());
            boolean photoExists = existingPhoto != null;
            boolean albumExists = this.albums.containsKey(photo.getAlbumId());

            if (!photoExists) {
                return CompletableFuture.completedFuture(OpResult.nonExistent());
            } else if (!albumExists) {
                return CompletableFuture.completedFuture(OpResult.stateMismatch("Cannot move photo to non-existent album"));
            } else {
                this.photos.put(photo.getId(), photo);
                this.albumPhotoIds.remove(existingPhoto.getAlbumId(), existingPhoto.getId());
                this.albumPhotoIds.put(photo.getAlbumId(), photo.getId());
                return CompletableFuture.completedFuture(OpResult.updated());
            }
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    @Override
    public CompletableFuture<OpResult> deletePhoto(Long photoId) {
        this.lock.writeLock().lock();
        try {
            Photo existingPhoto = this.photos.get(photoId);
            boolean photoExists = existingPhoto != null;

            if (!photoExists) {
                return CompletableFuture.completedFuture(OpResult.nonExistent());
            } else {
                this.photos.remove(photoId);
                this.albumPhotoIds.remove(existingPhoto.getAlbumId(), photoId);
                return CompletableFuture.completedFuture(OpResult.deleted());
            }
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    //------------------------------------------------------------------------------------------------------------------

    private Optional<AlbumContent> getContent(Long albumId) {
        Optional<Album> album = Optional.ofNullable(this.albums.get(albumId));
        Optional<AlbumContent> albumContent = album.map(a -> {
            ImmutableList<Photo> albumPhotos = this.albumPhotoIds.get(albumId).stream()
                    .map(this.photos::get)
                    .collect(ImmutableCollectors.toList());
            return new AlbumContent(albumId, albumPhotos);
        });
        return albumContent;
    }
}
