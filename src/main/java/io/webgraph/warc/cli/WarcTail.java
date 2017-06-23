package io.webgraph.warc.cli;

import io.webgraph.warc.WarcReader;
import io.webgraph.warc.WarcRecord;
import io.webgraph.warc.WarcWriter;

import picocli.CommandLine;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.google.common.collect.EvictingQueue;

import static java.lang.System.out;

@CommandLine.Command(
    name = "tail",
    description = "Display last records of a WARC file"
)
class WarcTail extends Warc.Subcommand {

    @CommandLine.Option(
        names = "-n",
        description = "The number of records to display"
    )
    int count = 10;

    @CommandLine.Parameters(
        arity = "0..1",
        description = "The WARC file to read"
    )
    File file;

    @Override
    public int doRun() throws IOException {

        InputStream input = file != null ? new FileInputStream(file) : System.in;
        OutputStream output = out;

        try (WarcReader reader = new WarcReader(input);
             WarcWriter writer = new WarcWriter(output)) {

            EvictingQueue<WarcRecord> records = EvictingQueue.create(count);

            reader.forEach(records::add);
            records.forEach(writer::write);
        }

        return 0;
    }
}
