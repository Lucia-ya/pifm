package com.luciaya.pifm;

import saschpe.exoplayer2.ext.icy.IcyHttpDataSource;

public interface MainContract {
    public interface Presenter {
        void onDestroy();

        void onPauseBtnClicked();

        void onPlayBtnClicked();

        void onChangeNameCalled(IcyHttpDataSource.IcyMetadata icyMetadata);
    }

    public interface View {
        void play();

        void pause();

        void changeName(String artist, String music);
    }

}
