package unitor.uni.k1a2.unitor2.File.AsyncTask;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

import unitor.uni.k1a2.unitor2.File.FileKey;
import unitor.uni.k1a2.unitor2.File.SharedPreference.PreferenceKey;
import unitor.uni.k1a2.unitor2.File.SharedPreference.SharedPreferenceIO;
import unitor.uni.k1a2.unitor2.R;
import unitor.uni.k1a2.unitor2.activitys.KeySoundFragment;

/**
 * Created by jckim on 2018-01-14.
 */

public class CopyFile extends AsyncTask<Object, Object, String> {

    private Context context;
    private KeySoundFragment keySoundFragment;
    private ProgressDialog progressDialog;

    @Override
    protected void onPreExecute() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
    }

    @Override
    protected String doInBackground(Object... objects) {//key, files, context, fragment
        String key = (String)objects[0];
        ArrayList<String[]> files = (ArrayList<String[]>) objects[1];
        context = (Context) objects[2];
        String path_unipack;

        if (key == FileKey.KEY_COPY_SOUND) {
            progressDialog.setTitle(context.getString(R.string.async_copy_sound_title));
            keySoundFragment = (KeySoundFragment) objects[3];
        }
        progressDialog.show();

        SharedPreferenceIO shIO = new SharedPreferenceIO(context, PreferenceKey.KEY_REPOSITORY_INFO);
        path_unipack = shIO.getString(PreferenceKey.KEY_INFO_PATH, "");
        progressDialog.setMax(files.size());

        for (int i = 0;i < files.size();i++) {//name, path
            String title = files.get(i)[0];
            String path = files.get(i)[1];
            publishProgress(i, path);

            copyFile(new File(path), path_unipack, title);
        }

        return key;
    }

    @Override
    protected void onProgressUpdate(Object... values) {
        progressDialog.setProgress((int)values[0]);
        progressDialog.setMessage((String)values[1]);
    }

    @Override
    protected void onPostExecute(String s) {
        if (s == FileKey.KEY_COPY_SOUND) {
            if (keySoundFragment != null) {
                keySoundFragment.addSound();
            }
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage(context.getString(R.string.async_copy_sound_finish));
            progressDialog.show();
        }
    }

    private boolean copyFile(File file , String save_file, String name){
        boolean result;
        new File(save_file).mkdirs();
        if(file!=null&&file.exists()){
            try {
                FileInputStream fis = new FileInputStream(file);
                FileOutputStream newfos = new FileOutputStream(save_file + name);
                int readcount=0;
                byte[] buffer = new byte[1024];
                while((readcount = fis.read(buffer,0,1024))!= -1){
                    newfos.write(buffer,0,readcount);
                }
                newfos.close();
                fis.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            result = true;
        }else{
            result = false;
        }
        return result;
    }
}
