import java.net.URI;
import java.net.http.HttpRequest;
import java.time.Duration;

class AuthenicationRequestDirector {

    private static final String PROXY_LOGIN_URL = "https://login.proxy.boisepubliclibrary.org/login/?user=";
    
    private HttpRequest.Builder builder;
    private String libraryId;

    AuthenicationRequestDirector(HttpRequest.Builder builder, String libraryId) {
        this.builder = builder;
        this.libraryId = libraryId;
    }

    void construct() {
        this.builder
            .POST(HttpRequest.BodyPublishers.noBody())
            .uri(URI.create(PROXY_LOGIN_URL + libraryId))
            .timeout(Duration.ofSeconds(30));
    }

    HttpRequest getRequest() {
        return builder.build();
    }

}
