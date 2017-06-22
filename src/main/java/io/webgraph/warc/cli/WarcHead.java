package io.webgraph.warc.cli;

import io.webgraph.warc.Warc;

import picocli.CommandLine;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static java.lang.System.out;

@CommandLine.Command(
    name = "head",
    description = "display the last 3 warc records of <file> or STDIN")
class WarcHead extends SubCommand {

    @CommandLine.Option(names = "--help", help = true)
    boolean help;

    @CommandLine.Parameters(arity = "0..1")
    File file;

    @Override
    public int run() throws IOException {

        if (help) {
            CommandLine.usage(this, out);
            return 0;
        }

        InputStream input = file != null ? new FileInputStream(file) : System.in;
        OutputStream output = out;

        try (Warc.Reader reader = new Warc.Reader(input);
             Warc.Writer writer = new Warc.Writer(output)) {

            reader.stream()
                .limit(1)
                .forEach(writer::write);
        }

        return 0;
    }
}
