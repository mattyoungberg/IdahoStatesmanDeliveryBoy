import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

class NewsBankURIBuilder {

    private NewsBankRoutes route = null;
    private String t = null;
    private boolean format = false;
    private DocRef DocRef = null;
    private DocRef origin = null;
    private String issueId = null;
    private String url = null;
    private boolean query = false;
    private String auth = null;

    enum NewsBankRoutes {

        ISSUE_BROWSE("issue-browse"),
        DOCUMENT_VIEW("document-view"),
        INDEX("nb-sitelinks/imageviewer/toc/index.xml");

        private static final String PROXY_HOST = "https://infoweb-newsbank-com.proxy.boisepubliclibrary.org/apps/news/";
        private final String file;

        NewsBankRoutes(String file) {
            this.file = file;
        }

        public String getBaseUrl() {
            return PROXY_HOST + file;
        }

    }

    NewsBankURIBuilder setRoute(NewsBankRoutes route) {
        this.route = route;
        return this;
    }

    NewsBankURIBuilder setT(LocalDate date) {
        this.t = composeT(date);
        return this;
    }

    NewsBankURIBuilder setFormat() {
        this.format = true;
        return this;
    }

    NewsBankURIBuilder setDocRef(DocRef DocRef) {
        this.DocRef = DocRef;
        return this;
    }

    NewsBankURIBuilder setOrigin(DocRef origin) {
        this.origin = origin;
        return this;
    }

    NewsBankURIBuilder setIssueId(DocRef DocRef) {
        this.issueId = DocRef.getIssueId();
        return this;
    }

    NewsBankURIBuilder setUrl(DocRef DocRef) {
        this.url = "image/" + DocRef.toString();
        return this;
    }

    NewsBankURIBuilder setQuery() {
        this.query = true;
        return this;
    }

    NewsBankURIBuilder setAuth(String authToken) {
        this.auth = authToken;
        return this;
    }

    URI build() {
        raiseIfRouteNotSet();
        String url = route.getBaseUrl() +
                getPParamString() +
                getOptionalTParamString() +
                getOptionalFormatParamString() +
                getOptionalDocRefParamString() +
                getOptionalOriginParamString() +
                getOptionalIssueIdParamString() +
                getOptionalUrlParamString() +
                getOptionalQueryParamString() +
                getOptionalAuthParamString();
        return URI.create(url);
    }

    private String getPParamString() {
        String paramName = route != NewsBankRoutes.INDEX ? "p" : "product";
        return "?" + paramName + "=WORLDNEWS";
    }

    private String getOptionalTParamString() {
        return t != null ? "&t=" + t : "";
    }

    private String getOptionalFormatParamString() {
        return format ? "&format=image" : "";
    }

    private String getOptionalDocRefParamString() {
        return DocRef != null ? "&docref=image/" + DocRef.toString() : "";
    }

    private String getOptionalOriginParamString() {
        return origin != null ? "&origin=image/" + origin.toString() : "";
    }

    private String getOptionalIssueIdParamString() {
        return issueId != null ? "&issueid=" + issueId : "";
    }

    private String getOptionalUrlParamString() {
        return url != null ? "&url=" + url : "";
    }

    private String getOptionalQueryParamString() {
        return query ? "&query=" : "";
    }

    private String getOptionalAuthParamString() {
        return auth != null ? "&auth=" + auth : "";
    }

    private static String composeT(LocalDate date) {
        return getTParamPubname() + "/" + getTParamYear(date) + "/" + getTParamMonthDay(date);
    }

    private static String getTParamPubname() {
        return "pubname:" +
                URLEncoder.encode("ISIM-EEDT", StandardCharsets.UTF_8) +
                "!" +
                URLEncoder.encode("Idaho Statesman, The (Boise, ID)", StandardCharsets.UTF_8);
    }

    private static String getTParamYear(LocalDate date) {
        DateTimeFormatter yearFormat = DateTimeFormatter.ofPattern("yyyy");
        return "year:" +
                date.format(yearFormat) +
                "!" +
                date.format(yearFormat);  // Redundant, I know, but it's what the query wants
    }

    private static String getTParamMonthDay(LocalDate date) {
        return "mody:" +
                date.format(DateTimeFormatter.ofPattern("MMdd")) +
                "!" +
                URLEncoder.encode(date.format(DateTimeFormatter.ofPattern("MMMM dd")), StandardCharsets.UTF_8);

    }

    private void raiseIfRouteNotSet() {
        if(route == null)
            throw new IllegalStateException("`setRoute()` must be called before building");
    }
}