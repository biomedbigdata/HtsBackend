package org.nanocan.layout

class SpotType implements Serializable {

    String name
    String type
    String color

    static constraints = {
        color unique:  true, validator:  {val, obj -> val != "#ffffff"}, nullable: false, blank: false
        type inList: ["Buffer", "Sample", "Control"]
    }

    String toString()
    {
        name + "(" + type + ")"
    }
}
