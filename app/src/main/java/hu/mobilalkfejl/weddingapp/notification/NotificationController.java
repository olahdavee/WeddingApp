package hu.mobilalkfejl.weddingapp.notification;

import android.animation.ArgbEvaluator;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import hu.mobilalkfejl.weddingapp.R;

public class NotificationController {
    private final NotificationManager notificationManager;
    private final Context context;

    public NotificationController(Context context) {
        this.context = context;
        this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        createChannel();
    }

    private void createChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
            return;

        NotificationChannel channel = new NotificationChannel("wedding", "Foglalás", NotificationManager.IMPORTANCE_DEFAULT);

        channel.enableVibration(true);
        channel.setDescription("Esküvői helyszín foglaló alkalmazás értesítés");

        notificationManager.createNotificationChannel(channel);
    }

    public void sendNotification(String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "wedding")
                .setContentTitle("Sikeres helyszín foglalás!")
                .setColor(Color.parseColor("#ff8ba5"))
                .setSmallIcon(R.drawable.icon)
                .setContentText(message);

        notificationManager.notify(1, builder.build());
    }
}
