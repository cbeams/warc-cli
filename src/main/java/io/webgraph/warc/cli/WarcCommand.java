package io.webgraph.warc.cli;

import picocli.CommandLine;

import java.io.IOException;

import java.util.List;

import static java.lang.String.format;
import static java.lang.System.out;

@CommandLine.Command(
    name = "warc",
    commandListHeading = "%nsubcommands:%n",
    customSynopsis = "warc [--help] [--version] <subcommand> [<args>]",
    footerHeading = "%n")
class WarcCommand extends BaseCommand {

    @CommandLine.Option(names = "--help", help = true)
    boolean help;

    @CommandLine.Option(names = "--version", help = true)
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
