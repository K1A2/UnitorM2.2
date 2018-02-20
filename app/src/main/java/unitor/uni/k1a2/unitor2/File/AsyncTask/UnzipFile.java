package unitor.uni.k1a2.unitor2.File.AsyncTask;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import unitor.uni.k1a2.unitor2.File.FileIO;
import unitor.uni.k1a2.unitor2.R;
import unitor.uni.k1a2.unitor2.views.adapters.recyclerview.UnipackListAdapter;
import unitor.uni.k1a2.unitor2.views.adapters.recyclerview.UnipackListItem;

/**
 * Created by jckim on 2017-12-06.
 */

public class UnzipFile extends AsyncTask<Object, String, Boolean> {

    private Context context;
    private ProgressDialog progressDialog;
    private String name, path, target, finish;
    private UnipackListAdapter adapter;

    public UnzipFile(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
    }

    @Override
    protected Boolean doInBackground(Object... objects) {
        name = (String) objects[0];
        path = (String) objects[1];
        target = (String) objects[2];
        adapter = (UnipackListAdapter) objects[4];
        publishProgress("start", name);

        try {
            FileInputStream fileInputStream = new FileInputStream(path);
            ZipInputStream zipInputStream = new ZipInputStream(fileInputStream);
            ZipEntry zipEntry = null;

            File targetFile = null;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                String filenameTounzip = zipEntry.getName();
                targetFile = new File(target, filenameTounzip);

                if (zipEntry.isDirectory()) {
                    File pathF = new File(targetFile.getAbsolutePath());
                    pathF.mkdirs();
                } else {
                    File pathF = new File(targetFile.getParent());
                    pathF.mkdirs();
                    Unzip(zipInputStream, targetFile);
                }
            }

            fileInputStream.close();
            zipInputStream.close();
            if (targetFile != null) finish = targetFile.getAbsolutePath();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            publishProgress(e.getMessage());
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            publishProgress(e.getMessage());
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            publishProgress(e.getMessage());
            return false;
        }
    }

    @Override
    protected void onProgressUpdate(String... values) {
        if (values[0].equals("start")) {
            progressDialog.setTitle(context.getString(R.string.async_unzip_title));
            progressDialog.setMessage(String.format(context.getString(R.string.async_unzip_message), values[1]));
            progressDialog.show();
        } else {
            new FileIO(context).showErr(values[0]);
        }
    }

    @Override
    protected void onPostExecute(Boolean b) {
        progressDialog.dismiss();
        progressDialog = new ProgressDialog(context);
        if (b == true) {
            progressDialog.setTitle(String.format(context.getString(R.string.dialog_unzip_sucT), name));
            progressDialog.setMessage(String.format(context.getString(R.string.dialog_unzip_sucM), name));
            String[] s = new FileIO(context).getUnipackInfo(new File(finish + "/info"), finish + "/");
            UnipackListItem item = new UnipackListItem();
            item.setTitle(s[0]);
            item.setProducer(s[1]);
            item.setChain(s[2]);
            item.setPath(s[3]);
            adapter.addItem(item);
        } else {
            progressDialog.setTitle(String.format(context.getString(R.string.dialog_unzip_failT), name));
            progressDialog.setMessage(String.format(context.getString(R.string.dialog_unzip_failM), name));
        }
        progressDialog.setButton(context.getString(R.string.alert_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                progressDialog.dismiss();
            }
        });
        progressDialog.show();
    }

    private File Unzip(ZipInputStream zipInputStream, File targetFile) throws IOException {
        FileOutputStream fileOutputStream = null;

        final int BUFFER_SIZE = 1024 * 2;

        try {
            fileOutputStream = new FileOutputStream(targetFile);

            byte[] buffer = new byte[BUFFER_SIZE];
            int len = 0;
            try {
                while ((len = zipInputStream.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, len);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                fileOutputStream.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return targetFile;
    }
}
