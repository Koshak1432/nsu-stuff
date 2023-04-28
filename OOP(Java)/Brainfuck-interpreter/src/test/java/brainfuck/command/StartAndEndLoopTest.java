package brainfuck.command;

import brainfuck.structures.components.Context;
import brainfuck.IOController;
import brainfuck.structures.components.Program;
import brainfuck.commands.CommandEndLoop;
import brainfuck.commands.CommandStartLoop;
import brainfuck.commands.ICommand;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class StartAndEndLoopTest {

    @Test
    void StartAndEndLoopTest() throws IOException {
        ICommand startLoop = new CommandStartLoop();
        ICommand endLoop = new CommandEndLoop();

        ctx.getPointer().setPointer((byte)1);
        startLoop.execute(ctx);
        assertEquals(0, ctx.getProgram().getIdx());

        ctx.getPointer().setPointer((byte)0);
        startLoop.execute(ctx);
        assertEquals(commands.length() - 1, ctx.getProgram().getIdx());

        endLoop.execute(ctx);
        assertEquals(commands.length() - 1, ctx.getProgram().getIdx());

        ctx.getPointer().setPointer((byte) 1);
        endLoop.execute(ctx);
        assertEquals(0, ctx.getProgram().getIdx());
    }

    private final String commands = "[>>+++++[-]]";
    private final Context ctx = new Context(System.in, System.out, new IOController(),
                                new Program(commands, '[', ']'));
}