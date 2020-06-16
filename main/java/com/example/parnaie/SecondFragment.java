package com.example.parnaie;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.loader.ResourcesProvider;
import android.graphics.Point;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.text.Layout;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.media.SoundPool;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.fragment.NavHostFragment;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;

public class SecondFragment extends Fragment {
    private TextView scoreLabel;
    private TextView startLabel;

    private int speed;
    private int rate;

    // Size
    private int screenWidth;
    private int screenHeight;

    // Score
    private int score = 0;

    // Initialize Class
    private Handler handler = new Handler();
    private SoundPlayer sound;


    // Status Check
    private boolean action_flg = false;
    private boolean start_flg = false;
    private boolean stop = false;
    private boolean game = true;
    boolean music = true;


    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_second, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sound = new SoundPlayer(getContext());
        scoreLabel = (TextView) view.findViewById(R.id.score);

        // Get screen size.

        Display display = Objects.requireNonNull(getActivity()).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        screenWidth = size.x;
        screenHeight = size.y;

        ImageView player = new ImageView(getContext());
        player.setTag("player");
        player.setImageResource(R.drawable.cipa);
        player.setX(screenWidth / 2 - 100);
        player.setY(screenHeight - 430);
        player.setLayoutParams(new android.view.ViewGroup.LayoutParams(300, 300));
        player.requestLayout();
        FrameLayout rootLayout = (FrameLayout) view.findViewById(R.id.frame);
        rootLayout.addView(player);

        speed = screenHeight / 140; // 768 / 45 = 17.06... => 17
        rate = 300;
        scoreLabel.setText("0");

        view.findViewById(R.id.frame).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_MOVE: {
                        Objects.requireNonNull(getView()).findViewWithTag("player").setX(motionEvent.getX() - 150);
                    }
                    break;
                }
                return true;
            }
        });
        Runnable runnableCode = new Runnable() {
            @Override
            public void run() {
                if (!game) {
                    sound.playOverSound();
                    new AlertDialog.Builder(getContext())
                            .setTitle("Game over!")
                            .setMessage("Score: " + score)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    handler.removeMessages(0);
                                    NavHostFragment.findNavController(SecondFragment.this)
                                            .navigate(R.id.action_SecondFragment_to_FirstFragment);
                                }
                            })
                            .show();
                }
                else {
                    generatePet();
                    if (rate > 250)
                        rate--;
                    if (score == 75)
                        speed = screenHeight / 100;
                    handler.postDelayed(this, rate);

                }
            }
        };
        handler.post(runnableCode);
        view.findViewById(R.id.button_power).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast toast = Toast.makeText(getActivity(),
                        "Power activated!",
                        Toast.LENGTH_SHORT);
                toast.show();

            }
        });

    }

    public void generatePet() {
        View view = Objects.requireNonNull(getView()).findViewById(R.id.frame);

        final ImageView[] image = {new ImageView(getContext())};
        image[0].setImageResource(R.drawable.black);

        int randomNum = ThreadLocalRandom.current().nextInt(-550, 350);
        image[0].setX(screenWidth / 2 + randomNum);
        image[0].setY(0);
        image[0].setLayoutParams(new android.view.ViewGroup.LayoutParams(300, 300));
        image[0].requestLayout();
        FrameLayout rootLayout = (FrameLayout) view.findViewById(R.id.frame);
        rootLayout.addView(image[0]);


        Runnable runnableCode  = new Runnable() {
            @Override
            public void run() {
                changePos(image[0]);
                if (hitCheck(image[0]) == 1) {
                    image[0].setImageResource(R.drawable.empty);
                    return;
                }
                handler.postDelayed(this, 10);
            }
        };
        handler.post(runnableCode);
    }

    public void changePos(ImageView image) {
        image.setY(image.getY() + speed);
    }

    @SuppressLint("SetTextI18n")
    public int hitCheck(ImageView image) {
        ImageView player = Objects.requireNonNull(getView()).findViewWithTag("player");

        //IF PLAYER GOT IT
        if (image.getX() <= player.getX() + 140 && image.getX() >= player.getX() - 140 &&
                image.getY() <= player.getY() + 270 && image.getY() >= player.getY() - 270
        ) {
            sound.playHitSound();
            score++;
            scoreLabel.setText(Integer.toString(score));
            stop = true;
            return 1;

        }

        //IF IT REACHED BOTTOM
        if (image.getY() >= screenHeight - 405) {

            // Stop game!
            game = false;
            // Show Result
            /*Intent intent = new Intent(Objects.requireNonNull(getActivity()).getApplicationContext(), com.example.parnaie.result.class);
            intent.putExtra("SCORE", score);

            startActivity(intent);*/


        }
        return -1;

    }


}