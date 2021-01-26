import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import java.io.IOException;
import java.io.InputStream;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

class CustomHttpClient {

    private final HttpClient client;
    private final long libraryId;
    private boolean authenticated;
    private URI authEndpointURI;
    private HttpRequest authHttpRequest;
    private HttpResponse<Void> authResponse;

    CustomHttpClient(long libraryId) {
        this.client = buildClient();
        this.libraryId = libraryId;
        this.authenticated = false;
    }

    void authenticate() throws IOException, InterruptedException {
        if(authenticated) return;
        setAuthEndpointURI();
        setAuthRequest();
        authResponse = client.send(authHttpRequest, HttpResponse.BodyHandlers.discarding());
        authenticated = verifyAuthResponse();
    }

    HttpClient getClient() {
        raiseIfNotAuthenticated();
        return client;
    }

    <T> HttpResponse<T> sendGETSyncRequest(URI uri, HttpResponse.BodyHandler<T> bodyHandler) throws IOException, InterruptedException {
        raiseIfNotAuthenticated();
        HttpRequest request = buildGetRequest(uri);
        return getClient().send(request, bodyHandler);
    }

    <T> CompletableFuture<HttpResponse<T>> sendGETAsyncRequest(URI uri, HttpResponse.BodyHandler<T> bodyHandler) {
        raiseIfNotAuthenticated();
        HttpRequest request = buildGetRequest(uri);
        return getClient().sendAsync(request, bodyHandler);
    }

    static void raiseIfStatusNot2xx(HttpResponse<?> response) {
        if(response.statusCode() / 100 != 2)
            throw new IllegalStateException("Request to " + response.uri().toString() + " returned an invalid response: " + response.statusCode());
    }

    private HttpClient buildClient() {
        return HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .sslContext(getSSLContext())
                .cookieHandler(new CookieManager(null, CookiePolicy.ACCEPT_ALL))
                .connectTimeout(Duration.ofSeconds(30L))
                .build();
    }


    private void setAuthRequest() {
        this.authHttpRequest = HttpRequest.newBuilder()
                .uri(authEndpointURI)
                .POST(HttpRequest.BodyPublishers.noBody())
                .timeout(Duration.ofMinutes(2))
                .build();
    }

    private void setAuthEndpointURI() {
        this.authEndpointURI = URI.create("https://login.proxy.boisepubliclibrary.org/login/?user=" + this.libraryId);
    }

    private boolean verifyAuthResponse() {
        return authResponse.statusCode() / 100 == 2;
    }

    private HttpRequest buildGetRequest(URI uri) {
        return HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .timeout(Duration.ofMinutes(2))
                .build();
    }

    private void raiseIfNotAuthenticated() {
        if(!authenticated)
            throw new IllegalStateException("`authenticate()` must be the first call on this object");
    }

    private SSLContext getSSLContext() {
        try {
            return createSSLContext();
        } catch (Exception e) {
            throw new RuntimeException("The necessary certificate could not be added to the CustomHttpClient");
        }
    }

    private SSLContext createSSLContext() throws NoSuchAlgorithmException, CertificateException, KeyStoreException, KeyManagementException, IOException {
        SSLContext context = SSLContext.getInstance("SSLv3");
        TrustManager[] trustManagers = createTrustManagers();
        context.init(null, trustManagers, null);
        return context;
    }

    private TrustManager[] createTrustManagers() throws NoSuchAlgorithmException, CertificateException, KeyStoreException, IOException {
        TrustManagerFactory factory = TrustManagerFactory.getInstance("PKIX");
        factory.init(getConfiguredKeyStore());
        return factory.getTrustManagers();
    }

    private KeyStore getConfiguredKeyStore() throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException {
        Certificate goDaddyCertificate = getGoDaddyCertificate();
        KeyStore keyStore = createNewKeystore();
        keyStore.setCertificateEntry("www.godaddy.com", goDaddyCertificate);
        return keyStore;
    }

    private KeyStore createNewKeystore() throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException {
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        char[] password = "password".toCharArray();
        keyStore.load(null, password);
        return keyStore;
    }

    private Certificate getGoDaddyCertificate() throws CertificateException {
        InputStream certStream = getClass().getClassLoader().getResourceAsStream("gdig2.crt");
        CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
        return certFactory.generateCertificate(certStream);
    }
}
