package io.webgraph.warc.cli;

import io.webgraph.warc.Warc;
import io.webgraph.warc.WarcRecord;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.google.common.collect.EvictingQueue;

import java.util.List;

import static java.lang.String.format;
import static java.lang.System.*;

public class Main {

    public static void main(String[] args) throws IOException {

        List<CommandLine> commands = new CommandLine(new WarcCommand())
            .setUnmatchedArgumentsAllowed(true)
            .addSubcommand("head", new WarcHead())
            .addSubcommand("tail", new WarcTail())
            .parse(args);

        CommandLine warc = commands.remove(0);
        int status = ((WarcCommand) warc.getCommand()).run(warc, commands, warc.getUnmatchedArguments());
        exit(status);
    }


    @Command(synopsisHeading = "usage: ")
    static abstract class BaseCommand {

    }


    @Command(synopsisHeading = "usage: @|bold warc |@")
    static abstract class SubCommand {

        public abstract int run() throws IOException;
    }


    @Command(
        name = "warc",
        commandListHeading = "%nsubcommands:%n",
        customSynopsis = "warc [--help] [--version] <subcommand> [<args>]",
        footerHeading = "%n")
    static class WarcCommand extends BaseCommand {

        @Option(names = "--help", help = true)
        boolean help;

        @Option(names = "--version", help = true)
        boolean version;

        public int run(CommandLine command, List<CommandLine> subcommands, List<String> unmatchedArguments) throws IOException {

            if (version) {
                out.println("0.1.0");
                return 0;
            }

            if (help) {
                command.usage(out);
                return 0;
            }

            if (!unmatchedArguments.isEmpty()) {
                String arg = unmatchedArguments.get(0);
                out.println(format("unknown %s: %s", arg.startsWith("-") ? "option" : "command", arg));
                command.usage(out);
                return 1;
            }

            if (subcommands.isEmpty()) {
                command.usage(out);
                return 1;
            }

            return ((SubCommand) subcommands.get(0).getCommand()).run();
        }
    }


    @Command(
        name = "head",
        description = "display the last 3 warc records of <file> or STDIN")
    static class WarcHead extends SubCommand {

        @Option(names = "--help", help = true)
        boolean help;

        @Parameters(arity = "0..1")
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


    @Command(
        name = "tail",
        description = "display the first 10 warc records from <file> or STDIN")
    static class WarcTail extends SubCommand {

        @Option(names = "--help", help = true)
        boolean help;

        @Parameters(arity = "0..1")
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
}
