package com.imovil.recordapp;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;

import android.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;

public class DrawingFragment extends Fragment {
    Activity activity;

    private Test test;
    private DrawingArea drawingArea;
    private Button finishedButton;
    private TextView commentTextView;

    private static String fileName = null, outputFilename;

    public DrawingFragment() {
        // Required empty public constructor
    }

    public static DrawingFragment newInstance(String param1, String param2) {
        DrawingFragment fragment = new DrawingFragment();
        /*Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);*/
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //mParam1 = getArguments().getString(ARG_PARAM1);
            //mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_drawing, container, false);

        activity = getActivity();

        commentTextView = view.findViewById(R.id.commentTextView);
        drawingArea = (DrawingArea) view.findViewById(R.id.drawing_area2);
        drawingArea.initTrailDrawer();
        //drawingArea.setImageURI(Uri.fromFile(new File(getContext().getExternalCacheDir()+ File.separator+"fondo_app2.jpg")));

        if (getArguments() != null) {
            test = (Test) getArguments().getSerializable("test");
            if (test.getFilename() != null) {
                String fname = test.getFilename();
                    File file = new File(((ComunicaTest)activity).getFilePath(fname));
                    if (file.exists()) {
                        drawingArea.setImageURI(Uri.fromFile(file));
                    } else {
                        Thread thread = new Thread() {
                            @Override
                            public void run() {
                                try {
                                    while (true) {
                                        sleep(200);
                                        if (file.exists()) {
                                            activity.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    drawingArea.setImageURI(Uri.fromFile(file));
                                                }
                                            });
                                            break;
                                        }
                                    }
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        };

                        thread.start();
                    }
                }

            String comment;
            if ((comment = test.getComment()) != null)
                commentTextView.setText(comment);
        }

        outputFilename = test.getName() + "_draw.jpeg";
        fileName = ((ComunicaTest) activity).getFilePath(outputFilename);
        //.getExternalCacheDir().getAbsolutePath();

        finishedButton = view.findViewById(R.id.finishedButton);
        finishedButton.setOnClickListener(v -> {
            Bitmap image = screenShot(drawingArea);
            FileOutputStream fileOutputStream;
            try {
                fileOutputStream = new FileOutputStream(new File(fileName));
                image.compress(Bitmap.CompressFormat.JPEG, 90, fileOutputStream);
                fileOutputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            ((ComunicaTest) activity).uploadFile(fileName, "image/*");
            test.setOutputFilename(outputFilename);
            ((ComunicaTest) activity).nextTest();
        });

        return view;
    }

    public Bitmap screenShot(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(),
                view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }
}