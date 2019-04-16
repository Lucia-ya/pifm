package com.luciaya.pifm;

public interface MainContract {
    public interface Presenter {
        void onDestroy();

        void onPauseBtnClicked();

        void onPlayBtnClicked();
    }

    public interface View {
        void play();

        void pause();
    }

}
