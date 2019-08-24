package normalization.bean;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Block应该是最小分类单位，包含特定类型的连续文本行，只对文本分类，不对文本描述的对象分类
 * 可以先构建partition再构建blocks，也可以先构建blocks再构建partition
 * 4个字段，生成块时只有type可以暂不设定
 */

public class TextBlock extends TextSegment {

    /* Getter */
    public Type getType() {
        return super.getType();
    }

    public Pattern getBeginPattern() {
        return super.getBeginPattern();
    }

    public Pattern getEndPattern() {
        return super.getEndPattern();
    }

    public List<String> getLines() {
        return super.getLines();
    }

    /* Setter */
    public void setType(Type type) {
        super.setType(type);
    }

    public void setBeginPattern(Pattern beginPattern) {
        super.setBeginPattern(beginPattern);
    }

    public void setEndPattern(Pattern endPattern) {
        super.setEndPattern(endPattern);
    }

    public void setLines(List<String> lines) {
        super.setLines(lines);
    }

    /* Other */
    public void appendLine(String line) {
        getLines().add(line);
    }

    public boolean typeIsNotNull() {
        return super.typeIsNotNull();
    }

    /**
     * 拼装block
     * @param blockBegin
     * @param blockEnd
     * @param lines
     * @return
     */
    public static TextBlock makeBlock(Pattern blockBegin, Pattern blockEnd, List<String> lines) {

        TextBlock block = new TextBlock();

        block.setBeginPattern(blockBegin);

        block.setEndPattern(blockEnd);

        block.setLines(lines);

        return block;
    }
}
