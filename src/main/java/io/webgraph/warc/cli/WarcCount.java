package io.webgraph.warc.cli;

import io.webgraph.warc.WarcReader;

import org.iokit.core.IOKitException;

import picocli.CommandLine;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import java.util.Arrays;

import static java.lang.String.format;
import static java.lang.System.out;

@CommandLine.Command(
    name = "count",
    description = "Count number of records in a WARC file"
)
class WarcCount extends Warc.Subcommand {

    @CommandLine.Option(
        names = {"-h", "--help"},
        help = true,
        description = "Display this help text"
    )
    boolean help;

    @CommandLine.Parameters(
        arity = "0..*",
        paramLabel = "file",
        description = "The file(s) to concatenate"
    )
    File[] files = new File[0];

    @Override
    public int run() {

        if (help) {
            CommandLine.usage(this, out);
            return 0;
        }

        if (files.length == 0) {
            countRecords(System.in);
            return 0;
        }

        long total = Arrays.stream(files)
            .mapToLong(this::countRecords)
            .sum();

        if (files.length > 1)
            out.println(format("%8d total", total));

        return 0;
    }

    private void countRecords(InputStream in) {
        countRecords(in, null);
    }

    private long countRecords(File file) {
        try {
            return countRecords(new FileInputStream(file), file.getPath());
        } catch (FileNotFoundException ex) {
            throw new IOKitException(ex);
        }
    }

    private long countRecords(InputStream in, String path) {

        String formattedPath = path == null ? "" : format(" %s", path);

        try (WarcReader reader = new WarcReader(in)) {
            reader.forEach(record -> {
                if (reader.getReadCount() % 100 == 0)
                    out.print(format("%8d%s\r", reader.getReadCount(), formattedPath));
            });
            out.println(format("%8d%s", reader.getReadCount(), formattedPath));
            return reader.getReadCount();
        }
    }
}
