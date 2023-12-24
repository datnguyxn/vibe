package com.vibe.vibe.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.content.FileProvider;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.makeramen.roundedimageview.BuildConfig;
import com.makeramen.roundedimageview.RoundedImageView;
import com.vibe.vibe.R;
import java.io.File;
import com.vibe.vibe.entities.Album;
import com.vibe.vibe.entities.Song;

import java.io.FileOutputStream;
import java.text.MessageFormat;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GenerateQRCodeBottomSheetFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GenerateQRCodeBottomSheetFragment extends BottomSheetDialogFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG ="GenerateQRCodeBottomSheetFragment" ;
    private TextView qrTitle, qrDescription, qrDescriptionImage;
    private RoundedImageView qrCodeImage;
    private Bitmap qrBitmap;
    private MaterialButton btnShare;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Song song;
    private Album album;
    private boolean isSong = false;
    private boolean isAlbum = false;
    private boolean isPlaylist = false;


    public GenerateQRCodeBottomSheetFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GenerateQRCodeBottomSheetFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GenerateQRCodeBottomSheetFragment newInstance(String param1, String param2) {
        GenerateQRCodeBottomSheetFragment fragment = new GenerateQRCodeBottomSheetFragment();
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
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            song = (Song) bundle.getSerializable("song");
            album = (Album) bundle.getSerializable("album");
            if (bundle.getString("isSong").equals("isSong")) {
                isSong = true;
            } else if (bundle.getString("isAlbum").equals("isAlbum")) {
                isAlbum = true;
            } else if (bundle.getString("isPlaylist").equals("isPlaylist")) {
                isPlaylist = true;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_generate_qr_code_bottom_sheet, container, false);
        init(view);
        if (isSong) {
            qrTitle.setText("Song's QR Code");
            qrDescription.setText("Let share your song to your friends");
            generateQrCode(song.getName());
        } else if (isAlbum) {
            qrTitle.setText("Album's QR Code");
            qrDescription.setText("Let share your album to your friends");
            generateQrCode(album.getName());
        } else if (isPlaylist) {
            qrTitle.setText("Playlist's QR Code");
            qrDescription.setText("Let share your playlist to your friends");
            generateQrCode(album.getName());
        }
        handleClick();
        return view;
    }

    private void handleClick() {
        btnShare.setOnClickListener(v -> {
            Log.e(TAG, "handleClick: share button clicked");
            if (Environment.isExternalStorageManager()) {
                if (qrBitmap != null) {
                    Bitmap qrBitmapString = generateNameToBitMap();
                    String imageSrc = saveImageQrToCache(qrBitmapString);
                    if (imageSrc != null) {
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("image/*");
                        Uri imageUri = FileProvider.getUriForFile(getActivity(), getActivity().getPackageName() + ".provider", new File(imageSrc));
                        intent.putExtra(Intent.EXTRA_STREAM, imageUri);
                        intent.putExtra(Intent.EXTRA_TITLE, "QR Code");
                        if (isSong) {
                            intent.putExtra(Intent.EXTRA_TEXT, HtmlCompat.fromHtml(MessageFormat.format("Scan this QR Code to join <b>{0}</b> song", song.getName()), HtmlCompat.FROM_HTML_MODE_LEGACY));
                        } else if (isAlbum) {
                            intent.putExtra(Intent.EXTRA_TEXT, HtmlCompat.fromHtml(MessageFormat.format("Scan this QR Code to join <b>{0}</b> album", album.getName()), HtmlCompat.FROM_HTML_MODE_LEGACY));
                        } else if (isPlaylist) {
                            intent.putExtra(Intent.EXTRA_TEXT, HtmlCompat.fromHtml(MessageFormat.format("Scan this QR Code to join <b>{0}</b> playlist", album.getName()), HtmlCompat.FROM_HTML_MODE_LEGACY));
                        }
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                        startActivity(Intent.createChooser(intent, "Share QR Code"));

                    }
                }
            } else {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                Uri uri = Uri.fromParts("package", getContext().getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        });
    }
    private String saveImageQrToCache(Bitmap bitmap) {
        String src = null;
        try {
            File file = new File(getContext().getCacheDir(), "images");
//            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "images");
            boolean isFileCreated = true;
            if (!file.exists()) {
                isFileCreated = file.mkdirs();
            }
            if (isFileCreated) {
                File imageFile = null;
                if (isSong) {
                    imageFile = new File(file, song.getName() + ".png");
                } else if (isAlbum) {
                    imageFile = new File(file, album.getName() + ".png");
                } else if (isPlaylist) {
                    imageFile = new File(file, album.getName() + ".png");
                }
                FileOutputStream outputStream = new FileOutputStream(imageFile);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                outputStream.flush();
                outputStream.close();
                src = imageFile.getAbsolutePath();
            }
        } catch (Exception e) {
            Log.e(TAG, "saveImageQrToCache: ", e);
        }
        return src;
    }
    private Bitmap generateNameToBitMap() {
        Bitmap bitmap = Bitmap.createBitmap(qrBitmap.getWidth(), qrBitmap.getHeight(), qrBitmap.getConfig());

//        draw album name  on bitmap and it will have bottom position in bitmap
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(qrBitmap, 0, 0, null);
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(20);
        paint.setTextAlign(Paint.Align.CENTER);
        if (isSong) {
            canvas.drawText(song.getName(), canvas.getWidth() / 2, canvas.getHeight() - 20, paint);
        } else if (isAlbum) {
            canvas.drawText(album.getName(), canvas.getWidth() / 2, canvas.getHeight() - 20, paint);
        } else if (isPlaylist) {
            canvas.drawText(album.getName(), canvas.getWidth() / 2, canvas.getHeight() - 20, paint);
        }

        return bitmap;
    }


    private void init(View view) {
        qrTitle = view.findViewById(R.id.QrTitle);
        qrDescription = view.findViewById(R.id.qrDescription);
        qrDescriptionImage = view.findViewById(R.id.qrDescriptionImage);
        qrCodeImage = view.findViewById(R.id.qrCodeImage);
        btnShare = view.findViewById(R.id.btnShare);
    }

    private void generateQrCode(String name) {
        Log.e(TAG, "generate: init qr name: " + name );
        int width = 500;
        int height = 500;

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = qrCodeWriter.encode(name, com.google.zxing.BarcodeFormat.QR_CODE, width, height);
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            qrCodeImage.setImageBitmap(bitmap);
            qrBitmap = bitmap;
            Object[] params = new Object[]{name};
            String template = getResources().getString(R.string.descrption);
            String description = MessageFormat.format(template, params);
            String descriptionFormat = description.replaceAll(name, "<font color='#4991FD'>" + name + "</font>");
            qrDescriptionImage.setText(HtmlCompat.fromHtml(descriptionFormat, HtmlCompat.FROM_HTML_MODE_LEGACY));

        } catch (WriterException e) {
            throw new RuntimeException(e);
        }
    }
}