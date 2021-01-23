import java.net.URI;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Page implements Comparable<Page> {

    // Set upon construction
    private final DocRef docRef;
    private final DocRef origin;
    private URI uri;
    private int seq;
    private boolean webPageRetrieved = false;
    private boolean pdfRetrieved = false;

    // Available after all retrieve methods are called
    private CompletableFuture<HttpResponse<String>> webPage = null;
    private CompletableFuture<HttpResponse<byte[]>> pdf = null;

    Page(DocRef docRef, DocRef origin, int seq, LocalDate date) {
        this.docRef = docRef;
        this.origin = origin;
        this.uri = new NewsBankURIBuilder()
                .setRoute(NewsBankURIBuilder.NewsBankRoutes.DOCUMENT_VIEW)
                .setT(date)
                .setFormat()
                .setDocRef(docRef)
                .setOrigin(origin)
                .build();
        this.seq = seq;
    }

    void requestWebPage(CustomHttpClient client) {
        this.webPage = client.sendGETAsyncRequest(this.uri, HttpResponse.BodyHandlers.ofString());
        this.webPageRetrieved = true;
    }

    void requestPDFPage(CustomHttpClient client) {
        if(!this.webPageRetrieved) throw new IllegalStateException("You must call `retrieveWebPage() before calling this method.");
        CustomHttpClient.raiseIfStatusNot2xx(this.webPage.join());
        this.pdf = client.sendGETAsyncRequest(parsePdfUri(), HttpResponse.BodyHandlers.ofByteArray());
        this.pdfRetrieved = true;
    }

    byte[] getPDF() {
        if(!this.pdfRetrieved) throw new IllegalStateException("You must call `retrievePDFPage()` before calling this method.");
        HttpResponse<byte[]> response = this.pdf.join();
        CustomHttpClient.raiseIfStatusNot2xx(response);
        return response.body();
    }

    URI parsePdfUri() {
        String body = this.webPage.join().body();
        Pattern pattern = Pattern.compile("request:\\{imageServer:\"([^\"]*)\"");
        Matcher matcher = pattern.matcher(body);
        if(!matcher.find()) throw new IllegalArgumentException("The jpg url could not be found in the page body.");
        String jpgUrl = matcher.group(1);
        String pdfUrl = jpgUrl
                .replaceAll("color_jpeg_\\d{1,2}", "color_pdf")
                .replace(".jpg", ".pdf");
        return URI.create(pdfUrl);
    }

    @Override
    public int compareTo(Page page) {
        return Integer.compare(seq, page.seq);
    }
}
