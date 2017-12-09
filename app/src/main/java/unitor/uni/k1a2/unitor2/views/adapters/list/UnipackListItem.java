package unitor.uni.k1a2.unitor2.views.adapters.list;

/**
 * Created by jckim on 2017-12-02.
 */

public class UnipackListItem {

    private String titleStr;
    private String producerStr;
    private String pathStr;
    private String chainStr;

    public void setTitle(String title) {
        titleStr = title;
    }

    public void setProducer(String producer) {
        producerStr = producer;
    }

    public void setPath(String path) {
        pathStr = path;
    }

    public void setChain(String chain) {
        chainStr = chain;
    }

    public String getTitle() {
        return this.titleStr;
    }

    public String getProducer() {
        return this.producerStr;
    }

    public String getPath() {
        return this.pathStr;
    }

    public String getChain() {
        return this.chainStr;
    }
}
