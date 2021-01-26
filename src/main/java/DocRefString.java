import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DocRefString {

    private final String publicationId;
    private final String issueId;
    private final String pageId;

    DocRefString(String publicationId, String issueId, String pageId) {
        this.publicationId = publicationId;
        this.issueId = issueId;
        this.pageId = pageId;
    }

    static DocRefString fromString(String string) {
        Pattern pageRefPattern = Pattern.compile("^(?:image/)?v2:([\\w@]+)-([\\w@]+)-([\\w@]+)$");
        Matcher pageRefMatcher = pageRefPattern.matcher(string);
        if(!pageRefMatcher.matches())
            throw new IllegalArgumentException("Provided string does not match docRef format");
        return new DocRefString(pageRefMatcher.group(1), pageRefMatcher.group(2), pageRefMatcher.group(3));
    }

    String getIssueId() {
        return issueId.substring(0, issueId.indexOf('@'));
    }

    String getDocRefStringRepresentation() {
        return "v2:" + publicationId + "-" + issueId + "-" + pageId;
    }
}
