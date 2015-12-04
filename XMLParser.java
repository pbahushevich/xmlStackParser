package com.epam.tasks.xmlStackParser;

import com.epam.tasks.xmlStackParser.Exception.XMLParseException;
import com.epam.tasks.xmlStackParser.entity.Attribute;
import com.epam.tasks.xmlStackParser.entity.Node;
import com.epam.tasks.xmlStackParser.entity.XMLDocument;
import com.epam.tasks.xmlStackParser.entity.Pair;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Belarus on 26.11.2015.
 */
public class XMLParser {
    private Stack<Pair<Node,Integer>> nodeStack = new Stack<>();
    private Stack<Pair<Node,Integer>> elementStack = new Stack<>();
    private int currentLevel = 0;                                                                                       //the depth of the current in the reader
    private static final Pattern attributePattern = Pattern.compile("([a-zA-Z]{1}[a-z|A-Z|\\d|\\-]+)=\"([^\"]+)");      // here we search for the pattern like blablabla="blablabla
    private static final Pattern nodeCatchingPattern = Pattern.compile("(<(?<tagName>[a-zA-Z][\\w\\-]*)(?<emptyTag>/?)(?<attributes>[^>]*)>)?" +
            "(?<tagContent>[^<]+)?(</(?<closingTag>[^>]+)>)?");

    public static void main(String[] args) {
        try {
            XMLDocument doc = new XMLParser().parseFile("d:\\Java development\\EmapTestProjects\\student.xml");
            System.out.println(doc);
        }catch (Exception e){
            // do noth
        }
    }
    public XMLDocument parseFile(String fileName) throws XMLParseException {
        XMLDocument result = null;
        Integer currentLevel = 0;
        try(XMLDocumentReader reader = new XMLDocumentReader(new FileReader(fileName))){
            while(reader.ready()){
                String sb = reader.readXMlPhrase();
                Matcher m = nodeCatchingPattern.matcher(sb.trim());
                if(m.find()) {
                    parseXMLPhrase(m);
                }
            }
            result = getXMLDocumentFromStack(fileName);
        }catch (IOException ie){
            throw new XMLParseException(ie.getMessage());
        }
        return result;
    }

    private void parseXMLPhrase(Matcher m) throws XMLParseException {

        if (m.group("tagName")!=null){
            Node newNode = new Node(m.group("tagName"));
            parseAttributes(newNode, m.group("attributes"));
            nodeStack.push(new Pair<>(newNode, currentLevel++));
        }

        if(m.group("tagContent")!=null && !m.group("tagContent").equals("")){

            if(nodeStack.size()==0){
                throw new XMLParseException("Found text content in an unexpected place!No current opened nodes....");
            }
            Pair<Node,Integer> currentPair = nodeStack.pop();
            currentPair.getKey().setValue(m.group("tagContent"));
            nodeStack.push(currentPair);
        }


        if((m.group("closingTag")!=null) || (m.group("emptyTag")!=null && !m.group("emptyTag").equals("")) ){
            if(nodeStack.size()==0){
                throw new XMLParseException("Found closing tag in an unexpected place!No current opened nodes....");
            }

            Node currentNode  = nodeStack.pop().getKey();

            while (elementStack.size()>0 && elementStack.get(elementStack.size() - 1).getValue()>=currentLevel){
                currentNode.addElement(elementStack.pop().getKey());
            }
            elementStack.push(new Pair<>(currentNode,currentLevel-1));
            currentLevel--;
        }
    }

    private void parseAttributes(Node node, String nodeAttributes) {

        if("".equals(nodeAttributes)){
            return;
        }

        Matcher m = attributePattern.matcher(nodeAttributes);
        while(m.find()){
            node.addAttribute(new Attribute(m.group(1), m.group(2)));
        }

    }

    private XMLDocument getXMLDocumentFromStack(String fileName) throws XMLParseException{
        if (elementStack.size()==0){
            throw new XMLParseException("Error processing root node for XMLDocument after successful parsing the file. No root element found in stack.");
        }else if(elementStack.size()>1){
            throw new XMLParseException("Error processing root node for XMLDocument after successful parsing the file."+elementStack.size()+" root elements found in stack. Should be one.");
        }
        return new XMLDocument(fileName,elementStack.pop().getKey());
    }

    private class XMLDocumentReader extends BufferedReader {
        private StringBuffer sb = new StringBuffer();

        public XMLDocumentReader(InputStreamReader is){
            super(is);
        }

        public String readXMlPhrase() throws XMLParseException{
            int currentStatus =  0;
            String result = "";
            int i = 0;
            try{
                while(ready()) {
                    if(currentStatus!=0 || sb.length()==0) {
                        sb.append("\n").append(readLine());
                    }
                    while (i<sb.length() && currentStatus <5){
                        char c = sb.charAt(i);
                        switch (c) {
                            case '<':
                                if (currentStatus == 0) {                                                                   //we are just at the beginning - going into the node
                                    currentStatus = (sb.charAt(i + 1) == '/') ? (currentStatus = 3) : (currentStatus =1);   // we are either in the opening node or in the closing node(if it starts with "/"
                                }else
                                if ((currentStatus == 2) && (sb.charAt(i + 1) == '/')) {                                    //we are in the content field - going into the closing node
                                    currentStatus = 3;
                                }
                                else {                                                                                      //we are in the content field - but found opening tag - end of phrase (+ 1 step back)
                                    i--;
                                    currentStatus = 5;
                                }
                                break;
                            case '>':
                                if ((currentStatus == 3) || (currentStatus == 1 && sb.charAt(i - 1) == '/')) {   //we are in the closing node or in the empty node? came to the end
                                    currentStatus = 5;
                                } else currentStatus = 2;                                                                 //we are at the end of the opening tag going into the content
                        }
                        i++;
                    }
                    if (currentStatus == 5) {
                        result = sb.substring(0, i);
                        sb.delete(0, i);
                        break;
                    }
                }

            }catch (IOException ie){
                System.out.println(ie.getMessage());
                ie.printStackTrace();
            }

            return result;
        }

    }
}
