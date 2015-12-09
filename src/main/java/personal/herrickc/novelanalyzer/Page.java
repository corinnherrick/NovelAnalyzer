package personal.herrickc.novelanalyzer;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by herrickc on 12/7/15.
 */
public class Page {
    private final List<Paragraph> paragraphs;
    private final Novel novel;

    public Page(Node page, Novel novel) {
        NodeList paragraphs = page.getChildNodes();
        this.paragraphs = new ArrayList<Paragraph>();
        for (int i=0; i<paragraphs.getLength(); i++) {
            Node paragraph = paragraphs.item(i);

            if (paragraph.getNodeName().equals("paragraph")) { // make sure the node is a paragraph
                this.paragraphs.add(new Paragraph(paragraph, this));
            } else if (paragraph.getNodeName().equals("#text")) {
                // Ignore this case... just  whitespace between elements.
            }
            else {
                throw new RuntimeException("Page should only contain Paragraph elements. Found: " + paragraph.getLocalName());
            }
        }
        this.novel = novel;
    }

    // Returns true if the page is valid
    public boolean clean() {
        Iterator<Paragraph> paragraphIterator = paragraphs.iterator();
        while (paragraphIterator.hasNext()) {
            if (!paragraphIterator.next().clean()) {
                paragraphIterator.remove();
            }
        }
        return paragraphs.size() > 0;
    }

    public void write(FileWriter fw) throws IOException {
        fw.write("\n<page>\n");
        for (Paragraph paragraph : paragraphs) {
            paragraph.write(fw);
        }
        fw.write("\n</page>\n");
    }

    public int getId() {
        return this.novel.getPageId(this);
    }

    public int getParagraphId(Paragraph paragraph) {
        return paragraphs.indexOf(paragraph);
    }

    public List<Paragraph> getParagraphs() {
        return paragraphs;
    }



}
