
package by.sheremetus.parser.rssparser.entity;

import java.util.Date;

public class SearchResult {
    private String section;
    private byte[] image;
    private String linkHref;
    private Date date;

    public SearchResult(String section, byte[] image, String linkHref) {
        this.section = section;
        this.image = image;
        this.linkHref =linkHref;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getSection() {
        return section;
    }

    public byte[] getImage() {
        return image;
    }

    public String getLinkHref() {
        return linkHref;
    }

    public void setLinkHref(String linkHref) {
        this.linkHref = linkHref;
    }
}