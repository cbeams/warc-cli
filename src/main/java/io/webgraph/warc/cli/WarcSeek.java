package io.webgraph.warc.cli;

import io.webgraph.warc.WarcReader;
import io.webgraph.warc.WarcWriter;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.io.IOException;

import static java.lang.System.out;

@Command(
    name = "seek",
    description = "Display the record at a given offset in a WARC file"
)
class WarcSeek extends Warc.Subcommand {

    @Option(
        names = {"-o", "--offset"},
        description = "The offset (in bytes) of the record to read",
        required = true
    )
    long offset = 0;

    @Parameters(
        arity = "1",
        description = "The WARC file to read"
    )
    File file;

    @Override
    public int doRun() throws IOException {

        try (WarcReader reader = new WarcReader(file);
             WarcWriter writer = new WarcWriter(out)) {

            reader.seek(offset);
            writer.write(reader.read());
        }

        return 0;
    }
}
