package com.crewmeister.cmcodingchallenge.externalapi.data;


import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@Data
@XmlRootElement(name = "GenericData", namespace = "http://www.sdmx.org/resources/sdmxml/schemas/v2_1/message")
@XmlAccessorType(XmlAccessType.FIELD)
public class GenericData {

    @XmlElement(name = "DataSet",namespace = "http://www.sdmx.org/resources/sdmxml/schemas/v2_1/message")
    private DataSet dataSet;
}
