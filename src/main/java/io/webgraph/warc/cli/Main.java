package io.webgraph.warc.cli;

import picocli.CommandLine;

import java.io.IOException;

import java.util.List;

import static java.lang.System.exit;
import static java.lang.System.out;

public class Main {

    public static void main(String[] args) throws IOException {
        exit(run(args));
    }

    public static int run(String[] args) throws IOException {

        for (int i = 0; i < args.length; i++)
            if (args[i].matches("-\\d+"))
                args[i] = "-n=" + args[i].substring(1);

        List<CommandLine> commandLines = new CommandLine(new Warc())
            .setUnmatchedArgumentsAllowed(true)
            .addSubcommand("cat", new WarcCat())
            .addSubcommand("count", new WarcCount())
            .addSubcommand("head", new WarcHead())
            .addSubcommand("tail", new WarcTail())
            .parse(args);

        CommandLine commandLine = commandLines.get(0);

        if (commandLines.size() != 2) {
            commandLine.usage(out);
            return 1;
        }

        CommandLine subcommandLine = commandLines.get(1);

        Warc warc = (Warc) commandLine.getCommand();
        return warc.run(commandLine, subcommandLine);
    }
}
