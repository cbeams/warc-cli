package io.webgraph.warc.cli;

import io.webgraph.warc.WarcReader;
import io.webgraph.warc.WarcWriter;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static java.lang.System.out;

@Command(
    name = "cat",
    description = "Concatenate and print WARC files"
)
class WarcCat extends Warc.Subcommand {

    @Parameters(
        arity = "0..*",
        paramLabel = "file",
        description = "The WARC file(s) to concatenate"
    )
    File[] files = new File[0];

    @Override
    public int doRun() throws IOException {

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

        try (WarcReader reader = new WarcReader(in);
             WarcWriter writer = new WarcWriter(out)) {

            reader.forEach(writer::write);
        }
    }
}
