import java.io.IOException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OriginPage {

    private final LocalDate date;
    private final CustomHttpClient client;
    private final URI uri;
    private boolean requested = false;
    private String authToken;
    private PageRef pageRef;

    OriginPage(LocalDate date, CustomHttpClient client) {
        this.date = date;
        this.client = client;
        this.uri = buildURI();
    }

    private URI buildURI() {
        return new NewsBankURIBuilder()
                .setRoute(NewsBankURIBuilder.NewsBankRoutes.ISSUE_BROWSE)
                .setT(this.date)
                .setFormat()
                .build();
    }

    void request() throws IOException, InterruptedException {
        if(requested)
            return;
        HttpResponse<String> response = client.sendGETSyncRequest(uri, HttpResponse.BodyHandlers.ofString());
        CustomHttpClient.raiseIfStatusNot2xx(response);
        this.authToken = parseAuthToken(response);
        this.pageRef = PageRef.fromURLString(URLDecoder.decode(response.uri().toString(), StandardCharsets.UTF_8));
        this.requested = true;
    }

    LocalDate getDate() {
        return date;
    }

    String getAuthToken() {
        return authToken;
    }

    PageRef getPageRef() {
        return pageRef;
    }

    private static String parseAuthToken(HttpResponse<String> response) {
        String regex = "\"authToken\":\"([^\"]*)\"";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(response.body());
        if(!matcher.find()) throw new IllegalArgumentException("`response` does not have the correct form.");
        return matcher.group(1);
    }
}
