import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.http.HttpClient;
import java.time.Duration;

class HttpClientDirector {
    
    private HttpClient.Builder builder;
    
    HttpClientDirector(HttpClient.Builder builder) {
        this.builder = builder;
    }

    void construct() {

        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);

        this.builder
            .connectTimeout(Duration.ofSeconds(30))
            .version(HttpClient.Version.HTTP_1_1)
            .followRedirects(HttpClient.Redirect.ALWAYS)
            .cookieHandler(cookieManager);
    }

    HttpClient getClient() {
        return builder.build();
    }
}
