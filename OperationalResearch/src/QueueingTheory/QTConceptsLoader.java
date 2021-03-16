package QueueingTheory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class QTConceptsLoader {
    public static Properties conceptsMap;
    static {
        conceptsMap = new Properties();
        InputStream is= QTConceptsLoader.class.getClassLoader().getResourceAsStream("QTConcepts.properties");
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(is, StandardCharsets.UTF_8);
            conceptsMap.load(inputStreamReader);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Fail to load concepts!");
        }
    }

    public static void main(String[] args) {
        System.out.println(QTConceptsLoader.conceptsMap);
    }
}
