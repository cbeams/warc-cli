package io.webgraph.warc.cli;

import io.webgraph.warc.Warc;

import picocli.CommandLine;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static java.lang.String.format;
import static java.lang.System.out;

@CommandLine.Command(
    name = "count",
    description = "Count number of records in a WARC file")
class WarcCount extends SubCommand {

    @CommandLine.Option(
        names = {"-h", "--help"},
        help = true,
        description = "Display this help text")
    boolean help;

    @CommandLine.Parameters(
        arity = "0..*",
        paramLabel = "file",
        description = "The file(s) to concatenate")
    File[] files = new File[0];

    @Override
    public int run() throws IOException {

        if (help) {
            CommandLine.usage(this, out);
            return 0;
        }

        if (files.length == 0) {
            try (Warc.Reader reader = new Warc.Reader(System.in)) {
                out.println(format("%8d", reader.stream().count()));
            }
            return 0;
        }

        long total = 0;
        for (File file : files) {
            try (Warc.Reader reader = new Warc.Reader(new FileInputStream(file))) {
                out.println(format("%8d %s", reader.stream().count(), file.getPath()));
                total += reader.getReadCount();
            }
        }
        out.println(format("%8d total", total));

        return 0;
    }
}
