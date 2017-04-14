package org.krayne.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

public class Album {
    @JsonProperty("id") @NotNull private final Long id;
    @JsonProperty("userId") @NotNull private final Long userId;
    @JsonProperty("title") @NotEmpty private final String title;

    public Album(@JsonProperty("id") Long id, @JsonProperty("userId") Long userId, @JsonProperty("title") String title) {
        this.id = id;
        this.userId = userId;
        this.title = title;
    }

    public Long getId() {
        return this.id;
    }

    public Long getUserId() {
        return this.userId;
    }

    public String getTitle() {
        return this.title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Album album = (Album) o;

        if (!id.equals(album.id)) return false;
        if (!userId.equals(album.userId)) return false;
        return title.equals(album.title);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + userId.hashCode();
        result = 31 * result + title.hashCode();
        return result;
    }
}
