package fr.aumgn.bukkit.command;

public class CommandUsageError extends RuntimeException {
    private static final long serialVersionUID = -2455651120183829047L;

    public CommandUsageError() {
        super();
    }

    public CommandUsageError(String message) {
        super(message);
    }
}