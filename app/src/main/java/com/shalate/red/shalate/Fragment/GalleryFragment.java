package com.shalate.red.shalate.Fragment;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.darsh.multipleimageselect.activities.AlbumSelectActivity;
import com.darsh.multipleimageselect.helpers.Constants;
import com.darsh.multipleimageselect.models.Image;
import com.rbrooks.indefinitepagerindicator.IndefinitePagerIndicator;
import com.shalate.red.shalate.Activity.SharePost;
import com.shalate.red.shalate.Adapter.UriAdapter;
import com.shalate.red.shalate.R;
import com.shalate.red.shalate.Utilities.PermissionsEnabled;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GalleryFragment extends Fragment {

    private static final int REQUEST_CODE_CHOOSE = 23;
    RecyclerView rvImages;
    ImageView icGallery;
    TextView tvNext;
    private UriAdapter mAdapter;
    List<Uri> mSelected = null;
    private IndefinitePagerIndicator indefinitePagerIndicator;
    public static int fromFragment = 3;
    public static ArrayList<File> arrayList;
    private Uri selectedImageUri;
    int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private String imagepath;
    private File imageFile;

    private String selectedImagePath;
    private Bitmap bm;
    String substring;

    public GalleryFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        PermissionsEnabled enabled = new PermissionsEnabled(getActivity());
        enabled.enablePermission(2, PermissionsEnabled.READ_AND_WRITE_EXTERNAL_REQUEST_CODE);
        View v = inflater.inflate(R.layout.fragment_gallery, container, false);
        bind(v);
        init();
        rvImages.setAdapter(mAdapter = new UriAdapter());
        rvImages.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        indefinitePagerIndicator.attachToRecyclerView(rvImages);
        return v;
    }

    private void init() {
        mSelected = new ArrayList<>();
        arrayList = new ArrayList<>();
    }

    private void bind(View v) {
        rvImages = v.findViewById(R.id.rvImages);
        icGallery = v.findViewById(R.id.icGallery);
        indefinitePagerIndicator = v.findViewById(R.id.recyclerview_pager_indicator);
        tvNext = v.findViewById(R.id.tvNext);
//        icGallery.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                RxPermissions rxPermissions = new RxPermissions(getActivity());
//                rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                        .subscribe(new Observer<Boolean>() {
//                            @Override
//                            public void onSubscribe(Disposable d) {
//
//                            }
//
//                            @Override
//                            public void onNext(Boolean aBoolean) {
//                                if (aBoolean) {
//                                    Matisse from = Matisse.from(getActivity());
//                                    from.choose(MimeType.allOf())
//                                            .countable(true)
//                                            .maxSelectable(5)
//                                            .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
//                                            .thumbnailScale(0.85f)
//                                            .imageEngine(new GlideEngine())
//                                            .forResult(REQUEST_CODE_CHOOSE);
//                                }
//                            }
//
//                            @Override
//                            public void onError(Throwable e) {
//
//                            }
//
//                            @Override
//                            public void onComplete() {
//
//                            }
//                        });
//
//            }
//        });

        icGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AlbumSelectActivity.class);
                intent.putExtra(Constants.INTENT_EXTRA_LIMIT, 5);
                startActivityForResult(intent, 1);
            }
        });

        tvNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (arrayList.size() > 0) {
                    Intent intent = new Intent(getActivity(), SharePost.class);
                    intent.putExtra("fromFragment", "gallery");
                    startActivity(intent);
                } else {
                    Toast.makeText(getActivity(), getString(R.string.addData), Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (data != null) {
                mSelected.add(data.getData());
//                mSelected = Matisse.obtainResult(data);
//                Log.e("Matisse", "mSelected: " + mSelected);
//                List<Uri> uris = Matisse.obtainResult(data);
                ArrayList<Image> images = data.getParcelableArrayListExtra(Constants.INTENT_EXTRA_IMAGES);

                for (int i = 0; i < images.size(); i++) {
//                    String path1 = getPath(mSelected.get(i));
                    String path = images.get(i).path;
                    File file = new File(path);
                    Log.e("paths", "" + path);
                    arrayList.add(file);
                }

                mAdapter.setData(getActivity(), images);
            }
        }
    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Video.Media.DATA};
        Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            // HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
            // THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }
}