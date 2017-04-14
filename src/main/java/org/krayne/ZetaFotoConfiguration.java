package org.krayne;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.client.HttpClientConfiguration;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class ZetaFotoConfiguration extends Configuration {
    @NotEmpty private final String version;
    @Valid @NotNull private final HttpClientConfiguration httpClientConfiguration;
    @NotEmpty private final String initialAlbumsUrl;
    @NotEmpty private final String initialPhotosUrl;

    public ZetaFotoConfiguration(@JsonProperty("version") String version,
                                 @JsonProperty("httpClient") HttpClientConfiguration httpClientConfiguration,
                                 @JsonProperty("initialAlbumsUrl") String initialAlbumsUrl,
                                 @JsonProperty("initialPhotosUrl") String initialPhotosUrl) {
        this.version = version;
        this.httpClientConfiguration = httpClientConfiguration;
        this.initialAlbumsUrl = initialAlbumsUrl;
        this.initialPhotosUrl = initialPhotosUrl;
    }

    public String getVersion() {
        return this.version;
    }

    public HttpClientConfiguration getHttpClientConfiguration() {
        return this.httpClientConfiguration;
    }

    public String getInitialAlbumsUrl() {
        return this.initialAlbumsUrl;
    }

    public String getInitialPhotosUrl() {
        return this.initialPhotosUrl;
    }
}