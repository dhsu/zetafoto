# ZetaFoto

Overview
--------

Sample REST API using [Dropwizard](http://www.dropwizard.io/) that can be used for
learning and reference.

Prerequisites
-------------

* Java 8+
* Maven

Build and Run Locally
---------------------

From the root:
* `mvn clean package` to build the application
* `java -jar target/zetafoto-0.1-SNAPSHOT.jar server config.yml` to start
* `mvn test` to run unit tests
* Visit `http://localhost:8001/` to see application metrics

API
---

A Postman Collection is available in the repo at [tools/postman/zetafoto.postman_collection](tools/postman/zetafoto.postman_collection). If you use it, don't forget to add the Postman
environment variable, `zetafoto-url`, with value, `http://localhost:8000`.

### Initialize
This should not be exposed, but as a toy problem, it provides a way to get sample
data into the system.
* URL: `/zetafoto/v1/admin/init`
* Method: `POST`
* Success Response:
  * Code: 200

### Get Album
* URL: `/zetafoto/v1/albums/{albumId}`
* Method: `GET`
* URL Params: album ID
* Success Response:
  * Code: 200
    ```
    /* Example Body */
    {
      "id": 123,
      "userId": 456,
      "title": "Bufferflies and Hurricanes"
    }
    ```
* Error Responses:
  * Code: 404 if album does not exist

### Get Album Content
* URL: `/zetafoto/v1/albums/{albumId}/content`
* Method: `GET`
* URL Params: album ID
* Success Response:
  * Code: 200
    ```
    /* Example Body */
    {
      "albumId": 33,
      "photos": [
        {
          "id": 1601,
          "albumId": 123,
          "title": "Tabula Rasa",
          "url": "http://placehold.it/600/dd678",
          "thumbnailUrl": "http://placehold.it/150/dd678"
        }
      ]
    },
    ```
* Error Responses:
  * Code: 404 if album does not exist

### List All Albums
Realistically, this would not be part of the public API, and if it were,
it should have paging.
* URL: `/zetafoto/v1/albums`
* Method: `GET`
* Success Response:
  * Code: 200
    ```
    /* Example Body */
    [
      {
        "id": 1,
        "userId": 1,
        "title": "Please Hammer, Don't Hurt 'em"
      }
    ]
    ```

### Create Album
* URL: `/zetafoto/v1/albums`
* Method: `POST`
* Headers:
  * `Content-Type: application/json`
* Success Response:
  * Code: 201
    ```
    /* Example Body */
    {
      "id": 123,
      "userId": 456,
      "title": "Bufferflies and Hurricanes"
    }
    ```
* Error Responses:
  * Code: 409 if album with same ID already exists

### Update Album
* URL: `/zetafoto/v1/albums`
* Method: `PUT`
* Headers:
  * `Content-Type: application/json`
* Success Response:
  * Code: 200
    ```
    /* Example Body */
    {
      "id": 123,
      "userId": 456,
      "title": "Bufferflies and Hurricanes"
    }
    ```
* Error Responses:
  * Code: 404 if album does not exist
  * Code: 400 if user ID is not the same

### Delete Album
* URL: `/zetafoto/v1/albums/{albumId}`
* Method: `DELETE`
* URL Params: album ID
* Success Response:
  * Code: 204
* Error Responses:
  * Code: 404 if album does not exist
  * Code: 400 if album is not empty

### Get Photo
* URL: `/zetafoto/v1/photos/{photoId}`
* Method: `GET`
* URL Params: photo ID
* Success Response:
  * Code: 200
    ```
    /* Example Body */
    {
      "id": 7687686,
      "albumId": 108,
      "title": "Persephone and Hades",
      "url": "http://placehold.it/600/acff72",
      "thumbnailUrl": "http://placehold.it/150/acff72"
    }
    ```
* Error Responses:
  * Code: 404 if photo does not exist

### Create Photo
* URL: `/zetafoto/v1/photos`
* Method: `POST`
* Headers:
  * `Content-Type: application/json`
* Success Response:
  * Code: 201
    ```
    /* Example Body */
    {
      "id": 7687686,
      "albumId": 108,
      "title": "Persephone and Hades",
      "url": "http://placehold.it/600/acff72",
      "thumbnailUrl": "http://placehold.it/150/acff72"
    }
    ```
* Error Responses:
  * Code: 409 if photo with same ID already exists

### Update Photo
* URL: `/zetafoto/v1/photos`
* Method: `PUT`
* Headers:
  * `Content-Type: application/json`
* Success Response:
  * Code: 200
    ```
    /* Example Body */
    {
      "id": 7687686,
      "albumId": 108,
      "title": "Persephone and Hades 2",
      "url": "http://placehold.it/600/acff72",
      "thumbnailUrl": "http://placehold.it/150/acff72"
    }
    ```
* Error Responses:
  * Code: 404 if album does not exist
  * Code: 400 if creating a photo in a non-existent album

### Delete Photo
* URL: `/zetafoto/v1/photos/{photoId}`
* Method: `DELETE`
* URL Params: photo ID
* Success Response:
  * Code: 204
* Error Responses:
  * Code: 404 if photo does not exist

Notes
-----

* Implemented in-memory data source for debugging. This would obviously be
  insufficient for a real-world application.
* No user management implemented. User IDs are assumed to be valid.
* No authentication/authorization implemented.