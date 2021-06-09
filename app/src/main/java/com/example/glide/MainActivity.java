package com.example.glide;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    Button btnLoadImage, btnClearImageview, btnLoadImageAsBitmap;
    ImageView imageView;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLoadImage = findViewById(R.id.btnLoadImage);
        btnClearImageview = findViewById(R.id.btnClearImageview);
        btnLoadImageAsBitmap = findViewById(R.id.btnLoadImageAsBitmap);
        imageView = findViewById(R.id.imageView);
        progressBar = findViewById(R.id.progressBar);

        btnLoadImage.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);

                Glide.with(MainActivity.this)
                        .load("https://images.unsplash.com/photo-1607252650355-f7fd0460ccdb?ixlib" +
                                "=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto" +
                                "=format&fit=crop&w=750&q=80")
                        .placeholder(R.drawable.ic_baseline_account_circle)
                        .error(R.drawable.ic_baseline_error)
                        .fitCenter()
                        .centerCrop()
                        .override(500, Target.SIZE_ORIGINAL)
                        .transform(new CircleCrop())
                        .listener(new RequestListener<Drawable>() {
                            @Override public boolean onLoadFailed(@Nullable GlideException e,
                                                                  Object model,
                                                                  Target<Drawable> target,
                                                                  boolean isFirstResource) {
                                Log.d("TAG", "Error loading image");
                                progressBar.setVisibility(View.GONE);
                                return false;
                            }

                            @Override public boolean onResourceReady(Drawable resource,
                                                                     Object model,
                                                                     Target<Drawable> target,
                                                                     DataSource dataSource,
                                                                     boolean isFirstResource) {
                                progressBar.setVisibility(View.GONE);
                                return false;
                            }
                        })
                        .into(imageView);

            }
        });

        btnClearImageview.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                Glide.with(MainActivity.this).clear(imageView);
            }
        });

        btnLoadImageAsBitmap.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                FutureTarget<Bitmap> target = Glide.with(MainActivity.this)
                        .asBitmap()
                        .load("https://images.unsplash.com/photo-1607252650355-f7fd0460ccdb?ixlib" +
                                "=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto" +
                                "=format&fit=crop&w=750&q=80")
                        .submit();

                    //background task
                    final Bitmap[] bitmap = {null};
                    ExecutorService executorService = Executors.newSingleThreadExecutor();
                    Handler handler = new Handler(Looper.getMainLooper());
                    executorService.execute(new Runnable() {
                        @Override public void run() {
                            try {
                                bitmap[0] = target.get();

                                handler.post(new Runnable() {
                                    @Override public void run() {
                                        imageView.setImageBitmap(bitmap[0]);
                                    }
                                });
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    });
            }
        });
    }
}