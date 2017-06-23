package io.webgraph.warc.cli;

import picocli.CommandLine;

import java.io.IOException;

import java.util.List;

import static java.lang.System.exit;

public class Main {

    public static void main(String[] args) throws IOException {

        preprocess(args);

        List<CommandLine> commands = new CommandLine(new WarcCommand())
            .setUnmatchedArgumentsAllowed(true)
            .addSubcommand("head", new WarcHead())
            .addSubcommand("tail", new WarcTail())
            .parse(args);

        CommandLine warc = commands.remove(0);
        int status = ((WarcCommand) warc.getCommand()).run(warc, commands, warc.getUnmatchedArguments());
        exit(status);
    }

    private static void preprocess(String[] args) {
        for (int i = 0; i < args.length; i++)
            if (args[i].matches("-\\d+"))
                args[i] = "-n=" + args[i].substring(1);
    }
}
