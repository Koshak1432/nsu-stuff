package brainfuck.structures.components;

public interface IPointer {
    void movePointer(int idx);
    void setPointer(byte value);
    byte getValue();
    int getIdx();
}
