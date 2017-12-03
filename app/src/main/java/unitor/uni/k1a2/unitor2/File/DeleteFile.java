package unitor.uni.k1a2.unitor2.File;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;

import unitor.uni.k1a2.unitor2.adapters.list.UnipackListAdapter;

/**
 * Created by jckim on 2017-12-03.
 */

public class DeleteFile extends AsyncTask<Object, String, String> {

    private Context context;
    private ProgressDialog progressDialog;
    private UnipackListAdapter unipackListAdapter;
    private ListView listView;

    public DeleteFile(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
    protected String doInBackground(Object... objects) {
        String key = (String) objects[0];

        if (key.equals(FileKey.KEY_DELETE_UNIPACK)) {
            String title = (String) objects[2];
            String path = (String) objects[1];
            unipackListAdapter = (UnipackListAdapter) objects[4];
            listView = (ListView) objects[3];
            publishProgress(key, title);
            delete(path);
        }

        return key;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        if (values[0].equals(FileKey.KEY_DELETE_UNIPACK)) {
            progressDialog.setTitle(values[1]);
        }
    }

    @Override
    protected void onPostExecute(String s) {
        progressDialog.dismiss();
        if (s.equals(FileKey.KEY_DELETE_UNIPACK)) {
            unipackListAdapter.clear();
            ArrayList<String[]> arrayUnipack = new FileIO(context).getUnipacks();
            if (arrayUnipack != null) {
                for (String[] unipackInfo:arrayUnipack) {
                    if (unipackInfo != null) unipackListAdapter.addItem(unipackInfo[0], unipackInfo[1], unipackInfo[3], unipackInfo[2]);
                }
            }
            listView.setAdapter(unipackListAdapter);
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
