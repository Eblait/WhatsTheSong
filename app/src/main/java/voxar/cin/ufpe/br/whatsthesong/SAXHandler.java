package voxar.cin.ufpe.br.whatsthesong;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Created by Dicksson on 8/6/2014.
 */
public class SAXHandler extends DefaultHandler {

    Song trackList;
    String content;

    @Override
    //Triggered when the start of tag is found.
    public void startElement(String uri, String localName,
                             String qName, Attributes attributes)
            throws SAXException {

        if(qName.equals("data")){
            //Create a new Song object when the start tag is found
            trackList = new Song();
        }

    }

    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {

        if (qName == "duration") {
            trackList.setDuration(Integer.parseInt(content));
        } else if (qName.contains("link")) {
            trackList.getUrls().add(content);

            int pos = content.length() - 5;
            trackList.getIndexes().add(Integer.parseInt(content.substring(pos, pos + 1)));
        } else if (qName.contains("option")) {
            String str[] = content.split(":");
            str[1] = str[1].trim();

            trackList.getOptions().add(str);
        } else if (qName == "answer") {
            trackList.setAnswer(Integer.parseInt(content));
        }
    }

    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        content = String.copyValueOf(ch, start, length).trim();
    }

}