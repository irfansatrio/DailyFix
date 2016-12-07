package d3if0025.me.dailyfix;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
/**
 * Created by irfan on 07/12/2016.
 */

public class ActivityDetail extends AppCompatActivity {
    private CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail
        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == 1 && resultCode == RESULT_OK){
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            ImageView mImageView = (ImageView) findViewById(R.id.profile);
            if(mImageView != null){
                mImageView.setImageBitmap(imageBitmap);

            }
            showSnackbar();
        }
        else if(requestCode == 2 && resultCode == RESULT_OK){
            Bitmap bm = null;
            if(data != null){
                try {
                    bm = MediaStore.Images.Media.getBitmap(
                            getApplicationContext().getContentResolver(),data.getData());
                }catch (IOException e){
                    e.printStackTrace();
                }

            }
            ImageView Mimage2 = (ImageView) findViewById(R.id.profile);
            if(Mimage2 != null){
                Mimage2.setImageBitmap(bm);
            }
            showSnackbar();
        }

    }

    public void Dialog(View view){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Ambil foto profil baru dari mana ?");
        alertDialogBuilder.setTitle("Ganti Foto");

        alertDialogBuilder.setNegativeButton("BATAL",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();

                startActivityForResult(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            }
        });

        alertDialogBuilder.setNeutralButton("GALERI",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(intent, "pilih foto"), 2);
            }
        });
        alertDialogBuilder.setPositiveButton("KAMERA", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent in = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if(in.resolveActivity(getPackageManager()) !=null){
                    startActivityForResult(in, 1);
                }
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void startActivityForResult(String actionCloseSystemDialogs) {

    }

    private void showSnackbar(){
        final CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id
                .coordinatorLayout);

        Snackbar snackbar = Snackbar
                .make(coordinatorLayout, "Foto Telah Diganti", Snackbar.LENGTH_LONG)
                .setAction("Undo", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(ActivityDetail.this, "Fungsi Undo Belum Jadi", Toast.LENGTH_LONG).show();
                    }
                });

        snackbar.setActionTextColor(Color.RED);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(Color.DKGRAY);
        TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
    }
}
