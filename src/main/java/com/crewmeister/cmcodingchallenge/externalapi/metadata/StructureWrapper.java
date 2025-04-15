package com.crewmeister.cmcodingchallenge.externalapi.metadata;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@Data
@XmlRootElement(name = "Structure", namespace = "http://www.sdmx.org/resources/sdmxml/schemas/v2_1/message")
@XmlAccessorType(XmlAccessType.FIELD)
public class StructureWrapper {

    @XmlElement(name = "Structures",namespace = "http://www.sdmx.org/resources/sdmxml/schemas/v2_1/message")
    private Structures structures;
}
