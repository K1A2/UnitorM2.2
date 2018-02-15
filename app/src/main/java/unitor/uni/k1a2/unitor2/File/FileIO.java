package unitor.uni.k1a2.unitor2.File;

import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;

import unitor.uni.k1a2.unitor2.R;

/**
 * Created by jckim on 2017-12-03.
 */

public class FileIO extends ContextWrapper {

    private Context context;
    private String defaultPath;

    public FileIO(Context base) {
        super(base);
        this.context = base;
        this.defaultPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
    }

    //저장소 경로 리턴
    public String getDefaultPath() {
        return defaultPath;
    }

    //unipackproject폴더에 유니팩 리턴
    public ArrayList<String[]> getUnipacks() {
        File unipackprojectf = new File(defaultPath + "unipackProject/");

        try {
            isExists(unipackprojectf, FileKey.KEY_DIRECTORY_INT);
            isExists(new File(defaultPath + "unipackProject/.nomedia"), FileKey.KEY_FILE_INT);
        } catch (Exception e) {
            e.printStackTrace();
            showErr(e.getMessage());
            return null;
        }

        File[] unipackList = unipackprojectf.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.isDirectory();
            }
        });

        ArrayList<String[]> unipackInfo = new ArrayList<String[]>();

        for (File arrayUnipack:unipackList) {
            String path = arrayUnipack.getAbsolutePath();
            unipackInfo.add(getUnipackInfo(new File(path + "/info"), path + "/"));
        }

        return unipackInfo;
    }

    //유니팩 인포 가져옴
    public String[] getUnipackInfo(File unipack, String path) {
        if (unipack.exists()) {
            try {
                ArrayList<String> arrayInfo = getTextFile(unipack);

                String info[] = new String[4];
                for (String in:arrayInfo) {
                    if (in.startsWith(FileKey.KEY_INFO_TITLE)) {
                        info[0] = in.replace(FileKey.KEY_INFO_TITLE, "");
                    } else if (in.startsWith(FileKey.KEY_INFO_PRODUCER)) {
                        info[1] = in.replace(FileKey.KEY_INFO_PRODUCER, "");
                    } else if (in.startsWith(FileKey.KEY_INFO_CHAIN)) {
                        info[2] = in.replace(FileKey.KEY_INFO_CHAIN, "");
                    }
                }
                info[3] = path;

                return info;
            }catch (Exception e) {
                e.printStackTrace();
                showErr(e.getMessage());
                return null;
            }
        } else {
            return null;
        }
    }

    //파일내용 가져옴
    public ArrayList<String> getTextFile(File file) throws Exception {
        if (file.exists()) {
            ArrayList<String> arrayFile = new ArrayList<String>();
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                if (!line.isEmpty()) {
                    arrayFile.add(line);
                }
            }
            bufferedReader.close();

            return arrayFile;
        } else {
            return null;
        }
    }

    //info내용 가져옴
    public ArrayList<String> getInfo(String path) throws Exception {
        path += "info";
        return getTextFile(new File(path));
    }

    //키사운드 워크파일
    public ArrayList<String> getKeySoundWork() throws Exception {
        return getTextFile(new File(getDefaultPath()  + "unipackProject/work/keySound.txt"));
    }

    //키사운드 가져옴
    public ArrayList<String> getKeySound(String path) throws Exception {
        path += "keySound";
        return getTextFile(new File(path));
    }

    //사운드 파일 가져옴
    public ArrayList<String[]> getSoundFile(String path) throws Exception {
        File path_sound = new File(path);
        ArrayList<String[]> sounds = new ArrayList<String[]>();

        isExists(path_sound, FileKey.KEY_DIRECTORY_INT);
        File[] soundlist = path_sound.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.isFile()&&(file.getName().endsWith(".wav")||file.getName().endsWith(".mp3"));
            }
        });

        for (File f:soundlist) {
            sounds.add(new String[] {f.getName(), f.getAbsolutePath()});
        }

        return sounds;
    }

    //새 유팩생성
    public void mkNewUnipack(String Title, String Producer, String Chain, String path) throws Exception {
        File file = new File(path);
        isExists(file, FileKey.KEY_DIRECTORY_INT);

        path += "info";
        mkInfo(Title, Producer, Chain, path);
    }

    //인포 생성, 저장
    public void mkInfo(String Title, String Producer, String Chain, String path) throws Exception {
        File file = new File(path);
        isExists(file, FileKey.KEY_FILE_INT);

        PrintWriter printWriter = new PrintWriter(file);
        printWriter.printf(FileKey.KEY_INFO_CONTENT, Title, Producer, Chain);
        printWriter.close();
    }

    //키사운드 워크폴더에 저장
    public void mkKeySoundWork(ArrayList<String> content) throws Exception {
        String path = getDefaultPath() + "unipackProject/work/";
        File file = new File(path);
        isExists(file, FileKey.KEY_DIRECTORY_INT);
        path += "keySound.txt";
        file = new File(path);
        isExists(file, FileKey.KEY_FILE_INT);

        PrintWriter printWriter = new PrintWriter(file);
        for (String s:content) {
            printWriter.println(s);
        }
        printWriter.close();
    }

    //키사운드 생성 저장
    public void mkKeySound(String path) throws Exception {
        ArrayList<String> content = getKeySoundWork();
        File file = new File(path);
        isExists(file, FileKey.KEY_FILE_INT);

        PrintWriter printWriter = new PrintWriter(file);
        for (String s:content) {
            printWriter.println(s);
        }
        printWriter.close();
    }

    //파일 유무, 없으면 생성
    public void isExists(File file, int i) throws Exception {
        if (!file.exists()) {
            if (i == FileKey.KEY_FILE_INT) {
                file.createNewFile();
            } else if (i == FileKey.KEY_DIRECTORY_INT) {
                file.mkdirs();
            }
        }
    }

    //에러출력
    public void showErr(String e) {
        AlertDialog.Builder alertErr = new AlertDialog.Builder(context);
        alertErr.setTitle(getString(R.string.alert_err));
        alertErr.setMessage(e);
        alertErr.setPositiveButton(getString(R.string.alert_ok), null);
        alertErr.show();
    }
}
