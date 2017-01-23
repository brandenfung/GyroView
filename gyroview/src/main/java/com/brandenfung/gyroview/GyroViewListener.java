/*
 * Copyright (C) 2017 Branden Fung
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.brandenfung.gyroview;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;

import java.util.ArrayList;

/**
 * Helper class for changing orientation of views from orientation
 * sensor data updates.
 * @see #OrientationEventListener
 *
 * Created by Branden Fung on 1/15/2017.
 */

public class GyroViewListener extends OrientationEventListener {
    private boolean DEBUG = false;
    private String TAG = "GyroViewListener";

//    private Activity mActivity;
    private int mThreshold = 20;
    private int mOrientation; // default activity orientation

    private final ArrayList<OrientationView> mViews = new ArrayList<>();

    /**
     * Creates a new GyroViewListener
     *
     * @param activity containing the views to orient. The Activity must
     *                 have a static orientation set in the manifest file (ie.
     *                 android:screenOrientation=portrait|landscape|reversePortrait|
     *                 reverseLandscape).
     *
     * @throws IllegalStateException
     */
    public GyroViewListener(Activity activity) throws IllegalStateException {
        this(activity, SensorManager.SENSOR_DELAY_NORMAL);
    }

    /**
     * Creates a new GyroViewListener
     *
     * @param activity containing the views to orient.
     * @param rate at which sensor events are processed (see also
     * {@link SensorManager SensorManager}). Use the default
     * value of {@link SensorManager#SENSOR_DELAY_NORMAL
     * SENSOR_DELAY_NORMAL} for simple screen orientation change detection.
     *
     * @throws IllegalStateException
     */
    public GyroViewListener(Activity activity, int rate) throws IllegalStateException {
        super(activity, rate);
        mOrientation = activity.getRequestedOrientation();

        // Activity must have orientation set with a static orientation
        // in the manifest
        if (mOrientation == ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED) {
            throw new IllegalStateException("Calling activity must have a static orientation");
        }
        if (DEBUG) Log.d(TAG, "GyroViewListener successfully created.");
    }

    /**
     *  Add a view to the list of views that is turned when the orientation changes.
     *
     *  View to turn when orientation is changed
     *
     * @param view the view to update
     */
    public void setView(View view) {
        mViews.add(new OrientationView(view));
    }

    public void setThreshold(int threshold) {
        mThreshold = threshold;
    }

    @Override
    public void onOrientationChanged(int orientation) {
        orientation = getDiscreteOrientation(orientation, mThreshold);

        if (DEBUG) Log.d(TAG, "The discrete orientation is : " + orientation);

        if (orientation == ORIENTATION_UNKNOWN) return;

        for (final OrientationView view: mViews) {

            // Only animate if the previous animation has ended
            if (view.getAnimation()!= null && !view.getAnimation().hasEnded()) {
                return;
            }
            else {
                if (view.getPreviousRotation() != orientation) {

                    int initialOrientation =
                            view.getPreviousRotation() == -1? (int) view.getRotation() : view.getPreviousRotation();
                    int finalOrientation = orientation;

                    // Checks are made here to make sure the view orientation does not wrap around
                    if (initialOrientation == 270 && finalOrientation == 0) {
                        finalOrientation = 360;
                    }

                    if (initialOrientation == 0 && finalOrientation == 270) {
                        initialOrientation = 360;
                    }

                    view.setIntValues(initialOrientation, finalOrientation);
                    view.start();
                    view.setPreviousRotation(orientation);
                }
            }
        }
    }

    /**
     * Get the discrete orientation which is obtained by determining which angle (either 0, 90
     * 180, or 270) is closest to the current orientation. The 'closeness' metric is determined
     * by a threshold. For example, if the current orientation is 285 and the threshold is 20, then
     * the return value would be 90.
     *
     * Note: That the view orientation is backwards, compared to the device orientation. So if
     * the device is oriented 270 degrees, the view should be oriented 90 (or 270) degrees.
     *
     * @param orientation current orientation of the device
     * @param thres threshold in which the discrete orientation is determined, an int from [0, 44]
     *              default is 20
     * @return the discrete orientation, one of: 0, 90, 180, or 270
     */
    private int getDiscreteOrientation(int orientation, int thres) {
        if (225 + thres < orientation && orientation <= 315 - thres) {
            return toDegrees(90 + getOffset());
        } else if ((315 + thres < orientation && orientation <= 360) ||
                (0 <= orientation && orientation <= 45 - thres) ) {
            return toDegrees(getOffset());
        } else if (45 + thres < orientation && orientation <= 135 - thres) {
            return toDegrees(270 + getOffset());
        } else if (135 + thres < orientation && orientation <= 225 - thres) {
            return toDegrees(180 + getOffset());
        } else { // happens if orientation == ORIENTATION_UNKNOWN
            return ORIENTATION_UNKNOWN;
        }
    }

    /**
     * Gives the given offset for a given activity orientation. Used to make sure the views
     * are oriented correctly in respect to the activity orientation.
     *
     * @return the offset view orientation for the given activity orientation.
     */
    private int getOffset() {
        switch (mOrientation) {
            case ActivityInfo.SCREEN_ORIENTATION_PORTRAIT:
                return 0;
            case ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE:
                return -90;
            case ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT:
                return 180;
            case ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE:
                return 90;
        }
        return 0;
    }

    /**
     *  Returns the given angle normalised from [0, 360).
     *
     * @param angle angle to normalise
     * @return normalised angle
     */
    public static int toDegrees(int angle) {
        angle = angle % 360; // keep angle withing= 360 degrees
        angle = angle < 0 ? angle + 360 : angle; // deal with negative angles, to wrap around to 360
        return angle;
    }

    /**
     * Class to hold views along side orientation information.
     */
    private class OrientationView {
        private int mPreviousRotation = -1;
        private final View mView;
        private ValueAnimator valueAnimator = null;

        OrientationView(View view) {
            mView = view;
            valueAnimator = ValueAnimator.ofInt();
            valueAnimator.setDuration(500);
            valueAnimator.setInterpolator(new DecelerateInterpolator(1.5f));
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator animation) {
                    Integer value = (Integer) animation.getAnimatedValue();
                    mView.setRotation(value);
                }
            });
        }

        Animation getAnimation() {
            return mView.getAnimation();
        }

        float getRotation() {
            return mView.getRotation();
        }

        int getPreviousRotation() {
            return mPreviousRotation;
        }

        void setPreviousRotation(int prevRot) {
            mPreviousRotation = prevRot;
        }

        void setIntValues(int... values) {
            valueAnimator.setIntValues(values);
        }

        void start() {
            valueAnimator.start();
        }
    }

}
