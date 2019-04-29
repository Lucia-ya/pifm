package com.luciaya.pifm;

import saschpe.exoplayer2.ext.icy.IcyHttpDataSource;

public class MainPresenterImpl implements MainContract.Presenter {
    private MainContract.View mView;

    public MainPresenterImpl(MainContract.View view) {
        mView = view;
    }


    @Override
    public void onDestroy() {
        mView = null;
    }

    @Override
    public void onPauseBtnClicked() {
        mView.pause();
    }

    @Override
    public void onPlayBtnClicked() {
        mView.play();
    }

    @Override
    public void onChangeNameCalled(IcyHttpDataSource.IcyMetadata icyMetadata) {
        if (mView != null) {
            String s = String.valueOf(icyMetadata.getStreamTitle());
            int i = s.indexOf(" - ");
            if (i != -1) {
                mView.changeName(s.substring(0, i), s.substring(i + 3));
            }
        }
    }
}
