package unitor.uni.k1a2.unitor2.File;

import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
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
                arrayFile.add(line);
            }
            bufferedReader.close();

            return arrayFile;
        } else {
            return null;
        }
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
