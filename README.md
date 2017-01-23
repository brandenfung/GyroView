# GyroView
A simple Android library for orienting views right-side up in an Activity with static orientation.
![GYROVIEW GIF](https://github.com/brandenfung/GyroView/blob/master/demo.gif)

## Usage
Instantiate GyroViewListener, add the appropriate views to the listener, and enable it. For example, in an Activity:

```java
private GyroViewListener mListener = null;

protected void onCreate(Bundle savedInstanceState) {
    ...  
    // Here are the views I want to rotate
    mButton = (Button) findViewById(R.id.button);
    mTextView = (TextView) findViewById(R.id.textView);
    mImageView = (ImageView) findViewById(R.id.imageView);
    
    // Create the listener and add my views
    mListener = new GyroViewListener(this);
    mListener.setView(mButton);
    mListener.setView(mTextView);
    mListener.setView(mImageView);
    
    // Remember to enable the listener
    mListener.enable();
}
```
## Download
Grab the library via Gradle: 

```groovy

repositories {
    jcenter()
}

dependencies {
    compile 'com.brandenfung:gyroview:1.0.0'
}
```

## License
Copyright 2017 Branden Fung

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
