package io.webgraph.warc.cli;

import picocli.CommandLine;

import java.io.IOException;

@CommandLine.Command(synopsisHeading = "usage: @|bold warc |@")
abstract class SubCommand {

    public abstract int run() throws IOException;
}
