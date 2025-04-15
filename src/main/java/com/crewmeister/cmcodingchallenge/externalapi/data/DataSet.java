package com.crewmeister.cmcodingchallenge.externalapi.data;


import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class DataSet {

    @XmlElement(name = "Series",namespace = "http://www.sdmx.org/resources/sdmxml/schemas/v2_1/data/generic")
    private Series series;
}
