package com.example.parnaie;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Point;
import android.media.Image;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
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
    private int powers = 3;

    // Initialize
    private Handler handler = new Handler();
    private MediaPlayer sound;
    private ImageView player;


    // Status Check
    private boolean pause = true;
    private boolean game = true;
    private boolean immune = false;
    private boolean doubler = false;
    private boolean magnet = false;
    private boolean tigara = false;
    private boolean cotnari = false;
    boolean music = true;
    private String[] names = new String[]{"Ave", "Cipa", "Serban", "Luca", "Radu", "Paul", "Mada"};
    private String name;

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
        scoreLabel = (TextView) view.findViewById(R.id.score);

        // Get screen size.

        Display display = Objects.requireNonNull(getActivity()).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        screenWidth = size.x;
        screenHeight = size.y;
        player = new ImageView(getContext());
        player.setTag("player");
        player.setX(screenWidth / 2 - 100);
        player.setY(screenHeight - 440);
        player.setLayoutParams(new android.view.ViewGroup.LayoutParams(300, 300));
        player.requestLayout();
        FrameLayout rootLayout = (FrameLayout) view.findViewById(R.id.frame);
        rootLayout.addView(player);

        speed = screenHeight / 200;
        rate = 400;
        scoreLabel.setText("0");

        //MOVE
        view.findViewById(R.id.frame).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
                    Objects.requireNonNull(getView()).findViewWithTag("player").setX(motionEvent.getX() - 150);
                }
                return true;
            }
        });

        final Runnable runnableCode = new Runnable() {
            @Override
            public void run() {
                if (!game) {
                    sound = MediaPlayer.create(getActivity(), R.raw.gameover);
                    sound.start();
                    handler.removeMessages(0);
                    new AlertDialog.Builder(getContext())
                            .setTitle("Game over!")
                            .setMessage("Score: " + score)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    NavHostFragment.findNavController(SecondFragment.this)
                                            .navigate(R.id.action_SecondFragment_to_FirstFragment);
                                }
                            })
                            .setCancelable(false)
                            .show();
                } else {
                    if (!pause && score == 0)
                        generatePet();
                    if (tigara) {
                        generateTigara();
                    }

                    if(score < 50){
                        speed = screenHeight / 200;
                    }
                    if (score < 100) {
                        speed = screenHeight / 150;
                    } else if (score < 200) {
                        speed = screenHeight / 140;
                    } else if (score < 300) {
                        speed = screenHeight / 120;
                    } else if (score < 400) {
                        speed = screenHeight / 100;
                    } else if (score < 500) {
                        speed = screenHeight / 80;
                    } else if (score > 500) {
                        speed = screenHeight / 60;
                    }
                    handler.postDelayed(this, rate);
                }

            }
        };
        selectFighter();
        handler.post(runnableCode);

        //POWER BUTTON
        view.findViewById(R.id.button_power).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (powers > 0) {
                    powers--;
                    Toast toast = Toast.makeText(getActivity(),
                            "Powers left:" + powers,
                            Toast.LENGTH_SHORT);
                    toast.show();
                    usePower();
                }
            }
        });

        //PAUSE
        view.findViewById(R.id.buttonPause).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pause = true;

                sound = MediaPlayer.create(getActivity(), R.raw.open);
                sound.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mp.reset();
                        mp.release();
                        mp = null;
                    }
                });
                sound.start();
                selectFighter();
            }
        });
    }

    public void generatePet() {
        View view = Objects.requireNonNull(getView()).findViewById(R.id.frame);
        final ImageView[] image = {new ImageView(getContext())};
        image[0].setImageResource(R.drawable.black);
        image[0].setLayoutParams(new android.view.ViewGroup.LayoutParams(300, 300));
        FrameLayout rootLayout = (FrameLayout) view.findViewById(R.id.frame);
        rootLayout.addView(image[0]);

        initPos(image);

        Runnable runnableCode = new Runnable() {
            @Override
            public void run() {
                if (!pause) {
                    if (hitCheck(image[0]) == 1)
                        initPos(image);

                    if (cotnari) {
                        image[0].setImageResource(R.drawable.cotnari);
                        rate = 800;
                    }

                    changePos(image[0]);

                }
                handler.postDelayed(this, 2);
            }
        };
        handler.post(runnableCode);
    }

    public void generateTigara() {
        View view = Objects.requireNonNull(getView()).findViewById(R.id.frame);
        final ImageView[] image = {new ImageView(getContext())};

        image[0].setImageResource(R.drawable.tigara);
        image[0].setLayoutParams(new android.view.ViewGroup.LayoutParams(300, 300));
        FrameLayout rootLayout = (FrameLayout) view.findViewById(R.id.frame);
        rootLayout.addView(image[0]);


        initPos(image);

        Runnable runnableCode = new Runnable() {
            @Override
            public void run() {
                if (!pause) {
                    if (hitCheck(image[0]) == 1) {
                        score += 5;
                        initPos(image);
                    }
                }
                changePos(image[0]);
                handler.postDelayed(this, 2);
            }
        };
        handler.post(runnableCode);
    }

    public void initPos(ImageView[] image) {
        int randomNum = ThreadLocalRandom.current().nextInt(-550, 350);
        image[0].setX(screenWidth / 2 + randomNum);
        image[0].setY(0);
    }

    public void changePos(ImageView image) {
        if (magnet) {
            if (player.getX() > image.getX())
                image.setX(image.getX() + 2);
            else if (player.getX() < image.getX())
                image.setX(image.getX() - 2);
        }
        image.setY(image.getY() + speed);
    }

    @SuppressLint("SetTextI18n")
    public int hitCheck(ImageView image) {
        ImageView player = Objects.requireNonNull(getView()).findViewWithTag("player");

        //IF PLAYER GOT IT
        if (image.getX() <= player.getX() + 140 && image.getX() >= player.getX() - 140 &&
                image.getY() <= player.getY() + 255 && image.getY() >= player.getY() - 255
        ) {
            if (cotnari)
                game = false;
            if (!doubler)
                score++;
            else if (doubler)
                score += 5;
            scoreLabel.setText(Integer.toString(score));
            return 1;

        }

        //IF IT REACHED BOTTOM
        if (image.getY() >= screenHeight - 400) {
            if (!immune && !cotnari)
                game = false;
            if (cotnari) {
                score++;
                scoreLabel.setText(Integer.toString(score));
            }
            if (immune || cotnari)
                return 1;
        }
        return -1;
    }

    public void selectFighter() {
        AlertDialog.Builder options = new AlertDialog.Builder(getContext());
        options.setTitle("Select your fighter:");
        options.setItems(names, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i) {
                    case 0:
                        name = "Ave";
                        player.setImageResource(R.drawable.ave);
                        sound = MediaPlayer.create(getActivity(), R.raw.ave_sound);
                        sound.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                mp.reset();
                                mp.release();
                                mp = null;
                            }
                        });
                        sound.start();
                        pause = false;
                        break;
                    case 1:
                        name = "Cipa";
                        player.setImageResource(R.drawable.cipa);
                        sound = MediaPlayer.create(getActivity(), R.raw.cipa_sound);
                        sound.start();
                        pause = false;
                        break;
                    case 2:
                        name = "Serban";
                        player.setImageResource(R.drawable.serban);
                        sound = MediaPlayer.create(getActivity(), R.raw.serban_sound);

                        sound.start();
                        pause = false;
                        break;
                    case 3:
                        name = "Luca";
                        player.setImageResource(R.drawable.luca);
                        sound = MediaPlayer.create(getActivity(), R.raw.luca_sound);

                        sound.start();
                        pause = false;
                        break;
                    case 4:
                        name = "Radu";
                        player.setImageResource(R.drawable.radu);
                        sound = MediaPlayer.create(getActivity(), R.raw.radu_sound);

                        sound.start();
                        pause = false;
                        break;
                    case 5:
                        name = "Paul";
                        player.setImageResource(R.drawable.paul);
                        sound = MediaPlayer.create(getActivity(), R.raw.paul_sound);
                        sound.start();
                        powers = 1;
                        usePower();
                        pause = false;
                        break;
                    case 6:
                        name = "Mada";
                        player.setImageResource(R.drawable.mada);
                        sound = MediaPlayer.create(getActivity(), R.raw.mada_sound);
                        sound.start();
                        pause = false;
                        break;
                }
            }
        });
        options.setCancelable(false);
        options.show();
    }

    public void usePower() {
        Toast toast;
        int time = 0;

        sound = MediaPlayer.create(getActivity(), R.raw.power_up);
        sound.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.reset();
                mp.release();
                mp = null;
            }
        });
        sound.setVolume(0.2f, 0.2f);
        sound.start();

        switch (name) {
            case "Ave":
                toast = Toast.makeText(getActivity(),
                        "Power: Be Everywhere",
                        Toast.LENGTH_SHORT);
                toast.show();

                Objects.requireNonNull(getView()).findViewById(R.id.frame).setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
                            Objects.requireNonNull(getView()).findViewWithTag("player").setX(motionEvent.getX() - 150);
                            Objects.requireNonNull(getView()).findViewWithTag("player").setY(motionEvent.getY() - 150);
                        }
                        return true;
                    }
                });
                time = 10000;

                break;
            case "Cipa":
                toast = Toast.makeText(getActivity(),
                        "Power: Tigara",
                        Toast.LENGTH_SHORT);
                toast.show();
                time = 300;
                tigara = true;
                break;
            case "Serban":
                toast = Toast.makeText(getActivity(),
                        "Power: Pet Magnet",
                        Toast.LENGTH_SHORT);
                toast.show();

                magnet = true;

                time = 10000;
                break;
            case "Luca":
                toast = Toast.makeText(getActivity(),
                        "Power: Invincibility",
                        Toast.LENGTH_SHORT);
                toast.show();
                immune = true;

                getView().findViewById(R.id.frame).setBackgroundResource(R.drawable.apa);
                time = 10000;

                break;
            case "Radu":
                toast = Toast.makeText(getActivity(),
                        "Power: Slow motion",
                        Toast.LENGTH_SHORT);
                toast.show();
                speed = screenHeight / 200;
                time = 30000;
                break;
            case "Paul":
                toast = Toast.makeText(getActivity(),
                        "Power: Escape 2xl",
                        Toast.LENGTH_SHORT);
                toast.show();
                cotnari = true;
                break;
            case "Mada":
                toast = Toast.makeText(getActivity(),
                        "Power: x5 score",
                        Toast.LENGTH_SHORT);
                toast.show();
                doubler = true;
                time = 10000;
                break;
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (game)
                    reset();
            }
        }, time);

    }

    public void reset() {
        //reset background
        getView().findViewById(R.id.frame).setBackgroundResource(R.drawable.empty);

        //reset sound
        sound = MediaPlayer.create(getActivity(), R.raw.power_off);
        sound.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.reset();
                mp.release();
                mp = null;
            }
        });
        sound.start();

        //reset Y
        Objects.requireNonNull(getView()).findViewWithTag("player").setY(screenHeight - 430);

        //reset move listener
        Objects.requireNonNull(getView()).findViewById(R.id.frame).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
                    Objects.requireNonNull(getView()).findViewWithTag("player").setX(motionEvent.getX() - 150);
                }
                return true;
            }
        });

        //reset immune
        immune = false;

        //reset doubler
        doubler = false;

        //reset speed
        if (score < 50) {
            speed = screenHeight / 200;
        } else if (score < 100) {
            speed = screenHeight / 150;
        } else if (score < 200) {
            speed = screenHeight / 140;
        } else if (score < 300) {
            speed = screenHeight / 120;
        } else if (score < 400) {
            speed = screenHeight / 100;
        } else if (score < 500) {
            speed = screenHeight / 80;
        } else if (score > 500) {
            speed = screenHeight / 60;
        }

        //reset magnet
        magnet = false;

        //reset tigara
        tigara = false;

        //reset cotnari
        //cotnari = false;
    }

}