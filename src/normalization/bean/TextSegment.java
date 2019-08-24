package normalization.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public abstract class TextSegment {

    private Type type;

    private Pattern beginPattern;

    private Pattern endPattern;

    private List<String> lines = new ArrayList<>();

    /* Getter */
    public Type getType() {
        return type;
    }

    public Pattern getBeginPattern() {
        return beginPattern;
    }

    public Pattern getEndPattern() {
        return endPattern;
    }

    public List<String> getLines() {
        return lines;
    }

    /* Setter */
    public void setType(Type type) {
        this.type = type;
    }

    public void setBeginPattern(Pattern beginPattern) {
        this.beginPattern = beginPattern;
    }

    public void setEndPattern(Pattern endPattern) {
        this.endPattern = endPattern;
    }

    public void setLines(List<String> lines) {
        this.lines = lines;
    }

    public boolean typeIsNotNull() {
        return this.type != null;
    }
}
