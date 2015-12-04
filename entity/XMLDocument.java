package com.epam.tasks.xmlStackParser.entity;


/**
 * Created by Belarus on 25.11.2015.
 */
public class XMLDocument {
    private Node rootNode;
    private String fileName;
    public XMLDocument(String fileName, Node rootNode) {
        this.fileName = fileName;
        this.rootNode = rootNode;
    }

    @Override
    public boolean equals(Object obj){
        if(obj.getClass()!= this.getClass()) {
            return false;
        }
        XMLDocument xmlDoc = (XMLDocument)obj;
        if (!fileName.equals(xmlDoc.fileName)){
            return false;
        }
        if (!rootNode.equals(xmlDoc.rootNode)){
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "XMLDocument loaded from file - "+this.fileName+"\n"+this.rootNode.toString();
    }
}