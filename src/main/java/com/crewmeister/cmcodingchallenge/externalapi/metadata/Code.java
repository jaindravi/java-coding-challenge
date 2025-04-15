package com.crewmeister.cmcodingchallenge.externalapi.metadata;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.util.List;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class Code {

    @XmlAttribute(name = "id")
    private String id;

    @XmlElement(name = "Name", namespace = "http://www.sdmx.org/resources/sdmxml/schemas/v2_1/common")
    private List<Name> names;

}
