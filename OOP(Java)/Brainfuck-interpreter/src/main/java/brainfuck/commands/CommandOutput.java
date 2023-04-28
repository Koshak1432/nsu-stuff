package brainfuck.commands;

import brainfuck.structures.components.Context;

import java.io.IOException;

public class CommandOutput implements ICommand {
    @Override
    public void execute(Context ctx) throws IOException {
        ctx.getIoController().print(ctx.getOutStream(), (char)ctx.getPointer().getValue());
    }
}
