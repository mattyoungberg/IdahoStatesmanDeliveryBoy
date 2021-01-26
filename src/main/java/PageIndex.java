import java.io.IOException;
import java.net.URI;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class PageIndex {

    private final CustomHttpClient client;
    private final OriginPage originPage;

    PageIndex(OriginPage originPage, CustomHttpClient client) {
        this.client = client;
        this.originPage = originPage;
    }

    List<PageHTMLRequester> requestIndex() throws IOException, InterruptedException {
        URI uri = buildIndexURI();
        HttpResponse<String> response = client.sendGETSyncRequest(uri, HttpResponse.BodyHandlers.ofString());
        CustomHttpClient.raiseIfStatusNot2xx(response);
        List<DocRefString> docRefStrings = parseXML(response);
        return createPageHTMLRequesters(docRefStrings);
    }

    private URI buildIndexURI() {
        return new NewsBankURIBuilder()
                .setRoute(NewsBankURIBuilder.NewsBankRoutes.INDEX)
                .setIssueId(this.originPage.getPageRef())
                .setUrl(this.originPage.getPageRef())
                .setQuery()
                .setAuth(this.originPage.getAuthToken())
                .build();
    }

    private static List<DocRefString> parseXML(HttpResponse<String> response) {
        List<DocRefString> docRefStrings = new ArrayList<>();
        Pattern pattern = Pattern.compile("<page.*?docref=\"(v2:[^\"]*)\"");
        Matcher matcher = pattern.matcher(response.body());
        while(matcher.find()) {
            DocRefString docRefString = DocRefString.fromString(matcher.group(1));
            docRefStrings.add(docRefString);
        }
        return docRefStrings;
    }

    private List<PageHTMLRequester> createPageHTMLRequesters(List<DocRefString> docRefStrings) {
        List<PageHTMLRequester> pageHTMLRequesters = new ArrayList<>();
        for (DocRefString docRefString : docRefStrings) {
            PageRef pageRef = new PageRef(docRefString, originPage.getDate());
            PageHTMLRequester requester = new PageHTMLRequester(pageRef, originPage, client);
            pageHTMLRequesters.add(requester);
        }
        return pageHTMLRequesters;
    }
}
