import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {

        RuntimeOptions options = new RuntimeOptions(args);

        if (options.includesHelpFlag()) {
            options.getHelpMenu();
            return;
        }

        CustomHttpClient client = new CustomHttpClient(options.getLibraryId());
        client.authenticate();

        OriginPage originPage = PageHTMLRequester.getOriginPage(options.getDate(), client);

        PageIndex pageIndex = new PageIndex(originPage, client);
        List<PageHTMLRequester> pageHTMLRequesters = pageIndex.requestIndex();

        for (PageHTMLRequester pageHTMLRequester : pageHTMLRequesters)
            pageHTMLRequester.start();

        List<PagePDFRequester> pagePDFRequesters = new ArrayList<>();
        for (PageHTMLRequester pageHTMLRequester : pageHTMLRequesters) {
            URI pdfLocation = pageHTMLRequester.join();
            PagePDFRequester pagePDFRequester = new PagePDFRequester(pdfLocation, client);
            pagePDFRequesters.add(pagePDFRequester);
            pagePDFRequester.start();
        }

        List<byte[]> pdfs = new ArrayList<>();
        for(PagePDFRequester pagePDFRequester : pagePDFRequesters)
            pdfs.add(pagePDFRequester.join());

        PDFIssue pdfIssue = new PDFIssue(pdfs);
        pdfIssue.save(options.getTarget(), options.getFileName());
    }
}
