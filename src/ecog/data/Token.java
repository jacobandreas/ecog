package ecog.data;

/**
 * @author jda
 */
public class Token {

    public final String label;
    public final int beginFrame;
    public final int endFrame;

    public Token(String label, int beginFrame, int endFrame) {
        this.label = label;
        this.beginFrame = beginFrame;
        this.endFrame = endFrame;
    }

    public String toString() {
        return "Token(" + this.label + ")";
    }
}
