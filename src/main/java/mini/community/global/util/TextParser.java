package mini.community.global.util;

import org.springframework.util.ObjectUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class TextParser {
    public TextParser() {}

    public static List<String> doSplitCode(String str){
        if (ObjectUtils.isEmpty(str)) {
            return Collections.emptyList();
        }

        String[] splitResults = str.replace(" ","").split(",");
        return Arrays.stream(splitResults).distinct().collect(Collectors.toList());
    }
}
