This open source Java library allows you to integrate Cocoafish into your Android application. Except as otherwise noted, the Cocoafish Android SDK is licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)

Getting Started
===============

The SDK is lightweight and has no external dependencies. Getting started is easy.

Setup your environment
--------------------------

1. Pull the repository from GitHub:

    git clone git://github.com/cocoafish/cocoafish-android-sdk.git

2. If you have not already done so, follow the (http://developer.android.com/sdk/index.html)[Android SDK Getting Started Guide]. You will need the device emulator and debugging tools.

3. The Cocoafish Android SDK works fine in any Android development environment. To build in Eclipse:

  * Create a new project for the Cocoafish  SDK in your Eclipse workspace. 
  * Select __File__ -> __New__ -> __Project__, choose __Android Project__ (inside the Android folder), and then click __Next__.
  * Select "Create project from existing source".
  * Select the __cocoafish__ subdirectory from within the git repository. You should see the project properties populated (you might want to change the project name to something like "FacebookSDK").
  * Click Finish to continue.

The Cocoafish  SDK is now configured and ready to go.  

Sample Applications
--------------------

This library includes two sample applications to guide you in development.

* __demo__: A simple demo app that demonstrates places map view, user signup, login and facebook login and user checkins.

* Create the sample application in your workspace:
2. Select __File__ -> __New__ -> __Project__, choose __Android Project__, and then click __Next__.
  3. Select "Create project from existing source".
  4. Choose __demo__. You should see the project properties populated.
  5. Click Finish to continue.

* Build the project: from the Project menu, select "Build Project".

* Run the application: from the Run menu, select "Run Configurations...".  Under Android Application, you can create a new run configuration: give it a name and select the simple Example project; use the default activity Launch Action.  See http://developer.android.com/guide/developing/eclipse-adt.html#RunConfig for more details.

Integrate with an existing application
-----------

The easiest way to get started is to copy/hack up the sample applications (that's what they are there for). However, if you want to just integrate the Facebook SDK with an existing application (or create a new one from scratch), then you should:

* Add a dependency on the Cocoafish Android SDK library on your application:
  1. Select __File__ -> __Properties__. Open the __Android__ section within the Properties dialog.
  2. In the bottom __Library__ section, click __Add...__ and select the Cocoafish SDK project.
  3. Any issues? Check [Android documentation](http://developer.android.com/guide/developing/eclipse-adt.html#libraryProject)

* Ensure that your application has network access (android.permission.INTERNET) in the Android manifest:

	<code><uses-permission android:name="android.permission.INTERNET"></uses-permission></code>

