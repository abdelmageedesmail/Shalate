package com.shalate.red.shalate.Activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.devlomi.record_view.OnRecordClickListener;
import com.devlomi.record_view.OnRecordListener;
import com.devlomi.record_view.RecordButton;
import com.devlomi.record_view.RecordView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jacksonandroidnetworking.JacksonParserFactory;
import com.shalate.red.shalate.Adapter.ChatAdapter;
import com.shalate.red.shalate.Model.ChatModel;
import com.shalate.red.shalate.Model.ListModel;
import com.shalate.red.shalate.R;
import com.shalate.red.shalate.Utilities.GPSTracker;
import com.shalate.red.shalate.Utilities.PermissionsEnabled;
import com.shalate.red.shalate.Utilities.PrefrencesStorage;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class ChatActivity extends AppCompatActivity {


    private static final int GALLERYCODE = 100;
    private static final int REQUEST_CODE_DOC = 200;
    Runnable run;
    Handler handler = new Handler();
    LinearLayout liChat;
    RecyclerView rvChat;
    ImageView ivAttach, ivSend, ivLocation, ivcame, ivVoice;
    EditText edMessage;
    ChatAdapter chatAdapter;
    ArrayList<ChatModel> list = new ArrayList<>();
    // instance
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    FirebaseStorage storage;
    StorageReference storageReference;
    private String customerId;
    private int myId;
    private String chatKey;
    PrefrencesStorage prefrencesStorage;
    ListModel model;
    String from;
    double longitude, latitude;
    private Bitmap bm;
    File imageFile;
    private int REQUEST_CAMERA = 0;
    RecordView recordView;
    FrameLayout liSound;
    private RecordButton recordButton;
    private MediaRecorder mRecorder;
    private String mFileName = null;
    private ArrayList<String> listData;
    public static Bitmap thumbnail;

    public static Bitmap bitmap;
    public static File cameraFile;
    public static File destination;
    private ContentValues values;
    private Uri imageUri;
    private String imageurl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
//        Slidr.attach(this);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        listData = new ArrayList<>();
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        PermissionsEnabled enabled = new PermissionsEnabled(this);
        enabled.enablePermission(1, PermissionsEnabled.LOCATION_REQUEST_CODE);
        enabled.enablePermission(5, PermissionsEnabled.RECORD_AUDIO);
//        customerId = getIntent().getIntExtra("cusId", 9);
        prefrencesStorage = new PrefrencesStorage(this);
        myId = Integer.parseInt(prefrencesStorage.getId());
        customerId = getIntent().getExtras().getString("cusId");
        from = getIntent().getExtras().getString("from");
        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/demoAudio" + System.currentTimeMillis() + ".mp4";
        getIntentData();
        bind();
        init();
        onClicks();
    }


    private void bind() {
        rvChat = findViewById(R.id.rvChat);
        ivAttach = findViewById(R.id.ivAttach);
        ivSend = findViewById(R.id.ivSend);
        edMessage = findViewById(R.id.edMessage);
        ivLocation = findViewById(R.id.ivLocation);
        ivcame = findViewById(R.id.ivcame);
        liChat = findViewById(R.id.liChat);
        recordView = findViewById(R.id.record_view);
        recordButton = findViewById(R.id.record_button);

        //IMPORTANT
        recordButton.setRecordView(recordView);
        setUpRecord();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        GPSTracker mGps = new GPSTracker(this);

        updateLocation();

        ivLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SpannableString content = new SpannableString(getAddress(latitude, longitude));
                content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                HashMap<String, Object> map = new HashMap<>();
                map.put("date", ServerValue.TIMESTAMP);
                map.put("from", String.valueOf(myId));
                map.put("isLocation", "1");
                map.put("message", "" + content);
                map.put("lat", "" + latitude);
                map.put("lon", "" + longitude);
                map.put("postImage", "");
                map.put("postId", "");
                map.put("to", String.valueOf(customerId));
                map.put("type", 1);
                database.getReference("chat").child(chatKey).child("messages").push().setValue(map);
                sendNewMessage();
            }
        });
    }

    private void setUpRecord() {
        recordView.setOnRecordListener(new OnRecordListener() {
            @Override
            public void onStart() {
                recordView.setVisibility(View.VISIBLE);
                liChat.setVisibility(View.GONE);
                Log.e("RecordView", "onStart");
                startRecording();
            }

            @Override
            public void onCancel() {
                recordView.setVisibility(View.GONE);
                liChat.setVisibility(View.VISIBLE);
                Log.e("RecordView", "onCancel");
            }

            @Override
            public void onFinish(long recordTime) {
                recordView.setVisibility(View.GONE);
                liChat.setVisibility(View.VISIBLE);
                Log.e("RecordView", "onFinish");
                stopRecording();
            }

            @Override
            public void onLessThanSecond() {
//                recordView.setVisibility(View.GONE);
                liChat.setVisibility(View.VISIBLE);
                Log.e("RecordView", "onLessThanSecond");
                stopRecording();
            }
        });

        recordButton.setListenForRecord(true);

        //ListenForRecord must be false ,otherwise onClick will not be called
        recordButton.setOnRecordClickListener(new OnRecordClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("RecordButton", "RECORD BUTTON CLICKED");
                startRecording();
            }
        });
    }


    private void startRecording() {
        if (mRecorder == null) {
            try {
                mRecorder = new MediaRecorder();
                mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                mRecorder.setOutputFile(mFileName);
                mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                try {
                    mRecorder.prepare();
                    mRecorder.start();

                } catch (IOException e) {
                    e.printStackTrace();
                    mRecorder = null;
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                    mRecorder = null;
                }
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
    }


    private void stopRecording() {
        if (null != mRecorder) {
            mRecorder.stop();
            mRecorder.reset();
            mRecorder.release();
            mRecorder = null;
        }
        uploadAudio();
    }

    private String getFilename() {
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(filepath);
        if (!file.exists()) {
            file.mkdirs();
        }
        return (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".mp4");
    }

    private void uploadAudio() {
        File file = new File(getFilename());
        Log.e("fileName", getFilename());
        final StorageReference ref = storageReference.child(UUID.randomUUID().toString());
        ref.putFile(Uri.fromFile(new File(mFileName)))
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("date", ServerValue.TIMESTAMP);
                        map.put("from", String.valueOf(myId));

                        map.put("message", "");
                        if (model != null) {
                            map.put("to", String.valueOf(customerId));
                        } else {
                            map.put("to", String.valueOf(customerId));
                        }
                        map.put("postImage", "");
                        map.put("postId", "");
                        map.put("isLocation", "0");
                        map.put("lat", "" + 0.0);
                        map.put("lon", "" + 0.0);
                        map.put("type", 4);
                        map.put("voiceRecrod", "" + downloadUrl);
                        database.getReference("chat").child(chatKey).child("messages").push().setValue(map);
                        sendNewMessage();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("failed", e.getMessage());
                        Toast.makeText(ChatActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private String getAddress(double latitude, double longitude) {
        StringBuilder result = new StringBuilder();
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                result.append(address.getThoroughfare()).append(" - ");
                result.append(address.getLocality()).append(" - ");
                result.append(address.getCountryName());
            }
        } catch (IOException e) {
            Log.e("tag", e.getMessage());
        }

        return result.toString();
    }

    private void updateLocation() {
        final LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.

                latitude = location.getLatitude();
                longitude = location.getLongitude();
                locationManager.removeUpdates(this);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
    }

    private void getIntentData() {
        model = new Gson().fromJson(getIntent().getStringExtra("postModel"), new TypeToken<ListModel>() {
        }.getType());
    }

    private void init() {
        if (chatKey == null) {
            chatKey = myId + "_" + customerId;
        }

        rvChat.setLayoutManager(new LinearLayoutManager(this));
        chatAdapter = new ChatAdapter(this, list);
        rvChat.setAdapter(chatAdapter);
        database.getReference().child("chat").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Object value = dataSnapshot.getValue();
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        String parent = childSnapshot.getKey();
                        listData.add(parent);
                    }

                    for (int i = 0; i < listData.size(); i++) {
                        if (listData.get(i).equals(myId + "_" + customerId) || listData.get(i).equals(customerId + "_" + myId)) {
                            String[] split1 = listData.get(i).split("_");
                            String id = prefrencesStorage.getId();
                            if (split1[1].equals(id)) {
                                chatKey = customerId + "_" + id;
                            } else {
                                chatKey = id + "_" + customerId;
                            }
                            Log.e("allSnapparent", listData.get(i));
                        }
                    }

                    getChatInfo();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        sendChatData();
        Log.e("key", "" + chatKey);
    }


    private void sendChatData() {
        run = new Runnable() {
            @Override
            public void run() {
                if (from.equals("1")) {
                    Log.e("chatKey", "" + chatKey);
                    getIntentData();
                    String image = getIntent().getExtras().getString("image");
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("date", ServerValue.TIMESTAMP);
                    map.put("from", String.valueOf(myId));
                    if (model != null) {
                        if (model.getInformation().equals("null")) {
                            map.put("message", "");
                        } else {
                            map.put("message", model.getInformation());
                        }

                    } else {
                        String postText = getIntent().getExtras().getString("postText");
                        map.put("message", postText);
                    }
                    if (model != null) {
                        map.put("to", String.valueOf(customerId));
                    } else {
                        map.put("to", String.valueOf(customerId));
                    }
                    if (model.getPostImages() != null) {
                        if (model.getPostImages().size() > 0) {
                            map.put("postImage", model.getPostImages().get(0).getImage());
                        }
                    } else {
                        map.put("postImage", model.getVideoUrl());
                    }

                    map.put("postId", model.getPostId());
                    map.put("isLocation", "0");
                    map.put("lat", "" + 0.0);
                    map.put("lon", "" + 0.0);
                    map.put("voiceRecrod", "");
                    map.put("type", 5);
                    database.getReference("chat").child(chatKey).child("messages").push().setValue(map);

                    sendNewMessage();
                }
            }
        };
        handler.postDelayed(run, 2000);
    }

    private void getChatInfo() {
        database.getReference("chat").child(chatKey).child("messages").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                list.add(dataSnapshot.getValue(ChatModel.class));
                chatAdapter.notifyDataSetChanged();

                if (list.size() != 0) {
                    rvChat.scrollToPosition(list.size() - 1);
                } else {

                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendNewMessage() {
//        Log.e("msg", "" + list.get(list.size() - 1).getMessage() + "" + list.get(list.size() - 1).getType());
        String url = "http://sahalaatq8.com/api/profile/" + myId + "/chat/" + customerId + "/last";
        AndroidNetworking.initialize(this);
        AndroidNetworking.setParserFactory(new JacksonParserFactory());
        ANRequest.PostRequestBuilder post = AndroidNetworking.post(url);
        if (list.size() > 0) {
            if (list.get(list.size() - 1).getType() == 2 || list.get(list.size() - 1).getType() == 3 || list.get(list.size() - 1).getType() == 4) {
                post.addBodyParameter("message", getString(R.string.sendAttachment));
            } else {
                post.addBodyParameter("message", list.get(list.size() - 1).getMessage());
            }
        } else {
            post.addBodyParameter("message", "Message");
        }
//        else {
//            if (list.get(list.size()).getType() == 2 || list.get(list.size()).getType() == 3 || list.get(list.size()).getType() == 4) {
//                post.addBodyParameter("message", getString(R.string.sendAttachment));
//            } else {
//                post.addBodyParameter("message", list.get(list.size() - 1).getMessage());
//            }
//        }

        post.build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("response", "" + response);
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e("error", "" + anError.getMessage());
                    }
                });
    }

    private void createNewNode() {

        database.getReference("chat").child(chatKey).child("messages").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                list.add(dataSnapshot.getValue(ChatModel.class));
                chatAdapter.notifyDataSetChanged();
                if (chatKey == null) {

                }
                if (list.size() != 0)
                    rvChat.scrollToPosition(list.size() - 1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void onClicks() {
        ivAttach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(ChatActivity.this)
                        .setSingleChoiceItems(R.array.attach_list, 0, null)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                                int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();

                                // Do something useful withe the position of the selected radio button
                                if (selectedPosition == 0) {
                                    openGallery(GALLERYCODE);
                                } else {
                                    browseDocuments();
                                }
                            }
                        })
                        .show();
            }
        });

        ivcame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, "New Picture");
                values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
                imageUri = getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, REQUEST_CAMERA);
            }
        });
        ivSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!edMessage.getText().toString().isEmpty()) {
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("date", ServerValue.TIMESTAMP);
                    map.put("from", String.valueOf(myId));
                    map.put("message", edMessage.getText().toString());
                    map.put("isLocation", "0");
                    map.put("lat", "" + 0.0);
                    map.put("lon", "" + 0.0);
                    map.put("to", String.valueOf(customerId));
                    map.put("postImage", "");
                    map.put("postId", "");
                    map.put("type", 1);
                    map.put("voiceRecrod", "");
                    database.getReference("chat").child(chatKey).child("messages").push().setValue(map);
                    edMessage.setText("");
                    sendNewMessage();
                }
            }
        });
    }

    private void onCaptureImageResult(Intent data) {
        try {
            thumbnail = MediaStore.Images.Media.getBitmap(
                    getContentResolver(), imageUri);
            imageurl = getRealPathFromURI(imageUri);
            imageFile = new File(imageurl);
            destination = new File(Environment.getExternalStorageDirectory(),
                    System.currentTimeMillis() + ".jpg");
            uploadImage(Uri.fromFile(imageFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    private void openGallery(int gallerycode) {
        Intent intent = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(
                Intent.createChooser(intent, "إختر صورة"),
                gallerycode);
    }

    private void browseDocuments() {

        String[] mimeTypes =
                {"application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .doc & .docx
                        "application/vnd.ms-powerpoint", "application/vnd.openxmlformats-officedocument.presentationml.presentation", // .ppt & .pptx
                        "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // .xls & .xlsx
                        "text/plain",
                        "application/pdf",
                        "application/zip"};

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent.setType(mimeTypes.length == 1 ? mimeTypes[0] : "*/*");
            if (mimeTypes.length > 0) {
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            }
        } else {
            String mimeTypesStr = "";
            for (String mimeType : mimeTypes) {
                mimeTypesStr += mimeType + "|";
            }
            intent.setType(mimeTypesStr.substring(0, mimeTypesStr.length() - 1));
        }
        startActivityForResult(Intent.createChooser(intent, "ChooseFile"), REQUEST_CODE_DOC);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == GALLERYCODE) {
                Uri selectedImageUri = data.getData();
                Uri imageUri = data.getData();
                uploadImage(imageUri);
            }
            if (requestCode == REQUEST_CODE_DOC) {
                Uri fileUri = data.getData();
                uploadFile(fileUri);
            }
            if (requestCode == REQUEST_CAMERA) {
                onCaptureImageResult(data);
            }

        }
    }

    private void uploadImage(Uri imageUri) {
        if (imageUri != null) {
            final ProgressDialog progressDialog2 = new ProgressDialog(this);
            progressDialog2.setTitle("تحميل ......");
            progressDialog2.setCancelable(false);
            progressDialog2.show();

            final StorageReference ref = storageReference.child(UUID.randomUUID().toString());
            ref.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog2.dismiss();

                            Toast.makeText(ChatActivity.this, "تم التحميل", Toast.LENGTH_SHORT).show();
                            HashMap<String, Object> updateMap = new HashMap<>();
                            pushingImageMessage(taskSnapshot.getDownloadUrl().toString());

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog2.dismiss();
                            Toast.makeText(ChatActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            progressDialog2.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });
        }
    }

    private void pushingImageMessage(String s) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("date", ServerValue.TIMESTAMP);
        map.put("from", String.valueOf(myId));
        map.put("message", s);
        map.put("isLocation", "0");
        map.put("to", String.valueOf(customerId));
        map.put("postImage", "");
        map.put("postId", "");
        map.put("type", 2);
        map.put("voiceRecrod", "");
        database.getReference("chat").child(chatKey).child("messages").push().setValue(map);
        sendNewMessage();
    }

    private void uploadFile(Uri imageUri) {
        if (imageUri != null) {
            final ProgressDialog progressDialog2 = new ProgressDialog(this);
            progressDialog2.setTitle("تحميل ......");
            progressDialog2.setCancelable(false);
            progressDialog2.show();

            final StorageReference ref = storageReference.child(UUID.randomUUID().toString());
            ref.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog2.dismiss();

                            Toast.makeText(ChatActivity.this, "تم التحميل", Toast.LENGTH_SHORT).show();
                            HashMap<String, Object> updateMap = new HashMap<>();
                            pushingPdfMessage(taskSnapshot.getDownloadUrl().toString());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog2.dismiss();
                            Toast.makeText(ChatActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            progressDialog2.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });
        }
    }

    private void pushingPdfMessage(String s) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("date", ServerValue.TIMESTAMP);
        map.put("from", String.valueOf(myId));
        map.put("message", s);
        map.put("isLocation", "0");
        map.put("to", String.valueOf(customerId));
        map.put("type", 3);
        map.put("postImage", "");
        map.put("postId", "");
        map.put("voiceRecrod", "");
        database.getReference("chat").child(chatKey).child("messages").push().setValue(map);
        sendNewMessage();
        edMessage.setText("");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (list.size() > 0) {
            if (list.get(list.size() - 1).getType() != 2 || list.get(list.size() - 1).getType() != 3) {
                sendNewMessage();
            }

        }

    }
}
