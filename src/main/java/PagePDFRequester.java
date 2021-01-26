import java.net.URI;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PagePDFRequester {

    private final CustomHttpClient client;
    private final URI uri;
    private CompletableFuture<HttpResponse<byte[]>> completableFuture = null;

    PagePDFRequester(URI uri, CustomHttpClient client) {
        this.client = client;
        this.uri = uri;
    }

    static URI getPDFUrlFromPageHttpResponse(HttpResponse<String> pageHttpResponse) {
        Pattern pattern = Pattern.compile("request:\\{imageServer:\"([^\"]*)\"");
        Matcher matcher = pattern.matcher(pageHttpResponse.body());
        if(!matcher.find())
            throw new IllegalArgumentException("HTML body does not match expected pattern.");
        String jpgUrl = matcher.group(1);
        String pdfUrl = transformJPGUrlToPDFUrl(jpgUrl);
        return URI.create(pdfUrl);
    }

    void start() {
        if (completableFuture != null)
            return;
        completableFuture = client.sendGETAsyncRequest(uri, HttpResponse.BodyHandlers.ofByteArray());
    }

    byte[] join() {
        if (completableFuture == null)
            throw new IllegalStateException("The requester has not been started. Call `start()` first.");
        return completableFuture.join().body();
    }

    static String transformJPGUrlToPDFUrl(String jpgUrl) {
        return jpgUrl.replaceAll("color_jpeg_\\d{1,2}", "color_pdf").replace(".jpg", ".pdf");
    }
}
