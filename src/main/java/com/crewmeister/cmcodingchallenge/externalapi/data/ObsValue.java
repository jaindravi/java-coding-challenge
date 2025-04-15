package com.crewmeister.cmcodingchallenge.externalapi.data;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class ObsValue {

    @XmlAttribute(name = "value")
    private String value;
}
