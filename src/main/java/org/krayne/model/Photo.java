package org.krayne.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.URL;
import org.hibernate.validator.valuehandling.UnwrapValidatedValue;

import javax.validation.constraints.NotNull;
import java.util.Optional;

@JsonInclude(Include.NON_ABSENT)
public class Photo {
    @JsonProperty("id") @NotNull private final Long id;
    @JsonProperty("albumId") @NotNull private final Long albumId;
    @JsonProperty("title") @NotEmpty private final String title;
    @JsonProperty("url") @URL private final String url;
    @JsonProperty("thumbnailUrl") @UnwrapValidatedValue @URL private final Optional<String> thumbnailUrl;

    public Photo(@JsonProperty("id") Long id, @JsonProperty("albumId") Long albumId,
                 @JsonProperty("title") String title, @JsonProperty("url") String url,
                 @JsonProperty("thumbnailUrl") Optional<String> thumbnailUrl) {
        this.id = id;
        this.albumId = albumId;
        this.title = title;
        this.url = url;
        this.thumbnailUrl = thumbnailUrl;
    }

    public Long getId() {
        return this.id;
    }

    public Long getAlbumId() {
        return this.albumId;
    }

    public String getTitle() {
        return this.title;
    }

    public String getUrl() {
        return this.url;
    }

    public Optional<String> getThumbnailUrl() {
        return this.thumbnailUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Photo photo = (Photo) o;

        if (!id.equals(photo.id)) return false;
        if (!albumId.equals(photo.albumId)) return false;
        if (!title.equals(photo.title)) return false;
        if (!url.equals(photo.url)) return false;
        return thumbnailUrl.equals(photo.thumbnailUrl);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + albumId.hashCode();
        result = 31 * result + title.hashCode();
        result = 31 * result + url.hashCode();
        result = 31 * result + thumbnailUrl.hashCode();
        return result;
    }
}
