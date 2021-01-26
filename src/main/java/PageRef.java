import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PageRef {

    private final DocRefString docRefString;
    private final LocalDate date;

    PageRef(DocRefString docRefString, LocalDate date) {
        this.docRefString = docRefString;
        this.date = date;
    }

    static PageRef fromURLString(String url) {
        Pattern urlPattern = Pattern.compile("year:\\d{4}!(\\d{4})/mody:(\\d{2})(\\d{2}).*docref=(image/v2:[\\w@]+-[\\w@]+-[\\w@]+)");
        Matcher urlMatcher = urlPattern.matcher(url);
        if(!urlMatcher.find())
            throw new IllegalArgumentException("URL does not match expected pattern.");
        return parseMatcher(urlMatcher);
    }

    private static PageRef parseMatcher(Matcher matcher) {
        DocRefString docRefString = DocRefString.fromString(matcher.group(4));
        LocalDate date = LocalDate.of(
                Integer.parseInt(matcher.group(1)),
                Integer.parseInt(matcher.group(2)),
                Integer.parseInt(matcher.group(3))
        );
        return new PageRef(docRefString, date);
    }

    LocalDate getDate() {
        return date;
    }

    String getIssueId() {
        return docRefString.getIssueId();
    }

    String getDocRefString() {
        return docRefString.getDocRefStringRepresentation();
    }
}
