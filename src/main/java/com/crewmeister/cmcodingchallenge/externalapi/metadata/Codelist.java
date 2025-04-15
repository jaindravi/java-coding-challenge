package com.crewmeister.cmcodingchallenge.externalapi.metadata;


import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.List;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class Codelist {

    @XmlElement(name = "Code", namespace = "http://www.sdmx.org/resources/sdmxml/schemas/v2_1/structure")
    private List<Code> codes;
}
