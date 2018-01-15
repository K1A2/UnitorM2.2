package unitor.uni.k1a2.unitor2.File;

/**
 * Created by jckim on 2017-12-03.
 */

public interface FileKey {

    int KEY_FILE_INT = 0;
    int KEY_DIRECTORY_INT = 1;

    String KEY_INFO_TITLE = "title=";
    String KEY_INFO_PRODUCER = "producerName=";
    String KEY_INFO_CHAIN = "chain=";
    String KEY_INFO_CONTENT = "title=%s\nproducerName=%s\nbuttonX=8\nbuttonY=8\nchain=%s\nsquareButton=true\nlandscape=true";

    String KEY_DELETE_UNIPACK = "deleteUnipack";
    String KEY_COPY_SOUND = "copySound";
    String KEY_COPY_LED = "copyLED";
}
