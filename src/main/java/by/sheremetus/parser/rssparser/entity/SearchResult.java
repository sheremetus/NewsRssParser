
package by.sheremetus.parser.rssparser.entity;

import java.util.Date;

public class SearchResult {
    private String section;
    private byte[] image;
    private Date date;

    public SearchResult(String section, byte[] image) {
        this.section = section;
        this.image = image;
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
}