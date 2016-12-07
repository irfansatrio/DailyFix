package d3if0025.me.dailyfix;

/**
 * Created by irfan on 28/11/2016.
 */
import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.R.attr.data;
import static d3if0025.me.dailyfix.DataUtils.*;


    public class EditActivity extends ActionBarActivity implements Toolbar.OnMenuItemClickListener {

        private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
        private Button btnSelect;


        private EditText titleEdit, bodyEdit;
        private RelativeLayout relativeLayoutEdit;
        private Toolbar toolbar;
        private MenuItem menuHideBody;
       // ImageView result;

        private InputMethodManager imm;
        private Bundle bundle;

        private String[] colourArr;
        private int[] colourArrResId;
        private int[] fontSizeArr;
        private String[] fontSizeNameArr;
        private String colour = "#FFFFFF";
        private int fontSize = 18;
        private Boolean hideBody = false;
        private AlertDialog fontDialog, saveChangesDialog;
        private Object TakeAPicture;
        private String userChoosenTask;
        private ImageView ivImage;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            btnSelect = (Button) findViewById(R.id.btnSelectPhoto);
            btnSelect.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    selectImage();
                }
            });
            ivImage = (ImageView) findViewById(R.id.ivImage);



            if (Build.VERSION.SDK_INT >= 18)
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_USER);

            else
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);

          /*  colourArr = getResources().getStringArray(R.array.colours);

            colourArrResId = new int[colourArr.length];
            for (int i = 0; i < colourArr.length; i++)
                colourArrResId[i] = Color.parseColor(colourArr[i]);*/

            fontSizeArr = new int[] {14, 18, 22};
            fontSizeNameArr = getResources().getStringArray(R.array.fontSizeNames);

            setContentView(R.layout.activity_edit);


            toolbar = (Toolbar)findViewById(R.id.toolbarEdit);
            titleEdit = (EditText)findViewById(R.id.titleEdit);
            bodyEdit = (EditText)findViewById(R.id.bodyEdit);
            relativeLayoutEdit = (RelativeLayout)findViewById(R.id.relativeLayoutEdit);
            ScrollView scrollView = (ScrollView)findViewById(R.id.scrollView);

            imm = (InputMethodManager) this.getSystemService(INPUT_METHOD_SERVICE);

            if (toolbar != null)
                initToolbar();

            scrollView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (!bodyEdit.isFocused()) {
                        bodyEdit.requestFocus();
                        bodyEdit.setSelection(bodyEdit.getText().length());
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,
                                InputMethodManager.HIDE_IMPLICIT_ONLY);

                        return true;
                    }

                    return false;
                }
            });

            bundle = getIntent().getExtras();

            if (bundle != null) {
                if (bundle.getInt(NOTE_REQUEST_CODE) != NEW_NOTE_REQUEST) {
                  //  colour = bundle.getString(NOTE_COLOUR);
                    fontSize = bundle.getInt(NOTE_FONT_SIZE);
                    hideBody = bundle.getBoolean(NOTE_HIDE_BODY);

                    titleEdit.setText(bundle.getString(NOTE_TITLE));
                    bodyEdit.setText(bundle.getString(NOTE_BODY));
                    bodyEdit.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);

                    if (hideBody)
                        menuHideBody.setTitle(R.string.action_show_body);
                }

                else if (bundle.getInt(NOTE_REQUEST_CODE) == NEW_NOTE_REQUEST) {
                    titleEdit.requestFocus();
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                }
              //  relativeLayoutEdit.setBackgroundColor(Color.parseColor(colour));
            }

            initDialogs(this);
        }

        protected void initToolbar() {
            toolbar.setTitle("");

            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
            toolbar.inflateMenu(R.menu.menu_edit);
            toolbar.setOnMenuItemClickListener(this);
            Menu menu = toolbar.getMenu();

            if (menu != null)
                menuHideBody = menu.findItem(R.id.action_hide_show_body);
        }
        protected void initDialogs(Context context) {
            fontDialog = new AlertDialog.Builder(context)
                    .setTitle(R.string.dialog_font_size)
                    .setItems(fontSizeNameArr, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            fontSize = fontSizeArr[which];
                            bodyEdit.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
                        }
                    })
                    .setNeutralButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create();
            saveChangesDialog = new AlertDialog.Builder(context)
                    .setMessage(R.string.dialog_save_changes)
                    .setPositiveButton(R.string.yes_button, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            if (!isEmpty(titleEdit))
                                saveChanges();

                            else
                                toastEditTextCannotBeEmpty();
                        }
                    })
                    .setNegativeButton(R.string.no_button, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            if (bundle != null && bundle.getInt(NOTE_REQUEST_CODE) ==
                                    NEW_NOTE_REQUEST) {

                                Intent intent = new Intent();
                                intent.putExtra("request", "discard");

                                setResult(RESULT_CANCELED, intent);

                                imm.hideSoftInputFromWindow(titleEdit.getWindowToken(), 0);

                                dialog.dismiss();
                                finish();
                                overridePendingTransition(0, 0);
                            }
                        }
                    })
                    .create();
        }
        public static boolean isTablet(Context context) {
            return (context.getResources().getConfiguration().screenLayout &
                    Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
        }

        public boolean onMenuItemClick(MenuItem item) {
            int id = item.getItemId();

           /*if (id == R.id.action_note_colour) {
                colorPickerDialog.show(getFragmentManager(), "colourPicker");
                return true;
            }


            if (id == R.id.action_font_size) {
                fontDialog.show();
                return true;
            }*/


            if (id == R.id.action_hide_show_body) {

                if (!hideBody) {
                    hideBody = true;
                    menuHideBody.setTitle(R.string.action_show_body);


                    Toast toast = Toast.makeText(getApplicationContext(),
                            getResources().getString(R.string.toast_note_body_hidden),
                            Toast.LENGTH_SHORT);
                    toast.show();
                }

                else {
                    hideBody = false;
                    menuHideBody.setTitle(R.string.action_hide_body);

                    // Toast note body will be shown
                    Toast toast = Toast.makeText(getApplicationContext(),
                            getResources().getString(R.string.toast_note_body_showing),
                            Toast.LENGTH_SHORT);
                    toast.show();
                }

                return true;
            }

            return false;
        }
        protected void saveChanges() {
            Intent intent = new Intent();


            intent.putExtra(NOTE_TITLE, titleEdit.getText().toString());
            intent.putExtra(NOTE_BODY, bodyEdit.getText().toString());
            intent.putExtra(NOTE_COLOUR, colour);
            intent.putExtra(NOTE_FONT_SIZE, fontSize);
            intent.putExtra(NOTE_HIDE_BODY, hideBody);

            setResult(RESULT_OK, intent);

            imm.hideSoftInputFromWindow(titleEdit.getWindowToken(), 0);

            finish();
            overridePendingTransition(0, 0);
        }
        public void onBackPressed() {
            if (bundle.getInt(NOTE_REQUEST_CODE) == NEW_NOTE_REQUEST)
                saveChangesDialog.show();

            else {
                if (!isEmpty(titleEdit)) {
                    if (!(titleEdit.getText().toString().equals(bundle.getString(NOTE_TITLE))) ||
                            !(bodyEdit.getText().toString().equals(bundle.getString(NOTE_BODY))) ||
                            !(colour.equals(bundle.getString(NOTE_COLOUR))) ||
                            fontSize != bundle.getInt(NOTE_FONT_SIZE) ||
                            hideBody != bundle.getBoolean(NOTE_HIDE_BODY)) {

                        saveChanges();
                    }

                    else {
                        imm.hideSoftInputFromWindow(titleEdit.getWindowToken(), 0);

                        finish();
                        overridePendingTransition(0, 0);
                    }
                }

                else
                    toastEditTextCannotBeEmpty();
            }
        }
        protected boolean isEmpty(EditText editText) {
            return editText.getText().toString().trim().length() == 0;
        }
        protected void toastEditTextCannotBeEmpty() {
            Toast toast = Toast.makeText(getApplicationContext(),
                    getResources().getString(R.string.toast_edittext_cannot_be_empty),
                    Toast.LENGTH_LONG);
            toast.show();
        }
        public void onWindowFocusChanged(boolean hasFocus) {
            super.onWindowFocusChanged(hasFocus);

            if (!hasFocus)
                if (imm != null && titleEdit != null)
                    imm.hideSoftInputFromWindow(titleEdit.getWindowToken(), 0);
        }
        public void onConfigurationChanged(Configuration newConfig) {
           // if (colorPickerDialog != null && colorPickerDialog.isDialogShowing())
             //   colorPickerDialog.dismiss();

            if (fontDialog != null && fontDialog.isShowing())
                fontDialog.dismiss();

            if (saveChangesDialog != null && saveChangesDialog.isShowing())
                saveChangesDialog.dismiss();

            super.onConfigurationChanged(newConfig);
        }
      /*  public void dispatchTakePictureIntent() {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }

        }
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {

            if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                result.setImageBitmap(imageBitmap);
            }
        }*/

        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//
        public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
            switch (requestCode) {
                case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        if(userChoosenTask.equals("Take Photo"))
                            cameraIntent();
                        else if(userChoosenTask.equals("Choose from Library"))
                            galleryIntent();
                    } else {
                        //code for deny
                    }
                    break;
            }
        }

        private void selectImage() {
            final CharSequence[] items = { "Take Photo", "Choose from Library",
                    "Cancel" };

            AlertDialog.Builder builder = new AlertDialog.Builder(EditActivity.this);
            builder.setTitle("Add Photo!");
            builder.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int item) {
                    boolean result=Utility.checkPermission(EditActivity.this);

                    if (items[item].equals("Take Photo")) {
                        userChoosenTask ="Take Photo";
                        if(result)
                            cameraIntent();

                    } else if (items[item].equals("Choose from Library")) {
                        userChoosenTask ="Choose from Library";
                        if(result)
                            galleryIntent();

                    } else if (items[item].equals("Cancel")) {
                        dialog.dismiss();
                    }
                }
            });
            builder.show();
        }
        private void galleryIntent()
        {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);//
            startActivityForResult(Intent.createChooser(intent, "Select File"),SELECT_FILE);
        }

        private void cameraIntent()
        {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, REQUEST_CAMERA);
        }

        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == SELECT_FILE)
                    onSelectFromGalleryResult(data);
                else if (requestCode == REQUEST_CAMERA)
                    onCaptureImageResult(data);
            }
        }


        private void onCaptureImageResult(Intent data) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

            File destination = new File(Environment.getExternalStorageDirectory(),
                    System.currentTimeMillis() + ".jpg");

            FileOutputStream fo;
            try {
                destination.createNewFile();
                fo = new FileOutputStream(destination);
                fo.write(bytes.toByteArray());
                fo.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            ivImage.setImageBitmap(thumbnail);
        }

        @SuppressWarnings("deprecation")
        private void onSelectFromGalleryResult(Intent data) {

            Bitmap bm=null;
            if (data != null) {
                try {
                    bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            ivImage.setImageBitmap(bm);
        }

    }










