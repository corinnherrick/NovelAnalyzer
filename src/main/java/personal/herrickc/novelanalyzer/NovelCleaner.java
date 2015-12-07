package personal.herrickc.novelanalyzer;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * This class takes in an xml file with page and paragraph tags and outputs a cleaner version.
 */
public class NovelCleaner {
    DocumentBuilder documentBuilder;

    public NovelCleaner() throws ParserConfigurationException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        this.documentBuilder = documentBuilderFactory.newDocumentBuilder();
    }
    public void clean(String inputFilename, String outputFilename) throws IOException, SAXException, TransformerException {
        Document doc = documentBuilder.parse(new File(inputFilename));
        NodeList paragraphs = doc.getElementsByTagName("paragraph");

        for (int i=paragraphs.getLength()-1; i >= 0; i--) {
            Node paragraph = paragraphs.item(i);
            if (!isValidParagraph(paragraph.getTextContent())) {
                paragraph.getParentNode().removeChild(paragraph);
            }

        }

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();

        DOMSource source = new DOMSource(doc);
        FileWriter out = new FileWriter(outputFilename);
        StreamResult result = new StreamResult(out);
        transformer.transform(source, result);
        out.close();
    }

    private boolean isValidParagraph(String text) {
        String trimmed = text.trim();
        return StringUtils.countMatches(text.trim(), "\n")>3;

    }

}
