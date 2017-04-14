package org.krayne;

import io.dropwizard.Application;
import io.dropwizard.client.HttpClientBuilder;
import io.dropwizard.client.HttpClientConfiguration;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.apache.http.client.HttpClient;
import org.krayne.datasource.PhotoAlbumDataSource;
import org.krayne.datasource.inmemory.InMemoryPhotoAlbumDataSource;
import org.krayne.init.Initializer;
import org.krayne.resource.AdminResource;
import org.krayne.resource.AlbumResource;
import org.krayne.resource.PhotoResource;
import org.krayne.resource.response.JsonProcessingExceptionMapper;

public class ZetaFotoApplication extends Application<ZetaFotoConfiguration> {

    public static void main(final String[] args) throws Exception {
        new ZetaFotoApplication().run(args);
    }

    @Override
    public String getName() {
        return "ZetaFoto";
    }

    @Override
    public void initialize(final Bootstrap<ZetaFotoConfiguration> bootstrap) {
    }

    @Override
    public void run(final ZetaFotoConfiguration config, final Environment environment) {
        // data sources
        PhotoAlbumDataSource photoAlbumDataSource = new InMemoryPhotoAlbumDataSource();

        // register exception mappers
        environment.jersey().register(JsonProcessingExceptionMapper.class);

        // register health checks
        //environment.healthChecks().register("dynamodb", new DynamoDbHealthCheck(dynamoEndpoint));

        // initializer
        HttpClientConfiguration httpClientConfiguration = config.getHttpClientConfiguration();
        HttpClient httpClient = new HttpClientBuilder(environment).using(httpClientConfiguration).build(getName());
        Initializer initializer = new Initializer(environment.getObjectMapper(), photoAlbumDataSource, httpClient);

        // register resources
        environment.jersey().register(new AlbumResource(photoAlbumDataSource));
        environment.jersey().register(new PhotoResource(photoAlbumDataSource));
        environment.jersey().register(new AdminResource(photoAlbumDataSource, initializer, config.getInitialAlbumsUrl(), config.getInitialPhotosUrl()));
    }
}
