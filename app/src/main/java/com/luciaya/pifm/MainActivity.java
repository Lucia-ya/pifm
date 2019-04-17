package com.luciaya.pifm;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import saschpe.exoplayer2.ext.icy.IcyHttpDataSource;
import saschpe.exoplayer2.ext.icy.IcyHttpDataSourceFactory;

public class MainActivity extends AppCompatActivity implements MainContract.View {

    private ImageButton mImageButton;
    private TextView mArtist;
    private TextView mSongName;
    private static SimpleExoPlayer player_audio;
    private static final String TAG = "MainActivity";
    private static boolean play_audio = false;
    private MainContract.Presenter mPresenter;
    private ExtractorMediaSource audioSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mImageButton = (ImageButton) findViewById(R.id.imageButton);
        mArtist = (TextView) findViewById(R.id.artist_name);
        mArtist.setSelected(true);
        mSongName = (TextView) findViewById(R.id.song_name);
        mSongName.setSelected(true);
        mPresenter = new MainPresenterImpl(this);
        mImageButton.setEnabled(false);


        //библиотека для принятия данных с потока
        IcyHttpDataSourceFactory factory = new IcyHttpDataSourceFactory.Builder(Util.getUserAgent(this, getResources().getString(R.string.app_name)))
                .setIcyHeadersListener(null)
                .setIcyMetadataChangeListener(new IcyHttpDataSource.IcyMetadataListener() {
                    @Override
                    public void onIcyMetaData(IcyHttpDataSource.IcyMetadata icyMetadata) {
                        //режем строку принятую с потока на имя артиста и название трека и меняем текст в TextView
                        //будет запускаться всегда если меняется Title в потоке
                        String s = icyMetadata.getStreamTitle();
                        int i = s.indexOf(" - ");
                        if (i != -1) {
                            mArtist.setText(s.substring(0, i));
                            mSongName.setText(s.substring(i+3));
                        }
                    }
                }).build();

        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter(); //test
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector =
                new DefaultTrackSelector(videoTrackSelectionFactory);
        player_audio = ExoPlayerFactory.newSimpleInstance(this, trackSelector);

        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(getApplicationContext(), null, factory);

        audioSource = new ExtractorMediaSource
                (Uri.parse("http://cdn.pifm.ru/mp3"), dataSourceFactory, new DefaultExtractorsFactory(), new Handler(), null);
        player_audio.prepare(audioSource);

        //включаем кнопку через 2 секунды после запуска
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() { //таймер
                mImageButton.setEnabled(true);
            }
        }, 1500);





        mImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { //листенер кнопки пауза/плей
                if (!play_audio) {
                    mPresenter.onPlayBtnClicked(); //если пауза сейчас выключен включить и наоборот
                } else {
                    mPresenter.onPauseBtnClicked();
                }
            }
        });

        player_audio.addListener(new ExoPlayer.EventListener() { //листенер плеера


            @Override
            public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
                Log.v(TAG, "Listener-onTracksChanged... ");
            }

            @Override
            public void onLoadingChanged(boolean isLoading) {

            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {

            }

            @Override
            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
                Log.v(TAG, "Listener-onPlayerError...");
                player_audio.stop();
                player_audio.setPlayWhenReady(true);
            }

            @Override
            public void onPositionDiscontinuity(int reason) {

            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

            }

            @Override
            public void onSeekProcessed() {

            }
        });

    }

    @Override
    public void play() { //вызывается презентером
        play_audio = true;
        player_audio.prepare(audioSource); //поместить audioSource в плеер
        mImageButton.setImageResource(R.drawable.pause); //поменять изображение на кнопке
        player_audio.setPlayWhenReady(true); //запустить музыку
    }

    @Override
    public void pause() { //вызывается презентером
        play_audio = false;
        mImageButton.setImageResource(R.drawable.play); //поменять изображение на кнопке
        player_audio.setPlayWhenReady(false); //выключить воспроизведение
    }

    @Override
    public void onBackPressed() { //при  нажатии back оставновить воспроизведение музыки
        super.onBackPressed();
        player_audio.setPlayWhenReady(false);
    }
}
