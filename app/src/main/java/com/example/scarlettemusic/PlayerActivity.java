package com.example.scarlettemusic;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity {

    Bundle songExtraData;
    ImageView prev,play, next;
    int position;
    SeekBar mSeekBarTime;
    static MediaPlayer mMediaPlayer;
    TextView songName;
    ArrayList musicList;
    private Object MainActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);



        prev = findViewById(R.id.previous);
        play = findViewById(R.id.play);
        next = findViewById(R.id.next);
        mSeekBarTime = findViewById(R.id.mSeekBarTime);
        songName  = findViewById(R.id.songName);



        if (mMediaPlayer!=null) {
            mMediaPlayer.stop();
        }


        Intent intent = getIntent();
        songExtraData = intent.getExtras();

        musicList = (ArrayList)songExtraData.getParcelableArrayList("songsList");
        position = songExtraData.getInt("position", 0);

        // etkinlik başladığında medya oynatıcıyı başlatan yeni bir yöntem oluşturma

        initializeMusicPlayer(position);

        // oynatma düğmesi

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (position < musicList.size() -1) {
                    position++;
                } else {
                    position = 0;
                }
                initializeMusicPlayer(position);
            }
        });

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position<=0) {
                    position = musicList.size();
                } else {
                    position++;
                }

                initializeMusicPlayer(position);
            }
        });


    }

    private void initializeMusicPlayer(final int position) {
 if (mMediaPlayer!=null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.reset();
        }

        // şarkı adını çıkarmak
        String name = musicList.get(position).toString();
        songName.setText(name);

        // depodaki şarkılara erişim

        Uri uri = Uri.parse(musicList.get(position).toString());
        mMediaPlayer = MediaPlayer.create(this, uri);
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {

                // seekbar
                mSeekBarTime.setMax(mMediaPlayer.getDuration());

                // mediaplayer oynatılırken oynat düğmesi duraklatmayı göstermelidir
                play.setImageResource(R.drawable.pause);
                // medya oynatıcıyı başlatr
                mMediaPlayer.start();
            }
        });

        // tamamlama dinleyicisini ayarlama
        // şarkı bittikten sonra olması gereken // şimdilik sadece duraklatma düğmesini oynatacak şekilde ayarlayacağız

        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                play.setImageResource(R.drawable.play);
            }
        });


        // medya oynatıcının bir şarkıyı çalmayı bitirdikten sonra bir sonraki şarkıya geçmesi için
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                play.setImageResource(R.drawable.play);

                int currentPosition = position;
                if (currentPosition < musicList.size() -1) {
                    currentPosition++;
                } else {
                    currentPosition = 0;
                }
                initializeMusicPlayer(currentPosition);

            }
        });


        // arama çubuğu üzerinde çalışıyor

        mSeekBarTime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                // KULLANICI SEEEKBAR İLE DOKUNUR VE MESAJ OLURSA
                if (fromUser) {
                    mSeekBarTime.setProgress(progress);
                    mMediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        // buraya kadar arama çubuğu kendi kendine değişmeyecek

        new Thread(new Runnable() {
            @Override
            public void run() {
                while(mMediaPlayer!=null) {
                    try {
                        if (mMediaPlayer.isPlaying()) {
                            Message message = new Message();
                            message.what = mMediaPlayer.getCurrentPosition();
                            handler.sendMessage(message);
                            Thread.sleep(1000);

                        }

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            mSeekBarTime.setProgress(msg.what);
        }
    };

    // son olarak play için bir metod yarat

            private void play() {
                // mediaplayer boş değilse ve oynatılıyorsa ve oynat düğmesine basılırsa duraklat

                if (mMediaPlayer!=null && mMediaPlayer.isPlaying()) {
                    mMediaPlayer.pause();
                    // duraklattığımızda oynatmak için oynat duraklat düğmesinin görüntüsünü değiştirin
                    play.setImageResource(R.drawable.play);
                } else {
                    mMediaPlayer.start();
                    // mediaplayer oynuyorsa // oynat düğmesinin görüntüsü duraklatmayı göstermelidir
                    play.setImageResource(R.drawable.pause);

                }
            }


}
