package normalization.text.formatter;

import normalization.bean.TextBlock;
import normalization.bean.TextPartition;
import normalization.bean.TextSegment;
import normalization.bean.Type;

import java.io.*;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class TextFormatter {

    public final String EQUAL_MODE = "equal";
    public final String PREFIX_MODE = "prefix";
    public final String KEYS_MODE = "keys";

    /**
     * 格式化配置文件
     * @param configFilePath 配置文件对象，用于格式化
     * @return 格式化后的配置文件对象
     */
    abstract Path format(Path configFilePath);

    /**
     * 从磁盘读取文件
     * @param configFilePath 读取文件的字符流
     * @return 文件行List
     */
    List<String> readFile(Path configFilePath) {

        try {
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(configFilePath.toString()), "UTF-8"
                    )
            );

            String line;

            List<String> fileLines = new ArrayList<>();

            while ((line = br.readLine()) != null) {
                line += '\n';
                fileLines.add(line);
            }

            br.close();

            return fileLines;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 向磁盘写文件
     * @param configFilePath 配置文件
     * @param fileLines 文件行
     */
    void writeFile(Path configFilePath, List<String> fileLines) {

        try {
            BufferedWriter bw = new BufferedWriter(
                    new OutputStreamWriter(
                            new FileOutputStream(configFilePath.toString()), "UTF-8"
                    )
            );

            for (String line : fileLines) {
                bw.write(line);
            }

            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据关键字<tt>blockBegin</tt><tt>blockEnd</tt>从配置文件找到块，此时的块是未分类的，
     * 即不清楚与哪个object(地址对象，地址组对象，策略对象等)对应
     * @param fileLines 配置文件行
     * @param blockBegin 分块开始
     * @param blockEnd 分块结束
     * @return 未分类块 <tt>List</tt>
     */
    List<TextBlock> findBlocks(List<String> fileLines, Pattern blockBegin, Pattern blockEnd) {

        List<TextBlock> blocks = new ArrayList<>();

        List<String> cacheLines = new ArrayList<>();

        boolean isBlocking = false;

        for (String line : fileLines) {
            Matcher beginMatcher = blockBegin.matcher(line);
            Matcher endMatcher = blockEnd.matcher(line);

            if (isBlocking) {
                cacheLines.add(line);
                if (endMatcher.lookingAt()) {
                    isBlocking = false;
                    blocks.add(TextBlock.makeBlock(blockBegin, blockEnd, cacheLines));
                    cacheLines = new ArrayList<>();
                }
            } else {
                if (beginMatcher.lookingAt()) {
                    isBlocking = true;
                    cacheLines.add(line);
                    if (endMatcher.lookingAt()) {
                        isBlocking = false;
                        blocks.add(TextBlock.makeBlock(blockBegin, blockEnd, cacheLines));
                        cacheLines = new ArrayList<>();
                    }
                }

            }
        }

        return blocks;
    }

    /**
     * 从文本文件找出partitions
     * @param fileLines
     * @param partitionBegin
     * @param partitionEnd
     * @return
     */
    List<TextPartition> findPartitions(List<String> fileLines, Pattern partitionBegin, Pattern partitionEnd) {

        List<TextPartition> textPartitions = new ArrayList<>();

        List<String> cacheLines = new ArrayList<>();

        boolean isPartitioning = false;

        for (String line : fileLines) {
            Matcher beginMatcher = partitionBegin.matcher(line);
            Matcher endMatcher = partitionEnd.matcher(line);

            if (isPartitioning) {
                cacheLines.add(line);
                if (endMatcher.lookingAt()) {
                    isPartitioning = false;
                    textPartitions.add(TextPartition.makePartition(partitionBegin, partitionEnd, cacheLines));
                    cacheLines = new ArrayList<>();
                }
            } else {
                if (beginMatcher.lookingAt()) {
                    isPartitioning = true;
                    cacheLines.add(line);
                    if (endMatcher.lookingAt()) {
                        isPartitioning = false;
                        textPartitions.add(TextPartition.makePartition(partitionBegin, partitionEnd, cacheLines));
                        cacheLines = new ArrayList<>();
                    }
                }
            }
        }

        return textPartitions;
    }

    /**
     * 根据关键字<tt>keys</tt>将<tt>textSegments</tt>分类
     * @param textSegments 待分类的段，可以是blocks，也可以是partitions
     * @param typeKeys 分类关键字，是用户输入的
     */
    void classifySegments(List<? extends TextSegment> textSegments, Map<Type, Set<String>> typeKeys, String mode) {
        switch (mode) {
            case KEYS_MODE:

                Map<Type, List<Pattern>> typePatterns = getTypePatterns(typeKeys);

                // 虽然外层有两个for但是只把text file读了一遍，复杂度和text file行数成正比
                for (TextSegment textSegment : textSegments) {

                    markSegment(textSegment, typePatterns);
                }

                break;
            case EQUAL_MODE:
            case PREFIX_MODE:

                break;
        }
    }

    /**
     * 将关键字转为匹配模式
     * @param typeKeys 一个block中若存在以某种方式匹配(关键字匹配，前缀匹配)至少一个<tt>Type</tt>对应<tt>Set</tt>中的关键字的行，
     *                 则认为该block属于该type类型
     * @return 类型，模式Map，用于匹配block
     */
    private Map<Type, List<Pattern>> getTypePatterns(Map<Type, Set<String>> typeKeys) {

        Map<Type, List<Pattern>> typeMap = new HashMap<>();

        for (Map.Entry<Type, Set<String>> entry : typeKeys.entrySet()) {

            List<Pattern> patterns = new ArrayList<>();

            for (String key : entry.getValue()) {

                patterns.add(Pattern.compile(key));
            }

            typeMap.put(entry.getKey(), patterns);

        }
        return typeMap;
    }

    /**
     * 用于将未分类的<tt>textSegment</tt>标记为某种类型
     * @param textSegment 待标记的段
     * @param typeMap 用于标记的类型，模式Map
     */
    private void markSegment(TextSegment textSegment, Map<Type, List<Pattern>> typeMap) {

        for (String line : textSegment.getLines()) {

            // 定义的Type的数量一定是很少的，所以复杂度不会很高
            for (Map.Entry<Type, List<Pattern>> entry : typeMap.entrySet()) {

                for (Pattern pattern : entry.getValue()) {

                    Matcher matcher = pattern.matcher(line);

                    if (matcher.find()) {

                        textSegment.setType(entry.getKey());

                        return;
                    }
                }
            }
        }
    }


    /**
     * 将分类好的块放在一起，一个partition包含一个文件同一类别的所有块
     * @param blocks 已分类的块
     * @return 同类块的区域
     */
    List<TextPartition> mergeBlocks(List<TextBlock> blocks) {
        Map<Type, TextPartition> textPartitionMap = new HashMap<>();
        Type type;
        for (TextBlock block : blocks) {
            type = block.getType();
            if (textPartitionMap.containsKey(type)) {
                textPartitionMap.get(type).appendBlock(block);
            } else {

                TextPartition textPartition = new TextPartition();
                textPartition.setType(type);
                textPartition.setPure(true);
                textPartition.appendLines(block.getLines());
                textPartition.appendBlock(block);

                textPartitionMap.put(type, textPartition);
            }
        }

        List<TextPartition> textPartitions = new ArrayList<>();
        for (Map.Entry<Type, TextPartition> entry : textPartitionMap.entrySet()) {
            TextPartition textPartition = entry.getValue();
            textPartitions.add(textPartition);
        }

        return textPartitions;
    }





}
