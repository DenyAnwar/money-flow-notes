package mythgreendev.com.app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // Thread
        Thread timer = new Thread() {
            public void run() {
                try {
                    sleep(2000); // 1000 = 1 detik
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    // Intent
                    // Intent(activity_yang_sekarang, activity_selanjutnya)
                    Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                    startActivity(intent);

                    // Close activity
                    finish();
                }
            }
        };
        timer.start();
    }
}