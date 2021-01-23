import java.io.IOException;
import java.net.URI;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class PageIndex {

    // Set upon construction
    private final CustomHttpClient client;
    private final LocalDate date;
    private boolean retrieved = false;

    // Available after `requestIndex()`
    private String authToken = null;
    private DocRef origin = null;
    private List<Page> pages = null;

    public PageIndex(CustomHttpClient client, LocalDate date) {
        this.client = client;
        this.date = date;
    }

    void requestIndex() throws IOException, InterruptedException {
        if(this.retrieved) return;
        getFrontPage();  // Sets this.authToken and this.origin
        getIndex();  // Sets this.pages
        this.retrieved = true;
    }

    List<Page> getPages() {
        if(!retrieved) throw new IllegalStateException("`retrieveIndex()` must be called before you can call this method.");
        return pages;
    }

    private void getFrontPage() throws IOException, InterruptedException {
        URI frontPageURI = buildFrontPageURI();
        HttpResponse<String> response = client.sendGETSyncRequest(frontPageURI, HttpResponse.BodyHandlers.ofString());
        CustomHttpClient.raiseIfStatusNot2xx(response);
        this.authToken = parseAuthToken(response);
        this.origin = parseDocRef(response);
    }

    private URI buildFrontPageURI() {
        return new NewsBankURIBuilder()
                .setRoute(NewsBankURIBuilder.NewsBankRoutes.ISSUE_BROWSE)
                .setT(this.date)
                .setFormat()
                .build();
    }

    private void getIndex() throws IOException, InterruptedException {
        URI indexURI = buildIndexURI();
        HttpResponse<String> response2 = client.sendGETSyncRequest(indexURI, HttpResponse.BodyHandlers.ofString());
        CustomHttpClient.raiseIfStatusNot2xx(response2);
        this.pages = parseXML(response2);
    }

    private URI buildIndexURI() {
        return new NewsBankURIBuilder()
                .setRoute(NewsBankURIBuilder.NewsBankRoutes.INDEX)
                .setIssueId(this.origin)
                .setUrl(this.origin)
                .setQuery()
                .setAuth(this.authToken)
                .build();
    }

    private static String parseAuthToken(HttpResponse<String> response) {
        String regex = "\"authToken\":\"([^\"]*)\"";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(response.body());
        if(!matcher.find()) throw new IllegalArgumentException("`response` does not have the correct form.");
        return matcher.group(1);
    }

    private static DocRef parseDocRef(HttpResponse<String> response) {
        String query = response.uri().getQuery();
        String regex = "docref=(image/v2:[\\w@]+-[\\w@]+-[\\w@]+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(query);
        if(!matcher.find()) throw new IllegalArgumentException("`response` does not have the correct form.");
        return DocRef.fromString(matcher.group(1));
    }

    private List<Page> parseXML(HttpResponse<String> response) {
        List<Page> pages = new ArrayList<>();

        Pattern pattern = Pattern.compile("<page.*?seq=\"(\\d+)\".*?docref=\"(v2:[^\"]*)\"");
        Matcher matcher = pattern.matcher(response.body());

        while(matcher.find()) {
            Page page = new Page(DocRef.fromString(matcher.group(2)), origin, Integer.parseInt(matcher.group(1)), date);
            pages.add(page);
        }

        return pages;
    }
}
