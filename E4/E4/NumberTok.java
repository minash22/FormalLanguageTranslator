package E4;

public class NumberTok extends Token {
    public int value; // Assuming the value of the number is an integer

    public NumberTok(int tag, int value) {
        super(tag);
        this.value = value;
    }

    public String toString() {
        return "<" + tag + ", " + value + ">";
    }

    public static final NumberTok num = new NumberTok(Tag.NUM, 0);

}
