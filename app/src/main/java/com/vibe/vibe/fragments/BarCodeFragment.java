package com.vibe.vibe.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.vibe.vibe.R;
import com.vibe.vibe.constants.Application;
import com.vibe.vibe.entities.Album;
import com.vibe.vibe.entities.Playlist;
import com.vibe.vibe.models.AlbumModel;
import com.vibe.vibe.models.PlaylistModel;
import com.vibe.vibe.models.SongModel;
import com.vibe.vibe.models.UserModel;

import java.io.InputStream;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BarCodeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BarCodeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG ="BarCodeFragment" ;
    public static final int REQUEST_CAMERA_PERMISSION = 123;
    private static final int REQUEST_GET_FROM_GALLERY = 321;
    private SurfaceView cameraReview;
    private MaterialButton btnFromGallery, btnScanQRCode;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private boolean isDetected = false;
    private final UserModel userModel = new UserModel();
    private final AlbumModel albumModel = new AlbumModel();
    private final SongModel songModel = new SongModel();
    private final PlaylistModel playlistModel = new PlaylistModel();
    private String uid;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public BarCodeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BarCodeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BarCodeFragment newInstance(String param1, String param2) {
        BarCodeFragment fragment = new BarCodeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bar_code, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
        if (getActivity() != null) {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new
                        String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                return;
            }
        }
        initForSurfaceView();
        handleScanQRCode();
    }

    private void handleScanQRCode() {
        btnScanQRCode.setOnClickListener(v -> {
            barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
                @Override
                public void release() {

                }

                @Override
                public void receiveDetections(@NonNull Detector.Detections<Barcode> detections) {
                    SparseArray<Barcode> barcodes = detections.getDetectedItems();
                    if (barcodes.size() > 0) {
                        String qrCodeContents = barcodes.valueAt(0).displayValue;
                        Log.e(TAG, "receiveDetections: value scan: " + qrCodeContents);
                        if (!isDetected) {
                            isDetected = true;
                            findAndGoToResult(qrCodeContents);
                        } else {
                            isDetected = false;
                        }
                    }
                }
            });
        });
        btnFromGallery.setOnClickListener(v -> {
//            if (Environment.isExternalStorageManager()) {
//                Intent intent = new Intent();
//                intent.setType("image/*");
//
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                    intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
//                } else {
//                    intent.setAction(Intent.ACTION_GET_CONTENT);
//                }
//
//                startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_GET_FROM_GALLERY);
//            } else {
//                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
//                Uri uri = Uri.fromParts("package", getContext().getPackageName(), null);
//                intent.setData(uri);
//                startActivity(intent);
//            }
            Intent intent = new Intent();
                intent.setType("image/*");

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                } else {
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                }

                startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_GET_FROM_GALLERY);
        });
    }

    private void initForSurfaceView() {
        barcodeDetector = new BarcodeDetector.Builder(getContext())
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();

        if (!barcodeDetector.isOperational()) {
            btnScanQRCode.setEnabled(false);
        }
        cameraSource = new CameraSource.Builder(getContext(), barcodeDetector)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedFps(24)
                .setAutoFocusEnabled(true)
                .build();
        cameraReview.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
                try {
                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(getActivity(), new
                                String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                        return;
                    }
                    cameraSource.start(cameraReview.getHolder());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {
            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {
                cameraSource.stop();
            }
        });
    }

    private void init(View view) {
        cameraReview = view.findViewById(R.id.cameraReview);
        btnFromGallery = view.findViewById(R.id.btnFromGallery);
        btnScanQRCode = view.findViewById(R.id.btnScanQRCode);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Application.SHARED_PREFERENCES_USER, getContext().MODE_PRIVATE);
        uid = sharedPreferences.getString(Application.SHARED_PREFERENCES_UUID, "");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.e(TAG, "onRequestPermissionsResult: init here");
                btnScanQRCode.setEnabled(true);
                initForSurfaceView();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_GET_FROM_GALLERY && resultCode == getActivity().RESULT_OK) {
            if (data == null || data.getData() == null) {
                return;
            }
            Uri selectedImage = data.getData();
            Log.e(TAG, "onActivityResult: " + selectedImage.toString());
            try {
                InputStream inputStream = getActivity().getContentResolver().openInputStream(selectedImage);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                if (bitmap == null) {
                    Snackbar.make(getView(), "Invalid QR Code", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                int width = bitmap.getWidth();
                int height = bitmap.getHeight();
                int newWidth = 500;
                int newHeight = 500;

                int[] pixels = new int[width * height];
                bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

                bitmap.recycle();

                bitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);
                RGBLuminanceSource source = new RGBLuminanceSource(newWidth, newHeight, pixels);
                BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));
                QRCodeReader reader = new QRCodeReader();

                Result result = reader.decode(binaryBitmap);
                String qrCodeContents = result.getText();
                findAndGoToResult(qrCodeContents);
            } catch (Exception e) {

            }
        }
    }

    private void findAndGoToResult(String qrCodeContents) {
        albumModel.getAlbum(qrCodeContents, new AlbumModel.onGetAlbumListener() {
            @Override
            public void onAlbumFound(Album album) {
                PlaylistFragment playlistFragment = new PlaylistFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("album", album);
                bundle.putString("SFP", "");
                bundle.putString("playlist", "");
                playlistFragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, playlistFragment).addToBackStack(null).commit();
                barcodeDetector.release();
                cameraSource.stop();
            }

            @Override
            public void onAlbumNotExist() {
                playlistModel.getPrivatePlaylist(uid, new PlaylistModel.OnGetPlaylistListener() {
                    @Override
                    public void onGetPlaylist(ArrayList<Playlist> playlists) {
                        for (Playlist playlist: playlists) {
                            if (playlist.getId().equals(qrCodeContents)) {
                                playlistModel.getPlaylistOfUser(uid, playlist.getId(), new PlaylistModel.onGetAllPlaylistOfUserListener() {
                                    @Override
                                    public void onGetAllPlaylistSuccess(Album album) {
                                        PlaylistFragment playlistFragment = new PlaylistFragment();
                                        Bundle bundle = new Bundle();
                                        bundle.putSerializable("album", album);
                                        bundle.putString("playlist", "This is a playlist");
                                        bundle.putString("SFP", album.getId());
                                        playlistFragment.setArguments(bundle);
                                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, playlistFragment).addToBackStack(null).commit();
                                        barcodeDetector.release();
                                        cameraSource.stop();
                                    }

                                    @Override
                                    public void onGetAllPlaylistFailed() {
                                        Log.e(TAG, "onGetAllPlaylistFailed: " + qrCodeContents);
                                        Snackbar.make(getView(), "Invalid QR Code", Snackbar.LENGTH_SHORT).show();
                                        barcodeDetector.release();
                                        cameraSource.stop();
                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onGetPlaylistFailed() {
                        Log.e(TAG, "onGetAllPlaylistFailed: " + qrCodeContents);
                        Snackbar.make(getView(), "Invalid QR Code", Snackbar.LENGTH_SHORT).show();
                        barcodeDetector.release();
                        cameraSource.stop();
                    }
                });
            }
        });
    }
}