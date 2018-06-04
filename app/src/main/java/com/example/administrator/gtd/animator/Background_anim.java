package com.example.administrator.gtd.animator;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.administrator.gtd.R;

public class Background_anim extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.backgroud,container,false);
        View background=view.findViewById(R.id.background);
        ObjectAnimator valueAnimator_background = ObjectAnimator.ofFloat(background,"alpha",1,0);
        ObjectAnimator valueAnimator_background2 = ObjectAnimator.ofFloat(background,"alpha",1,1);
        valueAnimator_background.setDuration(800);
        valueAnimator_background2.setDuration(1000);

        AnimatorSet animSet1 = new AnimatorSet();
        animSet1.play(valueAnimator_background2).before(valueAnimator_background);
        animSet1.start();
        return view;
    }
}
