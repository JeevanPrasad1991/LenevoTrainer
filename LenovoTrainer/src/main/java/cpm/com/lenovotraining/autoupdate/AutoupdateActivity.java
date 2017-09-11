package cpm.com.lenovotraining.autoupdate;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;

import cpm.com.lenovotraining.R;
import cpm.com.lenovotraining.constants.CommonString;

public class AutoupdateActivity extends AppCompatActivity {

        String versionCode;
        int length;
         Dialog dialog;
        ProgressBar pb;
        TextView percentage, message;
        Data data;

        String path = "", p, s;

        ProgressBar progressBar;
        boolean status;

        @Override
        protected void onCreate (Bundle savedInstanceState){
        // TODO Auto-generated method stub

        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        path = intent.getStringExtra(CommonString.KEY_PATH);
       // status = intent.getBooleanExtra(CommonString.KEY_STATUS, false);

        setContentView(R.layout.login_layout);

		/*if (status)
			setContentView(R.layout.mainpage);
		else
			setContentView(R.layout.new_main);*/

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Parinaam");
        builder.setMessage("New Update Available.")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        SharedPreferences preferences = PreferenceManager
                                .getDefaultSharedPreferences(AutoupdateActivity.this);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.clear();
                        editor.commit();

                        new File(
                                "/data/data/com.cpm.gt_gsk/databases/GTGSK_DATABASE")
                                .delete();


                        new DownloadTask(AutoupdateActivity.this).execute();

                    }
                });
				/*.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {

								Intent i = new Intent(AutoupdateActivity.this,
										MainActivity.class);
								startActivity(i);

								AutoupdateActivity.this.finish();

							}
						});*/
        AlertDialog alert = builder.create();

        alert.show();

    }

        class DownloadTask extends AsyncTask<Void, Data, String> {

            private Context context;

            DownloadTask(Context context) {
                this.context = context;
            }

            @Override
            protected void onPreExecute() {
                // TODO Auto-generated method stub
                super.onPreExecute();

                dialog = new Dialog(context);
                dialog.setContentView(R.layout.custom);
                dialog.setTitle("Download");
                dialog.setCancelable(false);
                dialog.show();

                pb = (ProgressBar) dialog.findViewById(R.id.progressBar1);
                percentage = (TextView) dialog.findViewById(R.id.percentage);
                message = (TextView) dialog.findViewById(R.id.message);

            }

            @Override
            protected String doInBackground(Void... params) {
                // TODO Auto-generated method stub

                try {
                    data = new Data();
                    data.name = "Downloading Application";
                    publishProgress(data);

                    versionCode = getPackageManager().getPackageInfo(
                            getPackageName(), 0).versionName;

                    data.name = "Upgraditing Version : " + versionCode;
                    publishProgress(data);

                    // download application
                    URL url = new URL(path);
                    HttpURLConnection c = (HttpURLConnection) url.openConnection();
                    c.setRequestMethod("GET");
                    // c.setDoOutput(true);
                    c.getResponseCode();
                    c.connect();
                    length = c.getContentLength();

                    String size = new DecimalFormat("##.##")
                            .format((double) ((double) length / 1024) / 1024)
                            + " MB";

                    String PATH = Environment.getExternalStorageDirectory()
                            + "/download/";
                    File file = new File(PATH);
                    file.mkdirs();
                    File outputFile = new File(file, "app.apk");
                    FileOutputStream fos = new FileOutputStream(outputFile);

                    InputStream is = c.getInputStream();

                    int bytes = 0;
                    byte[] buffer = new byte[1024];
                    int len1 = 0;

                    while ((len1 = is.read(buffer)) != -1) {

                        bytes = (bytes + len1);

                        s = new DecimalFormat("##.##")
                                .format((double) ((double) (bytes / 1024)) / 1024);

                        p = s.length() == 3 ? s + "0" : s;

                        p = p + " MB";
                        data.value = (int) ((double) (((double) bytes) / length) * 100);

                        data.name = "Download " + p + "/" + size;
                        publishProgress(data);

                        fos.write(buffer, 0, len1);

                    }
                    fos.close();
                    is.close();

                    return CommonString.KEY_SUCCESS;

                } catch (PackageManager.NameNotFoundException e) {
                    // TODO Auto-generated catch block
                /*    final AlertMessage message = new AlertMessage(
                            AutoupdateActivity.this,
                            AlertMessage.MESSAGE_EXCEPTION, "download", e);
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            message.showMessage();
                        }
                    });*/

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            showMessage(CommonString.MESSAGE_EXCEPTION);
                        }
                    });


                } catch (MalformedURLException e) {

                  /*  final AlertMessage message = new AlertMessage(
                            AutoupdateActivity.this,
                            AlertMessage.MESSAGE_EXCEPTION, "download", e);
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            message.showMessage();
                        }
                    });*/

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            showMessage(CommonString.MESSAGE_EXCEPTION);
                        }
                    });

                } catch (IOException e) {
                   /* final AlertMessage message = new AlertMessage(
                            AutoupdateActivity.this,
                            AlertMessage.MESSAGE_SOCKETEXCEPTION, "update", e);
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            message.showMessage();
                        }
                    });*/

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            showMessage(CommonString.MESSAGE_SOCKETEXCEPTION);
                        }
                    });

                } catch (Exception e) {
                   /* final AlertMessage message = new AlertMessage(
                            AutoupdateActivity.this,
                            AlertMessage.MESSAGE_EXCEPTION, "download", e);
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            message.showMessage();
                        }
                    });
*/
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            showMessage(CommonString.MESSAGE_EXCEPTION);
                        }
                    });
                }

                return "";
            }

            @Override
            protected void onProgressUpdate(Data... values) {
                // TODO Auto-generated method stub

                pb.setProgress(values[0].value);
                percentage.setText(values[0].value + "%");
                message.setText(values[0].name);

            }

            @Override
            protected void onPostExecute(String result) {
                // TODO Auto-generated method stub
                super.onPostExecute(result);
                dialog.dismiss();

                if (result.equals(CommonString.KEY_SUCCESS)) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setDataAndType(Uri.fromFile(new File(Environment
                                    .getExternalStorageDirectory()
                                    + "/download/"
                                    + "app.apk")),
                            "application/vnd.android.package-archive");
                    startActivity(i);

                    AutoupdateActivity.this.finish();
                }

            }

        }

        class Data {
            int value;
            String name;
        }

    public void showMessage(String msg){

        new AlertDialog.Builder(AutoupdateActivity.this)
                .setTitle("Update Dialog")
                .setMessage(msg)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                    }
                })

                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();


    }

    }
