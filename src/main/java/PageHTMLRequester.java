import java.io.IOException;
import java.net.URI;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;

class PageHTMLRequester {

    private final CustomHttpClient client;
    private final PageRef pageRef;
    private final LocalDate date;
    private final OriginPage origin;
    private final URI uri;
    private CompletableFuture<HttpResponse<String>> completableFuture = null;

    PageHTMLRequester(PageRef pageRef, OriginPage origin, CustomHttpClient client) {
        this.client = client;
        this.pageRef = pageRef;
        this.date = pageRef.getDate();
        this.origin = origin;
        this.uri = createURI();
    }

    private URI createURI() {
        return new NewsBankURIBuilder()
                .setRoute(NewsBankURIBuilder.NewsBankRoutes.DOCUMENT_VIEW)
                .setT(date)
                .setFormat()
                .setDocRef(pageRef)
                .setOrigin(origin)
                .build();
    }

    void start() {
        if (completableFuture != null)
            return;
        completableFuture = client.sendGETAsyncRequest(uri, HttpResponse.BodyHandlers.ofString());
    }

    URI join() {
        if (completableFuture == null)
            throw new IllegalStateException("The requester has not been started. Call `start()` first.");
        return PagePDFRequester.getPDFUrlFromPageHttpResponse(completableFuture.join());
    }

    static OriginPage getOriginPage(LocalDate date, CustomHttpClient client) throws IOException, InterruptedException {
        OriginPage originPage = new OriginPage(date, client);
        originPage.request();
        return originPage;
    }
}
