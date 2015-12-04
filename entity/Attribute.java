package com.epam.tasks.xmlStackParser.entity;

/**
 * Created by Belarus on 25.11.2015.
 */
public class Attribute {
    private final String name;
    private final String value;
    public Attribute(String name, String value) {
        this.name  = name;
        this.value = value;

    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        return 17*name.hashCode()+29*value.hashCode();
    }

    @Override
    public boolean equals(Object obj) {

        if(obj.getClass()!= Attribute.class) {
            return false;
        }
        Attribute at = (Attribute)obj;
        if (name.equals(at.getName()) && value.equals(at.getValue())){
            return true;
        }else{
            return false;
        }
    }
}
