package brainfuck.commands;

import brainfuck.structures.components.Context;

public class CommandMoveRight implements ICommand {
    @Override
    public void execute(Context ctx) {
        ctx.getPointer().movePointer(ctx.getPointer().getIdx() + 1);
    }
}
