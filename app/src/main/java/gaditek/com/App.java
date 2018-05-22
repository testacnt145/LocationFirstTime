package gaditek.com;

import android.app.Application;

public class App extends Application {

    private static App mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

//__________________________________________________________________________________________________
    //application context
    public static synchronized App getInstance() {
        return mInstance;
    }

}
