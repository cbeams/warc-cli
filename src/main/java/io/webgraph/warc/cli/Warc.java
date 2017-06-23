package io.webgraph.warc.cli;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.IOException;

import java.util.List;

import static java.lang.String.format;
import static java.lang.System.*;

@Command(
    name = "warc",
    commandListHeading = "%nsubcommands:%n",
    customSynopsis = "warc [--help] [--version] <subcommand> [<args>]",
    footerHeading = "%n"
)
class Warc {

    @Option(
        names = {"-h", "--help"},
        description = "Display this help text",
        help = true
    )
    boolean help;

    @Option(
        names = "--version",
        description = "Display the version",
        help = true
    )
    boolean version;

    public int run(CommandLine commandLine, CommandLine subcommandLine) throws IOException {

        if (version) {
            out.println("0.1.0");
            return 0;
        }

        if (help) {
            commandLine.usage(out);
            return 0;
        }

        List<String> unmatchedArgs = commandLine.getUnmatchedArguments();

        if (!unmatchedArgs.isEmpty()) {
            String arg = unmatchedArgs.get(0);
            out.println(format("unknown %s: %s", arg.startsWith("-") ? "option" : "command", arg));
            commandLine.usage(out);
            return 1;
        }

        return ((Subcommand) subcommandLine.getCommand()).run();
    }


    @Command(
        synopsisHeading = "usage: @|bold warc |@"
    )
    abstract static class Subcommand {

        @Option(
            names = {"-h", "--help"},
            help = true,
            description = "Display this help text"
        )
        boolean help;

        public int run() throws IOException {

            if (help) {
                CommandLine.usage(this, out);
                return 0;
            }

            return doRun();
        }

        public abstract int doRun() throws IOException;
    }
}
