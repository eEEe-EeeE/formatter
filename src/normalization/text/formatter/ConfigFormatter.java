package normalization.text.formatter;

import normalization.bean.TextPartition;
import normalization.bean.Type;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConfigFormatter extends TextFormatter {

    @Override
    public Path format(Path configFilePath) {

        List<String> fileLines = readFile(configFilePath);

        Pattern begin = Pattern.compile("\t:");

        Pattern end = Pattern.compile("\t\\)|\t:\\w+ \\(\\)");

        List<TextPartition> textPartitions = findPartitions(fileLines, begin, end);

        Map<Type, Set<String>> categoryKeys = new HashMap<>();

        Type type;

        Set<String> keys;

        type = new Type("address");

        keys = new HashSet<>();

        keys.add("\t:network_objects");

        categoryKeys.put(type, keys);

        type = new Type("service");

        keys = new HashSet<>();

        keys.add("\t:services");

        categoryKeys.put(type, keys);

        classifySegments(textPartitions, categoryKeys, this.KEYS_MODE);

        Path outputFile = Paths.get(configFilePath.getParent() +"\\FORMAT_" + configFilePath.getFileName());

        List<String> formatFileLines = new ArrayList<>();

        for (TextPartition textPartition : textPartitions) {
            if (textPartition.typeIsNotNull())
                formatFileLines.addAll(textPartition.getLines());
        }

        writeFile(outputFile, formatFileLines);

        return null;
    }

    public static void main(String[] args) {
        Pattern pattern = Pattern.compile("((\\w+) )+");
        Matcher matcher = pattern.matcher("<>> djlje dlj jeow23 o32jo");
        matcher.find();
        System.out.println(matcher.group(1));
        DecimalFormat df = new DecimalFormat();


    }
}
