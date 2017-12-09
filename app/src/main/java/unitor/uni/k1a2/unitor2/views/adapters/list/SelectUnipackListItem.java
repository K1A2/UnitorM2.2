package unitor.uni.k1a2.unitor2.views.adapters.list;

import android.graphics.drawable.Drawable;

/**
 * Created by jckim on 2017-12-05.
 */

public class SelectUnipackListItem {

    private Drawable iconDrw;
    private String titleStr;
    private String pathStr;

    public void setIconDrw(Drawable icon) {
        iconDrw = icon;
    }

    public void setTitle(String title) {
        titleStr = title;
    }

    public void setPath(String path) {
        pathStr = path;
    }

    public Drawable getIcon() {
        return this.iconDrw;
    }

    public String getTitle() {
        return this.titleStr;
    }

    public String getPath() {
        return this.pathStr;
    }
}
