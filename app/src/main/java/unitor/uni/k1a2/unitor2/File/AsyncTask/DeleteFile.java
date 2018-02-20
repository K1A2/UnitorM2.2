package unitor.uni.k1a2.unitor2.File.AsyncTask;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import java.io.File;

import unitor.uni.k1a2.unitor2.File.FileKey;
import unitor.uni.k1a2.unitor2.views.adapters.recyclerview.UnipackListAdapter;

/**
 * Created by jckim on 2017-12-03.
 */

public class DeleteFile extends AsyncTask<Object, String, Object[]> {

    private Context context;
    private ProgressDialog progressDialog;
    private UnipackListAdapter unipackListAdapter;
    private int position;

    public DeleteFile(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
    }

    @Override
    protected Object[] doInBackground(Object... objects) {
        String key = (String) objects[0];

        if (key.equals(FileKey.KEY_DELETE_UNIPACK)) {
            String title = (String) objects[2];
            String path = (String) objects[1];
            unipackListAdapter = (UnipackListAdapter) objects[4];
            position = (int) objects[5];
            publishProgress(key, title);
            delete(path);
        }

        return new Object[] {key, position};
    }

    @Override
    protected void onProgressUpdate(String... values) {
        if (values[0].equals(FileKey.KEY_DELETE_UNIPACK)) {
            progressDialog.setTitle(values[1]);
            progressDialog.show();
        }
    }

    @Override
    protected void onPostExecute(Object[] objects) {
        progressDialog.dismiss();
        if (((String) objects[0]).equals(FileKey.KEY_DELETE_UNIPACK)) {
            unipackListAdapter.removeItem((int) objects[1]);
        }
    }

    private void delete(String path) {
        File d = new File(path);
        if (d.exists()) {
            File[] childFileList = d.listFiles();
            for (File childFile : childFileList) {
                if (childFile.isDirectory()) {
                    delete(childFile.getPath());
                } else {
                    childFile.delete();
                }
            }
            d.delete();
        }
    }
}
