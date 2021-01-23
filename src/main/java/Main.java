import org.apache.commons.cli.*;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {

        // Create and handle command line arguments

        Options options = buildOptions();
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd;
        try {
            cmd = parser.parse(options, args);
        } catch(ParseException e) {
            System.out.println("Parsing of command line arguments failed. Reason: " + e.getMessage());
            return;
        }

        if(cmd.hasOption('h')) {
            HelpFormatter formatter = new HelpFormatter();
            String header = "Downloads and saves an issue of the Idaho Statesman made freely available through the Boise Library's relationship with Newsbank.";
            String footer = "Please report any problems at https://www.github.com/mattyoungberg/IdahoStatesmanDeliveryBoy.";
            formatter.printHelp("Idaho Statesman Delivery Boy", header, options, footer);
            return;
        }

        if(!cmd.hasOption('i')) {
            System.out.println("Parsing of command line arguments failed. Reason: Missing required option: id");
            return;
        }
        long id = Long.parseLong(cmd.getOptionValue('i'));
        LocalDate date = null;
        Path target = null;
        String fileName = null;
        if(cmd.hasOption('d')) {
            date = LocalDate.parse(cmd.getOptionValue('d'), DateTimeFormatter.BASIC_ISO_DATE);
        } else {
            date = LocalDate.now();
        }
        if(cmd.hasOption('t')) {
            target = Path.of(cmd.getOptionValue('t'));
        } else {
            target = Path.of(System.getProperty("user.dir"));
        }
        if(cmd.hasOption('f')) {
            fileName = cmd.getOptionValue('f');
        } else {
            fileName = "IdahoStatesman_" + date.format(DateTimeFormatter.BASIC_ISO_DATE) + ".pdf";
        }

        // Run the application

        CustomHttpClient client = new CustomHttpClient(id);
        client.authenticate();

        PageIndex pageIndex = new PageIndex(client, date);
        pageIndex.requestIndex();
        List<Page> pages = pageIndex.getPages();

        List<byte[]> pdfs = new ArrayList<>();
        for(Page page : pages)
            page.requestWebPage(client);  // Async request; waits on completable future
        for(Page page : pages)
            page.requestPDFPage(client);  // Async request; waits on completable future
        for(Page page : pages)
            pdfs.add(page.getPDF());

        PDFIssue issue = new PDFIssue(pdfs);
        issue.save(target, fileName);
    }

    public static Options buildOptions() {
        Options options = new Options();

        Option id = Option.builder("i")
                .argName("-i")
                .longOpt("id")
                .desc("required; A valid, 14-digit Boise library card ID number")
                .hasArg(true)
                .numberOfArgs(1)
                .required(false)  // true, but need to allow a singular -h option to be given
                .build();

        Option date = Option.builder("d")
                .argName("-d")
                .longOpt("date")
                .desc("the date of the issue to request (yyyyMMdd); defaults to today")
                .hasArg(true)
                .numberOfArgs(1)
                .required(false)
                .build();

        Option target = Option.builder("t")
                .argName("-t")
                .longOpt("target")
                .desc("The target directory at which to save the PDF output; defaults to the current working directory")
                .hasArg(true)
                .numberOfArgs(1)
                .required(false)
                .build();

        Option help = Option.builder("h")
                .argName("-h")
                .longOpt("help")
                .desc("Prints this message")
                .hasArg(false)
                .required(false)
                .build();

        Option fileName = Option.builder("f")
                .argName("-f")
                .longOpt("fileName")
                .desc("A custom name for the output file; defaults to IdahoStatesman_yyyyMMdd")
                .hasArg(true)
                .numberOfArgs(1)
                .required(false)
                .build();

        options.addOption(id);
        options.addOption(date);
        options.addOption(target);
        options.addOption(help);

        return options;
    }
}
