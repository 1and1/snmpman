package com.oneandone.snmpman;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.kohsuke.args4j.Option;

import java.io.File;

/** The command-line options for the {@link Snmpman} application. */
@Slf4j
public final class CommandLineOptions {

    /** The {@code Snmpman} configuration. */
    @Option(name = "-c", aliases = "--configuration", usage = "the path to the configuration XML", required = true)
    @Getter private File configurationFile;
    
    /** Flag that defines that a help message should be displayed when {@code true}. */
    @Option(name = "-h", aliases = "--help", usage = "print the help message", help = true)
    @Getter private boolean showHelp = false;
}
