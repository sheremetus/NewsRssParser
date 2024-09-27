
package by.sheremetus.parser.rssparser.entity;

import java.util.Date;
import java.util.List;

public class SearchResult {
    private String section;
    private List<byte[]> image;
    private List<String> linkHref;
    private Date date;

    public SearchResult(String section, List<byte[]> image, List<String> linkHref) {
        this.section = section;
        this.image = image;
        this.linkHref = linkHref;
    }

    public void setSection(String section) {
        this.section = section;
    }


    public List<byte[]> getImage() {
        return image;
    }

    public void setImage(List<byte[]> image) {
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

    public List<String> getLinkHref() {
        return linkHref;
    }

    public void setLinkHref(List<String> linkHref) {
        this.linkHref = linkHref;
    }
}