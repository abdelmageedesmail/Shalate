package com.shalate.red.shalate.Fragment;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.jacksonandroidnetworking.JacksonParserFactory;
import com.mukesh.countrypicker.CountryPicker;
import com.mukesh.countrypicker.CountryPickerListener;
import com.r0adkll.slidr.Slidr;
import com.shalate.red.shalate.Activity.ConfirmEmailActivity;
import com.shalate.red.shalate.R;
import com.shalate.red.shalate.Utilities.PermissionsEnabled;
import com.shalate.red.shalate.Utilities.PrefrencesStorage;
import com.shalate.red.shalate.Utilities.ProgressLoading;
import com.shalate.red.shalate.internet.Urls;
import com.shalate.red.shalate.notification.FCMRegistrationService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentClientRegister extends Fragment implements View.OnClickListener {

    EditText etName, etEmail, etPhone, etPassword;
    LinearLayout liCreate;
    ProgressLoading loading;
    private String macAddress;
    private String lang;
    ImageView profile_image, ivBack;
    int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private String imagepath;
    private File imageFile;
    private Uri selectedImageUri;
    private String selectedImagePath;
    private Bitmap bm;
    String substring;
    private FrameLayout frCountry;
    private TextView tvCode, cCode;
    PrefrencesStorage storage;
    private String ACode, replaceCode;

    public static Bitmap thumbnail;

    public static Bitmap bitmap;
    public static File cameraFile;
    public static File destination;
    private Uri imageUri;
    private String imageurl;
    private ContentValues values;

    public FragmentClientRegister() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loading = new ProgressLoading(getActivity());
        storage = new PrefrencesStorage(getActivity());
        PermissionsEnabled enabled = new PermissionsEnabled(getActivity());
        enabled.enablePermission(2, PermissionsEnabled.READ_AND_WRITE_EXTERNAL_REQUEST_CODE);
        enabled.enablePermission(4, PermissionsEnabled.CAMERA_USAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Slidr.attach(getActivity());
        View inflate = inflater.inflate(R.layout.fragment_fragment_client_register, container, false);
        bind(inflate);
        getMacAddress();
        return inflate;
    }

    private void bind(View v) {
        etName = v.findViewById(R.id.etName);
        etEmail = v.findViewById(R.id.etEmail);
        etPhone = v.findViewById(R.id.etPhone);
        etPassword = v.findViewById(R.id.etPassword);
        liCreate = v.findViewById(R.id.liCreate);
        profile_image = v.findViewById(R.id.profile_image);
        frCountry = v.findViewById(R.id.frCountry);
        tvCode = v.findViewById(R.id.tvCode);
        cCode = v.findViewById(R.id.code);

        frCountry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final CountryPicker picker = CountryPicker.newInstance("Select Country");  // dialog title
                picker.setListener(new CountryPickerListener() {
                    @Override
                    public void onSelectCountry(String name, String code, String dialCode, int flagDrawableResID) {
                        // Implement your code here
                        tvCode.setText(name);
                        ACode = dialCode;
                        replaceCode = ACode.replace("+", "00");
                        cCode.setText("( " + dialCode + " )");
                        picker.dismiss();

                    }
                });
                picker.show(getActivity().getSupportFragmentManager(), "COUNTRY_PICKER");
            }
        });
        liCreate.setOnClickListener(this);
        profile_image.setOnClickListener(this);
    }

    private void setUpValidation() {
        if (etName.getText().toString().equals("") || etName.getText().toString().isEmpty()) {
            etName.setError(getString(R.string.empty));
        } else if (etPhone.getText().toString().equals("") || etPhone.getText().toString().isEmpty()) {
            etPhone.setError(getString(R.string.empty));
        } else if (etPassword.getText().toString().equals("") || etPassword.getText().toString().isEmpty()) {
            etPassword.setError(getString(R.string.empty));
        } else {
            register();
        }
    }

    private void selectImage() {
        final CharSequence[] items = {getString(R.string.pickPhoto), getString(R.string.chooseImage),
                getString(R.string.cancel)};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.changePhoto));
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals(getString(R.string.pickPhoto))) {

                    values = new ContentValues();
                    values.put(MediaStore.Images.Media.TITLE, "New Picture");
                    values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
                    imageUri = getActivity().getContentResolver().insert(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(intent, REQUEST_CAMERA);

//                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                    startActivityForResult(intent, REQUEST_CAMERA);
                } else if (items[item].equals(getString(R.string.chooseImage))) {

                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(
                            Intent.createChooser(intent, "Select File"),
                            SELECT_FILE);


                } else if (items[item].equals(getString(R.string.cancel))) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    String mPhotoPath;

    private void onCaptureImageResult(Intent data) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//        bm.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
//        ivPhoto.setImageBitmap(bm);

        try {
            thumbnail = MediaStore.Images.Media.getBitmap(
                    getActivity().getContentResolver(), imageUri);
            profile_image.setImageBitmap(thumbnail);
            imageurl = getRealPathFromURI(imageUri);
            imageFile = new File(imageurl);
            destination = new File(Environment.getExternalStorageDirectory(),
                    System.currentTimeMillis() + ".jpg");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
    //************************* HERE ***************************************************************

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {
        selectedImageUri = data.getData();
        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = getActivity().managedQuery(selectedImageUri, projection, null, null,
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
            profile_image.setImageBitmap(finalImage);
        } else {
            profile_image.setImageBitmap(bm);
        }
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Bitmap retVal;

        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        retVal = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);

        return retVal;
    }

    private void register() {
        if (replaceCode == null) {
            String replace = cCode.getText().toString().replace("(", "");
            String replace1 = replace.replace(")", "");
            replaceCode = replace1.replace("+", "00");
        }
        if (Locale.getDefault().getDisplayLanguage().equals("العربية")) {
            lang = "ar";
        } else {
            lang = "en";
        }
        loading.showLoading();
        AndroidNetworking.initialize(getActivity());
        AndroidNetworking.setParserFactory(new JacksonParserFactory());
        ANRequest.MultiPartBuilder upload = AndroidNetworking.upload(Urls.registerUser);
        upload.addMultipartParameter("password", etPassword.getText().toString())
                .addMultipartParameter("username", etName.getText().toString())
                .addMultipartParameter("phone", etPhone.getText().toString())
                .addMultipartParameter("device_type", "android")
                .addMultipartParameter("device_token", FCMRegistrationService.token)
                .addMultipartParameter("device_unique_address", macAddress)
                .addMultipartParameter("lang", lang)
                .addMultipartParameter("tele_code", replaceCode)
                .addMultipartParameter("account_type", "client");
        if (imageFile != null) {
            upload.addMultipartFile("image", imageFile);
        }
        upload.setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            loading.cancelLoading();
                            Log.e("response", "" + response);
                            String status = response.getString("status");
                            if (status.equals("1")) {
                                JSONObject data = response.getJSONObject("data");
                                Intent intent = new Intent(getActivity(), ConfirmEmailActivity.class);
                                intent.putExtra("activeCode", data.getString("active_code"));
                                intent.putExtra("phone", etPhone.getText().toString());
                                intent.putExtra("tele_code", replaceCode);
                                storage.storeKey("isEnterCode", "1");
                                startActivity(intent);
                            } else {
                                JSONArray errors = response.getJSONArray("errors");
                                for (int i = 0; i < errors.length(); i++) {
                                    JSONObject jsonObject = errors.getJSONObject(i);
                                    String key = jsonObject.getString("key");
                                    if (key.equals("email")) {
                                        Toast.makeText(getActivity(), getString(R.string.emailAlreadyTaken), Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getActivity(), getString(R.string.errorPhone), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e("error", "" + anError.getMessage());
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.liCreate:
                setUpValidation();
                break;
            case R.id.profile_image:
                selectImage();
                break;
        }
    }
}

