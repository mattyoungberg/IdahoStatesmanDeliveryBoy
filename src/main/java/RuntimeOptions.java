import org.apache.commons.cli.*;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

class RuntimeOptions {

    private final Options options;
    private final long libraryId;
    private final LocalDate date;
    private final Path target;
    private final String fileName;

    public RuntimeOptions(String[] args) throws ParseException {
        this.options = buildOptions();
        CommandLine cmd = parseOptions(args, options);
        this.libraryId = Long.parseLong(cmd.getOptionValue('i'));
        this.date = cmd.hasOption('d') ? LocalDate.parse(cmd.getOptionValue('d'), DateTimeFormatter.BASIC_ISO_DATE) : LocalDate.now();
        this.target = cmd.hasOption('t') ? Path.of(cmd.getOptionValue('t')) : Path.of(System.getProperty("user.dir"));
        this.fileName = cmd.hasOption('f') ? cmd.getOptionValue('f') : "IdahoStatesman_" + date.format(DateTimeFormatter.BASIC_ISO_DATE) + ".pdf";
    }

    static CommandLine parseOptions(String[] args, Options options) throws ParseException {
        CommandLineParser parser = new DefaultParser();
        return parser.parse(options, args);
    }

    static Options buildOptions() {
        Options options = new Options();
        options.addOption(buildLibraryIdOption());
        options.addOption(buildDateOption());
        options.addOption(buildTargetDirectoryOption());
        options.addOption(buildFileNameOption());
        options.addOption(buildHelpOption());
        return options;
    }

    static void getHelpMenu() {
        HelpFormatter formatter = new HelpFormatter();
        String header = "Downloads and saves an issue of the Idaho Statesman made freely available through the Boise Library's relationship with Newsbank.";
        String footer = "Please report any problems at https://www.github.com/mattyoungberg/IdahoStatesmanDeliveryBoy.";
        formatter.printHelp("Idaho Statesman Delivery Boy", header, buildOptions(), footer);
    }

    long getLibraryId() {
        return libraryId;
    }

    LocalDate getDate() {
        return date;
    }

    Path getTarget() {
        return target;
    }

    String getFileName() {
        return fileName;
    }

    static boolean checkForHelp(String[] args) throws ParseException {
        Options options = new Options();
        options.addOption(buildHelpOption());
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);
        return cmd.hasOption('h');
    }

    static Option buildLibraryIdOption() {
        return Option.builder("i")
                .argName("-i")
                .longOpt("id")
                .desc("required; A valid, 14-digit Boise library card ID number")
                .hasArg(true)
                .numberOfArgs(1)
                .required(true)
                .build();
    }

    static Option buildDateOption() {
        return Option.builder("d")
                .argName("-d")
                .longOpt("date")
                .desc("the date of the issue to request (yyyyMMdd); defaults to today")
                .hasArg(true)
                .numberOfArgs(1)
                .required(false)
                .build();
    }

    static Option buildTargetDirectoryOption() {
        return Option.builder("t")
                .argName("-t")
                .longOpt("target")
                .desc("The target directory at which to save the PDF output; defaults to the current working directory")
                .hasArg(true)
                .numberOfArgs(1)
                .required(false)
                .build();
    }

    static Option buildFileNameOption() {
        return Option.builder("f")
                .argName("-f")
                .longOpt("fileName")
                .desc("A custom name for the output file; defaults to IdahoStatesman_yyyyMMdd")
                .hasArg(true)
                .numberOfArgs(1)
                .required(false)
                .build();
    }

    static Option buildHelpOption() {
        return Option.builder("h")
                .argName("-h")
                .longOpt("help")
                .desc("Prints this message")
                .hasArg(false)
                .required(false)
                .build();
    }
}
