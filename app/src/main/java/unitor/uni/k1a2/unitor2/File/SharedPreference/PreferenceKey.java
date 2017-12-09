package unitor.uni.k1a2.unitor2.File.SharedPreference;

/**
 * Created by jckim on 2017-12-04.
 */

public interface PreferenceKey {
    String KEY_REPOSITORY_INFO = "Info";
    String KEY_REPOSITORY_KILL = "Kill";

    String KEY_INFO_TITLE = "Title";
    String KEY_INFO_PRODUCER = "Producer";
    String KEY_INFO_CHAIN = "Chain";
    String KEY_INFO_PATH = "Path";

    String KEY_KILL_DIED = "Killed";
    boolean KEY_KILL_SELF = false;
    boolean KEY_KILL_FORCE = true;
}
