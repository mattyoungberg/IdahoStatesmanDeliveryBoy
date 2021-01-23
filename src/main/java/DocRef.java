import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DocRef {

    private final String publicationId;
    private final String issueId;
    private final String pageId;

    DocRef(String publicationId, String issueId, String pageId) {
        this.publicationId = publicationId;
        this.issueId = issueId;
        this.pageId = pageId;
    }

    String getIssueId() {
        return issueId.substring(0, issueId.indexOf('@'));
    }

    static DocRef fromString(String string) {
        Matcher docRefMatcher = createDocRefMatcher(string);
        if(!docRefMatcher.matches())
            throw new IllegalArgumentException("Provided string does not match docRef format");
        return new DocRef(docRefMatcher.group(1), docRefMatcher.group(2), docRefMatcher.group(3));
    }

    private static Matcher createDocRefMatcher(String string) {
        Pattern docRefPattern = Pattern.compile("^(?:image/)?v2:([\\w@]+)-([\\w@]+)-([\\w@]+)$");
        return docRefPattern.matcher(string);
    }

    @Override
    public String toString() {
        return "v2:" + publicationId + "-" + issueId + "-" + pageId;
    }
}
