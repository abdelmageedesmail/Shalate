package com.shalate.red.shalate.Fragment;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.shalate.red.shalate.Activity.SharePost;
import com.shalate.red.shalate.R;
import com.shalate.red.shalate.Utilities.PermissionsEnabled;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class CameraFragment extends Fragment implements View.OnClickListener {

    ImageView ivCamera, ivPhoto;
    TextView tvPickPhoto, tvNext;
    int REQUEST_CAMERA = 0;
    public static Bitmap thumbnail;
    public static int fromFragment = 1;
    public static Bitmap bitmap;
    public static File cameraFile;
    public static File destination;
    private ContentValues values;
    private Uri imageUri;
    private String imageurl;

    public CameraFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PermissionsEnabled enabled = new PermissionsEnabled(getActivity());
        enabled.enablePermission(4, PermissionsEnabled.CAMERA_USAGE);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_camera, container, false);
        bing(v);
        return v;
    }


    private void bing(View v) {
        ivCamera = v.findViewById(R.id.ivCamera);
        ivPhoto = v.findViewById(R.id.ivPhoto);
        tvPickPhoto = v.findViewById(R.id.tvPickPhoto);
        tvNext = v.findViewById(R.id.tvNext);
        tvNext.setOnClickListener(this);
        ivCamera.setOnClickListener(this);
    }

    private void selectImage() {
        values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
        imageUri = getActivity().getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }


    private void onCaptureImageResult(Intent data) {
//        bm = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//        bm.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
//        ivPhoto.setImageBitmap(bm);

        try {
            thumbnail = MediaStore.Images.Media.getBitmap(
                    getActivity().getContentResolver(), imageUri);
            ivPhoto.setImageBitmap(thumbnail);
            imageurl = getRealPathFromURI(imageUri);
            cameraFile = new File(imageurl);
            destination = new File(Environment.getExternalStorageDirectory(),
                    System.currentTimeMillis() + ".jpg");
        } catch (Exception e) {
            e.printStackTrace();
        }

//        FileOutputStream fo;
//        try {
////            destination.createNewFile();
////            fo = new FileOutputStream(destination);
////            fo.write(bytes.toByteArray());
////            fo.close();
////            String absolutePath = destination.getAbsolutePath();
////            cameraFile = new File(absolutePath);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        tvPickPhoto.setVisibility(View.GONE);

    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivCamera:
//                captureImage();
                selectImage();
                break;
            case R.id.tvNext:
                if (cameraFile != null) {
                    Intent intent = new Intent(getActivity(), SharePost.class);
                    intent.putExtra("fromFragment", "camera");
                    startActivity(intent);
                } else {
                    Toast.makeText(getActivity(), getString(R.string.addData), Toast.LENGTH_SHORT).show();
                }

        }
    }
}
