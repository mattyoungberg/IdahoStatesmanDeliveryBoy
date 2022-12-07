import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;

public class Main {

    public final static String LIBRARY_ID_ENV_VAR = "LIBRARY_ID";

    public static void main(String[] args) throws Exception {

        // Check env for library ID
        String library_id = System.getenv(LIBRARY_ID_ENV_VAR);
        if (library_id == null) {
            throw new NullPointerException("system propery " + LIBRARY_ID_ENV_VAR + " not found");
        }

        // Create an HTTP client
        HttpClientDirector clientDirector = new HttpClientDirector(HttpClient.newBuilder());
        clientDirector.construct();
        HttpClient httpClient = clientDirector.getClient();

        // Get auth request and run on client
        AuthenicationRequestDirector authDirector = new AuthenicationRequestDirector(
            HttpRequest.newBuilder(), 
            library_id
        );
        authDirector.construct();
        HttpResponse<Void> authResponse = httpClient.send(authDirector.getRequest(), HttpResponse.BodyHandlers.discarding());
        if (authResponse.statusCode() / 100 != 2) {
            throw new InternalError("did not receive 2xx response during client authentication");
        }

        // Get today's date to coordinate requests during runtime
        LocalDate today = LocalDate.now();

        
        // OriginPage originPage = PageHTMLRequester.getOriginPage(options.getDate(), client);

        // PageIndex pageIndex = new PageIndex(originPage, client);
        // List<PageHTMLRequester> pageHTMLRequesters = pageIndex.requestIndex();

        // for (PageHTMLRequester pageHTMLRequester : pageHTMLRequesters)
        //     pageHTMLRequester.start();

        // List<PagePDFRequester> pagePDFRequesters = new ArrayList<>();
        // for (PageHTMLRequester pageHTMLRequester : pageHTMLRequesters) {
        //     URI pdfLocation = pageHTMLRequester.join();
        //     PagePDFRequester pagePDFRequester = new PagePDFRequester(pdfLocation, client);
        //     pagePDFRequesters.add(pagePDFRequester);
        //     pagePDFRequester.start();
        // }

        // List<byte[]> pdfs = new ArrayList<>();
        // for(PagePDFRequester pagePDFRequester : pagePDFRequesters)
        //     pdfs.add(pagePDFRequester.join());

        // PDFIssue pdfIssue = new PDFIssue(pdfs);
        // pdfIssue.save(options.getTarget(), options.getFileName());
    }
}
