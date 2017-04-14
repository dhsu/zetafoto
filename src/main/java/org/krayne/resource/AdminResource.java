package org.krayne.resource;

import com.codahale.metrics.annotation.Timed;
import org.krayne.datasource.PhotoAlbumDataSource;
import org.krayne.init.Initializer;
import org.krayne.resource.response.Responses;
import org.krayne.util.Futures;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;

@Path("/zetafoto/v1/admin")
public class AdminResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(AdminResource.class);

    private final PhotoAlbumDataSource photoAlbumDataSource;
    private final Initializer initializer;
    private final String initialAlbumsUrl;
    private final String initialPhotosUrl;

    public AdminResource(PhotoAlbumDataSource photoAlbumDataSource, Initializer initializer, String initialAlbumsUrl, String initialPhotosUrl) {
        this.photoAlbumDataSource = photoAlbumDataSource;
        this.initializer = initializer;
        this.initialAlbumsUrl = initialAlbumsUrl;
        this.initialPhotosUrl = initialPhotosUrl;
    }

    @POST
    @Timed
    @Path("/init")
    public void init(@Suspended AsyncResponse asyncResponse) {
        this.initializer.init(this.initialAlbumsUrl, this.initialPhotosUrl).whenComplete(Futures.handle(
            v -> asyncResponse.resume(Responses.ok()),
            throwable -> {
                LOGGER.error("Admin resource failed to initialize data", throwable);
                asyncResponse.resume(Responses.internalServerError());
            }
        ));
    }
}
