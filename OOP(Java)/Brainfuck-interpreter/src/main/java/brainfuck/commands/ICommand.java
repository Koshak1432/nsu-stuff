package brainfuck.commands;

import brainfuck.structures.components.Context;

import java.io.IOException;

public interface ICommand {
    void execute(Context ctx) throws IllegalArgumentException, IllegalStateException, IOException;
}
