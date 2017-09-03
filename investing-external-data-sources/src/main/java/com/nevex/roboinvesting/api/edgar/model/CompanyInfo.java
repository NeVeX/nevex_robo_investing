package com.nevex.roboinvesting.api.edgar.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by Mark Cunningham on 9/2/2017.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class CompanyInfo {

    @XmlElement(name="cik", namespace = "http://www.w3.org/2005/Atom")
    private String cik;

    public CompanyInfo() { }

    public String getCik() {
        return cik;
    }

    public void setCik(String cik) {
        this.cik = cik;
    }

}
