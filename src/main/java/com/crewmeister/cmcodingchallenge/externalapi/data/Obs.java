package com.crewmeister.cmcodingchallenge.externalapi.data;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class Obs {

    @XmlElement(name = "ObsDimension",namespace = "http://www.sdmx.org/resources/sdmxml/schemas/v2_1/data/generic")
    private ObsDimension obsDimension;

    @XmlElement(name = "ObsValue",namespace = "http://www.sdmx.org/resources/sdmxml/schemas/v2_1/data/generic")
    private ObsValue obsValue;
}
