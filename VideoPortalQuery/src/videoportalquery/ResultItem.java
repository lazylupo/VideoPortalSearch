/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package videoportalquery;

/**
 *
 * @author zerlot
 */
public class ResultItem {
    
    private String title = "";
    private String imageURL = "";
    private String thumbURL = "";
    private String recordNumber = "";
    private String source = "";
    
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getRecordNumber() {
        return recordNumber;
    }

    public void setRecordNumber(String recordNumber) {
        this.recordNumber = recordNumber;
    }

    public String getThumbURL() {
        return thumbURL;
    }

    public void setThumbURL(String thumbURL) {
        this.thumbURL = thumbURL;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        if(source != null){
            this.source = source;
        }
    }

    public boolean hasSource(){
        return !this.source.equals("");
    }
}
