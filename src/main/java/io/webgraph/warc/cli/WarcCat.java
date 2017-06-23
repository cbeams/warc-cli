package io.webgraph.warc.cli;

import io.webgraph.warc.Warc;

import picocli.CommandLine;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static java.lang.System.out;

@CommandLine.Command(
    name = "cat",
    description = "Concatenate and print WARC files")
class WarcCat extends SubCommand {

    @CommandLine.Option(
        names = {"-h", "--help"},
        help = true,
        description = "Display this help text")
    boolean help;

    @CommandLine.Parameters(
        arity = "0..*",
        paramLabel = "file",
        description = "The WARC file(s) to concatenate")
    File[] files = new File[0];

    @Override
    public int run() throws IOException {

        if (help) {
            CommandLine.usage(this, out);
            return 0;
        }

        if (files.length == 0) {
            cat(System.in);
            return 0;
        }

        for (File file : files) {
            cat(new FileInputStream(file));
        }

        return 0;
    }

    private void cat(InputStream in) {

        try (Warc.Reader reader = new Warc.Reader(in);
             Warc.Writer writer = new Warc.Writer(out)) {

            reader.forEach(writer::write);
        }
    }
}
