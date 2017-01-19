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
package com.brandenfung.gyroviewsample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.brandenfung.gyroview.GyroViewListener;

public class LandscapeActivity extends AppCompatActivity {

    private GyroViewListener mListener = null;
    private Button mButton = null;
    private TextView mTextView = null;
    private ImageView mImageView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landscape);

        mButton = (Button) findViewById(R.id.button);
        mTextView = (TextView) findViewById(R.id.textView2);
        mImageView = (ImageView) findViewById(R.id.imageView);

        mListener = new GyroViewListener(this);
        mListener.setView(mButton);
        mListener.setView(mTextView);
        mListener.setView(mImageView);
        mListener.enable();
    }

}
