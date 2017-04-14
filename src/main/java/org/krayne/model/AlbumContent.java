package org.krayne.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;

import java.util.List;

public class AlbumContent {
    @JsonProperty("albumId") private final Long albumId;
    @JsonProperty("photos") private final ImmutableList<Photo> photos;

    public AlbumContent(Long albumId, List<Photo> photos) {
        this.albumId = albumId;
        this.photos = ImmutableList.copyOf(photos);
    }

    public Long getAlbumId() {
        return this.albumId;
    }

    public ImmutableList<Photo> getPhotos() {
        return this.photos;
    }
}
