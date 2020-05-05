package com.westbrook.testdemo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ClipAnimationLayout {

    private ConcurrentLinkedQueue<ClipAnimationLayout.Data> dataQueue = new ConcurrentLinkedQueue<>();

    private List<ClipAnimationLayout.Data> dataList = new ArrayList<>();

    private Context context;
    private ViewGroup rootView;

    private int[] clipPositions = new int[3];

    private Random random = new Random();

    private ViewWrapper[] viewWrappers = new ViewWrapper[8];

    public ClipAnimationLayout(Context context, ViewGroup rootView) {
        this.context = context;
        this.rootView = rootView;
    }

    private void initRootView() {

        for (int i = 0; i < 8; i++) {

            View view = LayoutInflater.from(context).inflate(R.layout.clip_auto_random_item, null);

            viewWrappers[i] = new ViewWrapper(view);

            viewWrappers[i].getBackgroundView().setVisibility(View.GONE);

            viewWrappers[i].getForegroundView().setVisibility(View.VISIBLE);

            ((TextView) viewWrappers[i].getForegroundView().findViewById(R.id.text1)).setText(dataList.get(i).name);

            viewWrappers[i].setForegroundData(dataList.get(i));

            rootView.addView(view);

        }
    }

    public void setDataList(List<ClipAnimationLayout.Data> dataList) {

        this.dataList = dataList;

        for (int i = 8; i < dataList.size(); i++) {
            dataQueue.offer(dataList.get(i));
        }

        initRootView();

    }


    public static final class Data {

        public int id;

        public String name;

        public String imgSource;

        public String jumpAction;

    }


    public void startClip() {


        createRandomPositions();

        pushToQueue();

        stickyToTarget();

        startClipAnimation();

    }

    private void createRandomPositions() {
        clipPositions[0] = random.nextInt(8);
        while (true) {
            int randomInt = random.nextInt(8);
            if (randomInt != clipPositions[0]) {
                clipPositions[1] = randomInt;
                break;
            }
        }

        while (true) {
            int randomInt = random.nextInt(8);
            if (randomInt != clipPositions[0] && randomInt != clipPositions[1]) {
                clipPositions[2] = randomInt;
                break;
            }
        }
    }

    private void pushToQueue() {

        for (int i = 0; i < 3; i++) {

            ViewWrapper viewWrapper = viewWrappers[clipPositions[i]];
            dataQueue.offer(viewWrapper.getForegroundData());


//            FrameLayout FrameLayout = rootView.findViewById(clipPositions[i] + 3478);
//            if (FrameLayout.getChildAt(0).getVisibility() == View.VISIBLE) {
//                int id = FrameLayout.getChildAt(0).getId();
//                dataQueue.offer(dataList.get(id));
//                forceViews[i] = FrameLayout.getChildAt(0);
//                backViews[i] = FrameLayout.getChildAt(1);
//            } else {
//                int id = FrameLayout.getChildAt(1).getId();
//                dataQueue.offer(dataList.get(id));
//                forceViews[i] = FrameLayout.getChildAt(1);
//                backViews[i] = FrameLayout.getChildAt(0);
//            }
        }
    }

    private void stickyToTarget() {

        for (int i = 0; i < 3; i++) {

            ClipAnimationLayout.Data data = dataQueue.poll();

            ViewWrapper viewWrapper = viewWrappers[clipPositions[i]];

            viewWrapper.setForegroundData(data);

            View view = viewWrapper.getBackgroundView();

            ViewGroup viewGroup = (ViewGroup) view;

            TextView textView = (TextView) viewGroup.getChildAt(0);

            textView.setText(data.name);
        }
    }

    private void startClipAnimation() {

        for (int i = 0; i < 3; i++) {

            startAnimation(i);

        }


    }

    private void startAnimation(final int i) {

        final ViewWrapper viewWrapper = viewWrappers[clipPositions[i]];


        final ObjectAnimator mAnimatorA1 = ObjectAnimator.ofFloat(viewWrapper.getForegroundView(), View.ROTATION_Y, 0, 90).setDuration(500);
        final ObjectAnimator mAnimatorB1 = ObjectAnimator.ofFloat(viewWrapper.getBackgroundView(), View.ROTATION_Y, 270, 360).setDuration(500);
        mAnimatorA1.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                viewWrapper.getForegroundView().setVisibility(View.GONE);
                viewWrapper.getForegroundView().setRotationY(90);
                viewWrapper.getBackgroundView().setVisibility(View.VISIBLE);
                mAnimatorB1.start();
            }
        });

        mAnimatorB1.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationStart(Animator animation) {
                viewWrapper.getBackgroundView().setRotationY(270);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                viewWrapper.exchange();
            }
        });

        mAnimatorA1.start();
    }

    private static class ViewWrapper {

        private View rootView;

        private View foregroundView;

        private View backgroundView;

        private Data foregroundData;

        private ViewWrapper(View view) {
            this.rootView = view;
            initRootView();
        }

        private void initRootView() {

            foregroundView = rootView.findViewById(R.id.foreground);

            backgroundView = rootView.findViewById(R.id.background);

        }

        private void exchange() {
            View temp = foregroundView;

            foregroundView = backgroundView;

            backgroundView = temp;
        }

        private View getForegroundView() {
            return foregroundView;
        }

        private View getBackgroundView() {
            return backgroundView;
        }

        private Data getForegroundData() {
            return foregroundData;
        }

        private void setForegroundData(Data data) {
            this.foregroundData = data;
        }

    }
}
