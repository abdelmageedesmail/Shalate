package com.shalate.red.shalate.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.shalate.red.shalate.Activity.SharePost;
import com.shalate.red.shalate.R;
import com.universalvideoview.UniversalMediaController;
import com.universalvideoview.UniversalVideoView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;

import static android.app.Activity.RESULT_CANCELED;

//import hb.xvideoplayer.MxVideoPlayer;
//import hb.xvideoplayer.MxVideoPlayerWidget;

public class VideoFragment extends Fragment {
    private static final int REQUEST_TAKE_GALLERY_VIDEO = 4;
    ImageView ivVideo, icVolume, ic_pause, ivThumbnail;
    UniversalVideoView mVideoView;
    UniversalMediaController mMediaController;
    //    MxVideoPlayerWidget videoPlayerWidget;
    private String filemanagerstring;
    private String selectedImagePath;
    private static final String VIDEO_DIRECTORY = "/demonuts";
    private int GALLERY = 1, CAMERA = 2;
    public static int fromFragment = 2;
    public static File file;
    TextView tvNext;
    public static Uri contentURI;
    public static String selectedVideoPath;
    VideoView videoViewPlayer;

    public VideoFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_video, container, false);
        bind(v);
        return v;
    }

    private void bind(View v) {
        ivVideo = v.findViewById(R.id.ivVideo);
        icVolume = v.findViewById(R.id.icVolume);
        mVideoView = v.findViewById(R.id.videoView);
        mMediaController = v.findViewById(R.id.media_controller);
        ic_pause = v.findViewById(R.id.ic_pause);
        tvNext = v.findViewById(R.id.tvNext);
        videoViewPlayer = v.findViewById(R.id.videoViewPlayer);
//        videoPlayerWidget = v.findViewById(R.id.mpw_video_player);
        ivThumbnail = v.findViewById(R.id.ivThumbnail);
        mVideoView.setMediaController(mMediaController);

        ivVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPictureDialog();
            }
        });

        tvNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (file != null) {

                    Intent intent = new Intent(getActivity(), SharePost.class);
                    intent.putExtra("fromFragment", "video");
                    startActivity(intent);
                } else {
                    Toast.makeText(getActivity(), getString(R.string.addData), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    private void showPictureDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(getActivity());
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select video from gallery",
                "Record video from camera"};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                chooseVideoFromGallary();
                                break;
                            case 1:
                                takeVideoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    public void chooseVideoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, GALLERY);
    }

    private void takeVideoFromCamera() {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.d("result", "" + resultCode);
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode == RESULT_CANCELED) {
            Log.d("what", "cancle");
            return;
        }
        if (requestCode == GALLERY) {
            Log.d("what", "gale");
            if (data != null) {
                contentURI = data.getData();
                selectedVideoPath = getPath(contentURI);
                file = new File(selectedVideoPath);
                saveVideoToInternalStorage(selectedVideoPath);
//                videoPlayerWidget.startPlay(selectedVideoPath, MxVideoPlayer.SCREEN_LAYOUT_NORMAL, "");
                videoViewPlayer.setVideoPath(selectedVideoPath);
                videoViewPlayer.seekTo(1);
                MediaController mediaController = new MediaController(getActivity());
                mediaController.setAnchorView(videoViewPlayer);
                videoViewPlayer.setMediaController(mediaController);
            }

        } else if (requestCode == CAMERA) {
            Uri contentURI = data.getData();
            selectedVideoPath = getPath(contentURI);
            file = new File(selectedVideoPath);
            Log.d("frrr", selectedVideoPath);
            saveVideoToInternalStorage(selectedVideoPath);
            mVideoView.setVideoURI(contentURI);
            mVideoView.requestFocus();
//            mVideoView.start();

            videoViewPlayer.setVideoPath(selectedVideoPath);
            videoViewPlayer.seekTo(1);
            MediaController mediaController = new MediaController(getActivity());
            mediaController.setAnchorView(videoViewPlayer);
            videoViewPlayer.setMediaController(mediaController);
        }
    }

    private void saveVideoToInternalStorage(String filePath) {

        File newfile;
        try {
            File currentFile = new File(filePath);
            File wallpaperDirectory = new File(Environment.getExternalStorageDirectory() + VIDEO_DIRECTORY);
            newfile = new File(wallpaperDirectory, Calendar.getInstance().getTimeInMillis() + ".mp4");

            if (!wallpaperDirectory.exists()) {
                wallpaperDirectory.mkdirs();
            }

            if (currentFile.exists()) {
                InputStream in = new FileInputStream(currentFile);
                OutputStream out = new FileOutputStream(newfile);
                // Copy the bits from instream to outstream
                byte[] buf = new byte[1024];
                int len;

                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close();
                out.close();
                Log.v("vii", "Video file saved successfully.");
            } else {
                Log.v("vii", "Video saving failed. Source file missing.");
            }
        } catch (Exception e) {
            e.printStackTrace();
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
