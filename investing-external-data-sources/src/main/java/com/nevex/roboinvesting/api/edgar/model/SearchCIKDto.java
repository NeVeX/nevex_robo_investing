package com.nevex.roboinvesting.api.edgar.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Mark Cunningham on 9/2/2017.
 */
@XmlRootElement(name="feed", namespace = SearchCIKDto.NAMESPACE)
@XmlAccessorType(XmlAccessType.FIELD)
public class SearchCIKDto {

    static final String NAMESPACE = "http://www.w3.org/2005/Atom";

    @XmlElement(name="company-info", required = true, nillable = true, namespace = NAMESPACE)
    private CompanyInfo companyInfo;

    public SearchCIKDto() { }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class CompanyInfo {

        @XmlElement(name="cik", namespace = NAMESPACE)
        private String cik;

        public CompanyInfo() { }

        public String getCik() {
            return cik;
        }

        public void setCik(String cik) {
            this.cik = cik;
        }
    }

    public CompanyInfo getCompanyInfo() {
        return companyInfo;
    }

    public void setCompanyInfo(CompanyInfo companyInfo) {
        this.companyInfo = companyInfo;
    }
}
