package com.epam.tasks.xmlStackParser.entity;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Belarus on 25.11.2015.
 */
public class Node {

    private List<Node> elements;
    private List<Attribute> attributes;
    private String value  = "";
    private String name = "";

    public Node(){

    }

    public Node(String name) {
        this.name = name;
    }

    public Node(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public Node(String value, String name, List<Attribute> attributes,List<Node> elements ) {
        this.name = name;
        this.value = value;
        this.attributes = attributes;
        this.elements = elements;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void addElement(Node element){
        if(elements == null){
            elements = new LinkedList<>();
        }
        elements.add(element);
    }
    public List<Node> getElements(){
        return elements;
    }

    public void addAttribute(Attribute at){
        if(attributes == null){
            attributes = new LinkedList<>();
        }
        attributes.add(at);
    }
    public List<Attribute> getAttributes(){
        return attributes;
    }

    @Override
    public String toString(){

        return this.getStringView("");

    }

    private String getStringView(String border){//better don't look at it :((

        String result = "";

        String attributesString ="";
        if (attributes != null) {
            for (Attribute at : attributes){
                attributesString = attributesString+at.getName()+"="+at.getValue()+";";
            }
            attributesString = " ("+attributesString.substring(0,attributesString.length()-1)+")";
        }

        if(elements==null){
            result = "|"+border+name + " - " + value+attributesString;
            result = result + "\r\n"+attributesString;
        }else {
            result = result+"|" + border+"<"+ name+attributesString+"------------------> \r\n";
            for (Node n: elements){
                result = result+n.getStringView(border+"\t")+"\r\n";
            }
            result = result + "|"+border+"<------------------"+ name+"> \r\n";
        }
        return result.trim();
    }


    @Override
    public int hashCode() {
        return super.hashCode();
    }

    private int getRecursiveHashCode(){
        int currentHash = 0;
        currentHash = currentHash+23*name.hashCode()+17*value.hashCode();
        for(Attribute at: attributes){
            currentHash = currentHash+29*at.hashCode();
        }
        for(Node nd: elements){
            currentHash = currentHash+31*nd.hashCode();
        }
        return currentHash;

    }

    @Override
    public boolean equals(Object obj) {
        if(!super.equals(obj)){
            return false;
        }

        if(obj.getClass()!= this.getClass()) {
            return false;
        }

        Node nd = (Node)obj;
        if (!name.equals(nd.getName()) || !value.equals(nd.getValue())){
            return false;
        }
        if(!attributes.equals(nd.attributes)) {
            return false;
        }
        if(!elements.equals(nd.elements)) {
            return false;
        }

        return true;
    }
}
