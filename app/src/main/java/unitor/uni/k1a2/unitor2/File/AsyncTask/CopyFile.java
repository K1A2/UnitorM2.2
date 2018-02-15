package unitor.uni.k1a2.unitor2.File.AsyncTask;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

import unitor.uni.k1a2.unitor2.File.FileIO;
import unitor.uni.k1a2.unitor2.File.FileKey;
import unitor.uni.k1a2.unitor2.File.SharedPreference.PreferenceKey;
import unitor.uni.k1a2.unitor2.File.SharedPreference.SharedPreferenceIO;
import unitor.uni.k1a2.unitor2.R;
import unitor.uni.k1a2.unitor2.activitys.KeySoundFragment;

/**
 * Created by jckim on 2018-01-14.
 */

public class CopyFile extends AsyncTask<Object, Object, Object[]> {

    private Context context;
    private KeySoundFragment keySoundFragment;
    private ProgressDialog progressDialog;
    private String errMsg;
    private boolean cancelable = false;

    public CopyFile(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
    protected Object[] doInBackground(Object... objects) {//key, files, context, fragment
        String key = (String)objects[0];
        ArrayList<String[]> files = (ArrayList<String[]>) objects[1];
        context = (Context) objects[2];
        String path_target;

        SharedPreferenceIO shIO = new SharedPreferenceIO(context, PreferenceKey.KEY_REPOSITORY_INFO);
        path_target = shIO.getString(PreferenceKey.KEY_INFO_PATH, "");
        progressDialog.setMax(files.size());

        if (key == FileKey.KEY_COPY_SOUND) {
            publishProgress(context.getString(R.string.async_copy_sound_title));
            path_target = path_target + "sounds/";
        }

        for (int i = 0;i < files.size();i++) {//name, path
            if (cancelable ==true) return new Object[] {key, objects[3], errMsg};
            String title = files.get(i)[0];
            String path = files.get(i)[1];
            publishProgress(i, path);

            copyFile(new File(path), path_target, title);
        }

        return new Object[] {key, objects[3]};
    }

    @Override
    protected void onProgressUpdate(Object... values) {
        if (values.length == 1) {
            progressDialog.setTitle((String) values[0]);
        } else {
            progressDialog.setProgress((int)values[0]);
            progressDialog.setMessage((String)values[1]);
        }
    }

    @Override
    protected void onPostExecute(Object[] s) {
        progressDialog.dismiss();
        if (s.length == 3) {
            new FileIO(context).showErr((String) s[2]);
        } else {
            if (s[0] == FileKey.KEY_COPY_SOUND) {
                keySoundFragment = (KeySoundFragment) s[1];
                if (keySoundFragment != null) {
                    keySoundFragment.addSound();
                }
                progressDialog = new ProgressDialog(context);
                progressDialog.setCanceledOnTouchOutside(true);
                progressDialog.setCancelable(true);
                progressDialog.setMessage(context.getString(R.string.async_copy_sound_finish));
                progressDialog.show();
            } else if (s[0] == FileKey.KEY_COPY_LED) {

            }
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
                cancelable = true;
                errMsg = e.getMessage();
            }
            result = true;
        }else{
            result = false;
        }
        return result;
    }
}
