package io.webgraph.warc.cli;

import io.webgraph.warc.Warc;
import io.webgraph.warc.WarcRecord;

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
    description = "display the first 10 warc records from <file> or STDIN")
class WarcTail extends SubCommand {

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

            EvictingQueue<WarcRecord> records = EvictingQueue.create(3);

            reader.forEach(records::add);
            records.forEach(writer::write);
        }

        return 0;
    }
}