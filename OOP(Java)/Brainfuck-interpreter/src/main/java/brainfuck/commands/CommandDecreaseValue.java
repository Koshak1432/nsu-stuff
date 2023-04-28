package brainfuck.commands;

import brainfuck.structures.components.Context;

public class CommandDecreaseValue implements ICommand {
    @Override
    public void execute(Context ctx) {
        ctx.getPointer().setPointer((byte)(ctx.getPointer().getValue() - 1));
    }
}
