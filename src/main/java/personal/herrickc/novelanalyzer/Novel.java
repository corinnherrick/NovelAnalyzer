package personal.herrickc.novelanalyzer;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Created by herrickc on 12/6/15.
 */
public class Novel {

    private final List<Page> pages;
    private final Map<String, HashMap<String, Entity>> entities;

    public Novel(List<Page> pages) {
        this.pages = pages;

        this.entities = new HashMap<String, HashMap<String, Entity>>();
    }
    public Novel(String inputXMLFilename) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document doc = documentBuilder.parse(new File(inputXMLFilename));
        NodeList pageNodes = doc.getElementsByTagName("page");
        this.pages = new ArrayList<Page>();
        for (int i=0; i<pageNodes.getLength(); i++) {
            this.pages.add(new Page(pageNodes.item(i), this));
        }

        this.entities = new HashMap<String, HashMap<String, Entity>>();

    }

    // Returns true if the novel is valid (right now it always returns true)
    public boolean clean() {
        Iterator<Page> pageIterator = pages.iterator();
        while(pageIterator.hasNext()) {
            Page page = pageIterator.next();
            if (!page.clean()) {
                pageIterator.remove();
            }
        }
        return true;
    }

    public void write(String outputFilename) throws IOException {
        FileWriter fw = new FileWriter(outputFilename);
        write(fw);
    }

    public void write(FileWriter fw) throws IOException {
        fw.write("\n<novel>\n");
        for (Page page : this.pages) {
            page.write(fw);
        }
        fw.write("\n</novel>\n");
    }

    public int getPageId(Page page) {
        return pages.indexOf(page);
    }

    public List<Page> getPages() {
        return pages;
    }

    public void addEntity(Entity entity) {
        if (!entities.containsKey(entity.getType())) {
            entities.put(entity.getType(), new HashMap<String, Entity>());
        }
        if (entities.get(entity.getType()).containsKey(entity.getName())) {
            Triple<Integer, Integer, Integer> coords = (Triple<Integer, Integer, Integer>) entity.getMentions().toArray()[0];
            entities.get(entity.getType()).get(entity.getName()).addMention(coords.getLeft(), coords.getMiddle(), coords.getRight());
        } else {
            entities.get(entity.getType()).put(entity.getName(), entity);
        }
    }

    public List<Entity> getEntityList() {
        List<Entity> resultTemp = new ArrayList<Entity>();
        List<Entity> result = new ArrayList<Entity>();

        resultTemp.addAll(entities.get("PERSON").values());
        resultTemp.addAll(entities.get("LOCATION").values());
        resultTemp.addAll(entities.get("ORGANIZATION").values());
        for (Entity entity : resultTemp) {
            if (entity.getMentions().size() > 1) {
                result.add(entity);
            }
        }
        return result;
    }

    public Map<Entity, List<Pair<Entity, Integer>>> getCoocurrences() {
        Map<Entity, List<Pair<Entity, Integer>>> stats = new HashMap<Entity, List<Pair<Entity, Integer>>>();

        List<Entity> entities = getEntityList();
        for (int i=0; i<entities.size(); i++) {
            for (int j=i+1; j<entities.size(); j++) {
                Entity e1 = entities.get(i);
                Entity e2 = entities.get(j);

                if (!stats.containsKey(e1)) {
                    stats.put(e1, new ArrayList<Pair<Entity, Integer>>());
                }

                if (!stats.containsKey(e2)) {
                    stats.put(e2, new ArrayList<Pair<Entity, Integer>>());
                }

                int paragraphCoocurrences = e1.computeCoocurrenceParagraph(e2);
                if (paragraphCoocurrences == 0) {
                    continue;
                }

                stats.get(e1).add(new ImmutablePair<Entity, Integer>(e2, paragraphCoocurrences));
                stats.get(e2).add(new ImmutablePair<Entity, Integer>(e1, paragraphCoocurrences));


            }
        }
        return stats;
    }

}
