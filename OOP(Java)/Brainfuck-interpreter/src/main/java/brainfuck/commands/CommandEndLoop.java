package brainfuck.commands;

import brainfuck.structures.components.Context;

public class CommandEndLoop implements ICommand {
    @Override
    public void execute(Context ctx) throws IllegalArgumentException, IllegalStateException {
        if (0 != ctx.getPointer().getValue()) {
            ctx.getProgram().jumpTo(ctx.getProgram().findMatchingBracket(false));
        }
    }
}
