package io.webgraph.warc.cli;

import io.webgraph.warc.Warc;
import io.webgraph.warc.WarcRecord;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.google.common.collect.EvictingQueue;

public class Main {

    public static void main(String[] args) throws IOException {
        String subcommand = args[0];

        InputStream input = args.length > 1 ? new FileInputStream(args[1]) : System.in;
        OutputStream output = System.out;

        if (subcommand.equals("head"))

            try (Warc.Reader reader = new Warc.Reader(input);
                 Warc.Writer writer = new Warc.Writer(output)) {

                reader.stream()
                    .limit(10)
                    .forEach(writer::write);
            }

        else if (subcommand.equals("tail"))

            try (Warc.Reader reader = new Warc.Reader(input);
                 Warc.Writer writer = new Warc.Writer(output)) {

                EvictingQueue<WarcRecord> tail = EvictingQueue.create(3);

                reader.forEach(tail::add);
                tail.forEach(writer::write);
            }

        else throw new IllegalArgumentException("unknown subcommand: " + subcommand);
    }
}
