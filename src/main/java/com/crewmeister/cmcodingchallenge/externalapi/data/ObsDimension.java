package com.crewmeister.cmcodingchallenge.externalapi.data;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class ObsDimension {

    @XmlAttribute(name = "value")
    private String value;
}
