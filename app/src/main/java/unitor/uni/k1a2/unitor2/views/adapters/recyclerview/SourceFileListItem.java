package unitor.uni.k1a2.unitor2.views.adapters.recyclerview;

/**
 * Created by jckim on 2018-02-17.
 */

public class SourceFileListItem {

    private String titleStr;
    private String pathStr;

    public void setTitle(String title) {
        titleStr = title;
    }

    public void setPath(String path) {
        pathStr = path;
    }

    public String getTitle() {
        return this.titleStr;
    }

    public String getPath() {
        return this.pathStr;
    }
}
