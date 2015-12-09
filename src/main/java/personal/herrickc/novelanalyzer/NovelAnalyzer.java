package personal.herrickc.novelanalyzer;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.xml.sax.SAXException;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Created by herrickc on 12/6/15.
 */
public class NovelAnalyzer {

    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException, TransformerException, ClassNotFoundException {
        String inputFile = "/home/herrickc/Classes/21L512/Beloved.pdf";
        String outputFile = "/home/herrickc/Classes/21L512/Beloved.xml";
        String jsonFile = "/home/herrickc/Classes/21L512/Beloved.json";
        PDFToTextConverter converter = new PDFToTextConverter();
        converter.convert(inputFile, outputFile);

        Novel novel = new Novel(outputFile);
        novel.clean();
        novel.write(outputFile + ".cleaned");

        NERTagger tagger = new NERTagger();
        for (Page page : novel.getPages()) {
            int pageID = page.getId();
            for (Paragraph paragraph : page.getParagraphs()) {
                int paragraphID = paragraph.getId();
                Map<Integer, List<Pair<String, String>>> results = tagger.tag(paragraph.getText());
                for (Map.Entry<Integer, List<Pair<String, String>>> entry : results.entrySet()) {
                    int sentenceID = entry.getKey();
                    List<Pair<String, String>> entityData = entry.getValue();
                    for (Pair<String, String> entityDatum : entityData) {
                        String entityType = entityDatum.getKey();
                        String entityName = entityDatum.getValue();
                        Entity entity = new Entity(entityName, entityType);
                        entity.addMention(sentenceID, paragraphID, pageID);
                        novel.addEntity(entity);
                    }
                }
            }
        }

        Map<Entity, List<Pair<Entity, Integer>>> coocurrences = novel.getCoocurrences();

        // print everything to json
        JsonArrayBuilder nodeBuilder = Json.createArrayBuilder();
        List<Entity> entities = novel.getEntityList();
        for (Entity entity : entities) {
            int group=0;
            if (entity.getType().equals("PERSON")) {
                group = 1;
            } else if (entity.getType().equals("LOCATION")) {
                group = 2;
            } else if (entity.getType().equals("ORGANIZATION")) {
                group = 3;
            }
            nodeBuilder.add(Json.createObjectBuilder()
                    .add("name", entity.getName())
                    .add("group", group));

        }

        JsonArrayBuilder linkBuilder = getLinkBuilder(coocurrences, entities);


        JsonObject json = Json.createObjectBuilder()
                .add("nodes", nodeBuilder)
                .add("links", linkBuilder).build();

        FileWriter jsonWriter = new FileWriter(jsonFile);
        jsonWriter.write(json.toString());
        jsonWriter.close();


    }

    private static JsonArrayBuilder getLinkBuilder(Map<Entity, List<Pair<Entity, Integer>>> coocurrences, List<Entity> entities) {
        JsonArrayBuilder linkBuilder = Json.createArrayBuilder();
        for (int i=0; i<entities.size(); i++) {
            Entity e1 = entities.get(i);
            List<Pair<Entity, Integer>> potentialLinks = coocurrences.get(e1);
            Collections.sort(potentialLinks, new Comparator<Pair<Entity, Integer>>() {
                public int compare(Pair<Entity, Integer> o1, Pair<Entity, Integer> o2) {
                    return o2.getValue().compareTo(o1.getValue());
                }
            });
            for (int j=0; j<Math.ceil(potentialLinks.size()*1.0/10); j++) {
                Pair<Entity, Integer> link = potentialLinks.get(j);
                linkBuilder.add(Json.createObjectBuilder()
                        .add("source", i)
                        .add("target", entities.indexOf(link.getKey()))
                        .add("value", link.getValue()));
            }
        }

        return linkBuilder;
    }
}
