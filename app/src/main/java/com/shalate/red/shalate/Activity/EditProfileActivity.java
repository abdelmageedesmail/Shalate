package com.shalate.red.shalate.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.jacksonandroidnetworking.JacksonParserFactory;
import com.r0adkll.slidr.Slidr;
import com.shalate.red.shalate.R;
import com.shalate.red.shalate.Utilities.PermissionsEnabled;
import com.shalate.red.shalate.Utilities.PrefrencesStorage;
import com.shalate.red.shalate.Utilities.ProgressLoading;
import com.shalate.red.shalate.internet.Urls;
import com.shalate.red.shalate.notification.FCMRegistrationService;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener {

    EditText etName, etEmail, etPhone, etPassword;
    LinearLayout liCreate;
    ImageView ivProfile, ivBack;
    ProgressLoading loading;
    PrefrencesStorage storage;
    private String macAddress;
    private String lang;
    int SELECT_FILE = 1;
    private String imagepath;
    private File imageFile;
    private Uri selectedImageUri;
    private String selectedImagePath;
    private Bitmap bm;
    String substring;
    Button btnEdit, btnEditPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Slidr.attach(this);
        bind();
        init();
        fillView();
        setUpPermission();
        getMacAddress();
    }

    private void init() {
        storage = new PrefrencesStorage(this);
        loading = new ProgressLoading(this);
    }

    private void bind() {
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etPassword = findViewById(R.id.etPassword);
        liCreate = findViewById(R.id.liCreate);
        ivProfile = findViewById(R.id.profile_image);
        ivBack = findViewById(R.id.ivBack);
        btnEditPass = findViewById(R.id.btnEditPass);
        btnEdit = findViewById(R.id.btnEdit);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        btnEdit.setOnClickListener(this);
        ivProfile.setOnClickListener(this);
        btnEditPass.setOnClickListener(this);
        btnEdit.setOnClickListener(this);
    }

    private void fillView() {
        etName.setText(storage.getKey("name"));
        etEmail.setText(storage.getKey("email"));
        etPhone.setText(storage.getKey("phone"));
        Picasso.with(this).load(storage.getKey("image")).into(ivProfile);
    }

    private void setUpPermission() {
        PermissionsEnabled enabled = new PermissionsEnabled(this);
        enabled.enablePermission(2, PermissionsEnabled.READ_AND_WRITE_EXTERNAL_REQUEST_CODE);
    }

    private void setUpValidation() {
        if (etName.getText().toString().equals("") || etName.getText().toString().isEmpty()) {
            etName.setError(getString(R.string.empty));
        } else if (etEmail.getText().toString().equals("") || etEmail.getText().toString().isEmpty()) {
            etEmail.setError(getString(R.string.empty));
        } else if (etPhone.getText().toString().equals("") || etPhone.getText().toString().isEmpty()) {
            etPhone.setError(getString(R.string.empty));
        } else {
            update();
        }
    }

    private void update() {
        if (Locale.getDefault().getDisplayLanguage().equals("العربية")) {
            lang = "ar";
        } else {
            lang = "en";
        }
        loading.showLoading();
        String url = Urls.updateProfile + storage.getId() + "lang=" + lang;
        AndroidNetworking.initialize(EditProfileActivity.this);
        AndroidNetworking.setParserFactory(new JacksonParserFactory());
        AndroidNetworking.upload(url)
                .addMultipartParameter("username", etName.getText().toString())
                .addMultipartFile("image", imageFile)
                .addMultipartParameter("device_type", "android")
                .addMultipartParameter("device_token", FCMRegistrationService.token)
                .addMultipartParameter("device_unique_address", macAddress)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            loading.cancelLoading();
                            String status = response.getString("status");
                            if (status.equals("1")) {
                                JSONArray data = response.getJSONArray("data");
                                JSONObject jsonObject = data.getJSONObject(0);
                                PrefrencesStorage storage = new PrefrencesStorage(EditProfileActivity.this);
                                storage.storeId(jsonObject.getString("id"));
                                storage.storeKey("name", jsonObject.getString("username"));
                                storage.storeKey("email", jsonObject.getString("email"));
                                storage.storeKey("phone", jsonObject.getString("phone"));
                                storage.storeKey("account_type", jsonObject.getString("account_type"));
                                storage.storeKey("image", jsonObject.getString("image"));
                                startActivity(new Intent(EditProfileActivity.this, HomePage.class));
                            } else {
                                JSONArray errors = response.getJSONArray("errors");
                                for (int i = 0; i < errors.length(); i++) {
                                    JSONObject jsonObject = errors.getJSONObject(i);
                                    String key = jsonObject.getString("key");
                                    if (key.equals("email")) {
                                        Toast.makeText(EditProfileActivity.this, getString(R.string.emailAlreadyTaken), Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(EditProfileActivity.this, getString(R.string.phoneAlreadyTaken), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        loading.cancelLoading();
                    }
                });

    }

    private void getMacAddress() {

        try {
            // get all the interfaces
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            //find network interface wlan0
            for (NetworkInterface networkInterface : all) {
                if (!networkInterface.getName().equalsIgnoreCase("wlan0")) continue;
                //get the hardware address (MAC) of the interface
                byte[] macBytes = networkInterface.getHardwareAddress();
                if (macBytes == null) {
//                    return "";
                }


                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    //gets the last byte of b
                    res1.append(Integer.toHexString(b & 0xFF) + ":");
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                macAddress = res1.toString();
                Log.e("macAddress", macAddress);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void selectImage() {

        Intent intent = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(
                Intent.createChooser(intent, "Select File"),
                SELECT_FILE);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE) {
                onSelectFromGalleryResult(data);
            }
        }
    }
    //************************* HERE ***************************************************************

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {
        selectedImageUri = data.getData();
        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = managedQuery(selectedImageUri, projection, null, null,
                null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();
        selectedImagePath = cursor.getString(column_index);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(selectedImagePath, options);
        final int REQUIRED_SIZE = 200;
        int scale = 1;
        while (options.outWidth / scale / 2 >= REQUIRED_SIZE && options.outHeight / scale / 2 >= REQUIRED_SIZE)
            scale *= 2;
        options.inSampleSize = scale;
        options.inJustDecodeBounds = false;
        bm = BitmapFactory.decodeFile(selectedImagePath, options);
        substring = selectedImagePath.substring(selectedImagePath.lastIndexOf(".") + 1);

        imagepath = selectedImageUri.getPath();
        imageFile = new File(selectedImagePath);
        Bitmap finalImage = null;
        try {
            ExifInterface ei = new ExifInterface(selectedImagePath);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

            Log.e("orientation", orientation + "");
            //finalImage = rotateImage(bm, 270);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    finalImage = rotateImage(bm, 90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    finalImage = rotateImage(bm, 180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    finalImage = rotateImage(bm, 270);
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (finalImage != null) {
            ivProfile.setImageBitmap(finalImage);
        } else {
            ivProfile.setImageBitmap(bm);
        }
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Bitmap retVal;

        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        retVal = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);

        return retVal;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnEdit:
                setUpValidation();
                break;
            case R.id.profile_image:
                selectImage();
                break;
            case R.id.btnEditPass:
                startActivity(new Intent(EditProfileActivity.this, UpdatePassword.class));
                break;
        }
    }
}
