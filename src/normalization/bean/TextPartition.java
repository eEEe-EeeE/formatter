package normalization.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 一个partition包含类型相同的block
 * 可以先构建partition再构建blocks，也可以先构建blocks再构建partition
 * Partition的类型取决于blocks
 * blocks已标记类型<tt>type中的一种</tt>则Partition必标记类型<tt>type中的一种</tt>
 * Partition未标记类型<tt>null</tt>则blocks未标记类型<tt>null</tt>
 * 6个字段，生成区时type，pure和blocks可以暂不设定
 * 6个字段，合并块时beginPattern，endPattern可以暂不设定
 * type和pure一起设定，type和pure设定了blocks必设定
 * 生成的partition可以暂不划分block
 */
public class TextPartition extends TextSegment {

    private boolean isPure;

    private List<TextBlock> blocks = new ArrayList<>();

    public TextPartition() {
    }

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

    public List<TextBlock> getBlocks() {
        return blocks;
    }

    public List<String> getLines() {
        return super.getLines();
    }

    public boolean isPure() {
        return isPure;
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

    public void setBlocks(List<TextBlock> blocks) {
        this.blocks = blocks;
    }

    public void setLines(List<String> lines) {
        super.setLines(lines);
    }

    public void setPure(boolean pure) {
        isPure = pure;
    }

    /* Other */
    public boolean typeIsNotNull() {
        return super.typeIsNotNull();
    }

    public void appendBlock(TextBlock block) {
        getBlocks().add(block);
    }

    public void appendLines(List<String> lines) {
        getLines().addAll(lines);
    }

    /**
     * 用从混合型partition提取出指定类型单一型partition
     * @param type 单一型partition的类型
     * @return 有该类型则返回partition，否则返回null
     */
    public TextPartition getPurePartition(Type type) {
        if (typeIsNotNull()) {
            if (isPure())
                return this;
            else {

                List<TextBlock> pureBlocks = new ArrayList<>();

                List<String> pureLines = new ArrayList<>();

                for (TextBlock block : getBlocks()) {

                    if (block.getType().equals(type)) {

                        pureBlocks.add(block);

                        pureLines.addAll(block.getLines());
                    }
                }

                TextPartition pureTextPartition = makePartition(getBeginPattern(), getEndPattern(), pureLines);

                pureTextPartition.setType(type);

                pureTextPartition.setBlocks(pureBlocks);

                return pureTextPartition;
            }
        } else {
            return null;
        }
    }

    /**
     * 拼装partition
     * @param partitionBegin
     * @param partitionEnd
     * @param lines
     * @return
     */
    public static TextPartition makePartition(Pattern partitionBegin, Pattern partitionEnd, List<String> lines) {

        TextPartition textPartition = new TextPartition();

        textPartition.setBeginPattern(partitionBegin);

        textPartition.setEndPattern(partitionEnd);

        textPartition.setLines(lines);

        return textPartition;
    }

}
