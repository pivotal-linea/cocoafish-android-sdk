package com.cocoafish.demo;

import android.app.Application;
import android.content.Context;
import android.preference.PreferenceManager;

import com.cocoafish.sdk.Cocoafish;
import com.cocoafish.test.TestDriver;

/**
 * This is an example of a {@link android.app.Application} class.  Ordinarily you would use
 * a class like this as a central repository for information that might be shared between multiple
 * activities.
 * 
 * In this case, we have not defined any specific work for this Application.
 * 
 * See samples/ApiDemos/tests/src/com.example.android.apis/ApiDemosApplicationTests for an example
 * of how to perform unit tests on an Application object.
 */
public class DemoApplication extends Application {
	// public static final String APP_ID = "yfqXvb0AClkrVE2mbgmqJmB17BEcEKzF"; // app id in cocoafish.org
	public static final String APP_ID = "MAuUFCbReJmCAzelmEGIktMBlCmI2I7R";
	public static final String FACEBOOK_APP_ID = "109836395704353";
	public static final String APP_COMSUMER_KEY = "YrTFbGEWLhysLsb9QamiqrYWZFXyjgHZ";
	public static final String APP_COMSUMER_SECRET = "ENrGw1tVVNGTC9wkjDxnpKQ4DAsFCGiX";
	private static Cocoafish sdk = null;
	private static DemoSession session = null;


	@Override
    public void onCreate() {
        /*
         * This populates the default values from the preferences XML file. See
         * {@link DefaultValues} for more details.
         */
        PreferenceManager.setDefaultValues(this, R.xml.default_values, false);
        
        // initialize Cocoafish
        initialize(APP_COMSUMER_KEY, APP_COMSUMER_SECRET, getApplicationContext());
    }

    private static void initialize(String appComsumerKey, String appComsumerSecret, Context appContext ) {
		sdk = new Cocoafish(appComsumerKey, appComsumerSecret, appContext);
		session = new DemoSession();
		
	}
	@Override
    public void onTerminate() {
    }

	public static Cocoafish getSdk() {
		return sdk;
	}

    public static DemoSession getSession() {
		return session;
	}
}
