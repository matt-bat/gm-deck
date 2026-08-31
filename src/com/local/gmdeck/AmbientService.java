package com.local.gmdeck;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.IBinder;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Random;

public class AmbientService extends Service {
    public static final String ACTION_PLAY = "com.local.gmdeck.PLAY_AMBIENT";
    public static final String ACTION_STOP = "com.local.gmdeck.STOP_AMBIENT";
    public static final String EXTRA_SCENE = "scene";
    public static final String EXTRA_VOLUME = "volume";
    private static final int RATE = 22050;
    private static final int NOTIFICATION_ID = 2202;
    private static final String CHANNEL_ID = "gmdeck_ambient";

    private volatile boolean running;
    private volatile String scene = "rain";
    private volatile int volume = 45;
    private AudioTrack track;
    private Thread audioThread;

    @Override public IBinder onBind(Intent intent) { return null; }

    @Override public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && ACTION_STOP.equals(intent.getAction())) {
            stopSelf();
            return START_NOT_STICKY;
        }
        if (intent != null) {
            scene = safeScene(intent.getStringExtra(EXTRA_SCENE));
            volume = Math.max(5, Math.min(100, intent.getIntExtra(EXTRA_VOLUME, 45)));
        }
        startForeground(NOTIFICATION_ID, notification(scene));
        if (!running) startAudio();
        return START_STICKY;
    }

    private String safeScene(String value) {
        if (value == null) return "rain";
        String[] requested = value.split(",");
        StringBuilder safe = new StringBuilder();
        for (String item : requested) {
            if (!("rain".equals(item) || "tavern".equals(item) || "dungeon".equals(item)
                    || "forest".equals(item) || "campfire".equals(item))) continue;
            if (safe.indexOf(item) >= 0) continue;
            if (safe.length() > 0) safe.append(',');
            safe.append(item);
            if (safe.toString().split(",").length == 3) break;
        }
        return safe.length() == 0 ? "rain" : safe.toString();
    }

    private void startAudio() {
        running = true;
        int minimum = AudioTrack.getMinBufferSize(RATE, AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT);
        track = new AudioTrack(AudioManager.STREAM_MUSIC, RATE, AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT, Math.max(minimum, 8192), AudioTrack.MODE_STREAM);
        track.play();
        audioThread = new Thread(new Runnable() {
            @Override public void run() { renderLoop(); }
        }, "GMDeckAmbient");
        audioThread.start();
    }

    private void renderLoop() {
        short[] buffer = new short[2048];
        Random random = new Random();
        double tavernA = 0, tavernB = 0, dungeonPhase = 0, birdPhase = 0, smooth = 0;
        long sample = 0;
        while (running) {
            String active = scene;
            for (int i = 0; i < buffer.length; i++, sample++) {
                double white = random.nextDouble() * 2 - 1;
                smooth = smooth * .985 + white * .015;
                String wrapped = "," + active + ",";
                int layers = 0;
                double value = 0;
                if (wrapped.contains(",rain,")) { value += white * .13 + smooth * .3; layers++; }
                if (wrapped.contains(",tavern,")) {
                    tavernA += 2 * Math.PI * 110 / RATE;
                    tavernB += 2 * Math.PI * 164.81 / RATE;
                    value += Math.sin(tavernA) * .05 + Math.sin(tavernB) * .035 + smooth * .11; layers++;
                }
                if (wrapped.contains(",dungeon,")) {
                    dungeonPhase += 2 * Math.PI * 46 / RATE;
                    boolean drip = sample % (RATE * 5L) < 550;
                    value += Math.sin(dungeonPhase) * .08 + smooth * .18 + (drip ? Math.sin(sample * .18) * .1 * (1 - (sample % (RATE * 5L)) / 550.0) : 0); layers++;
                }
                if (wrapped.contains(",forest,")) {
                    birdPhase += 2 * Math.PI * 1700 / RATE;
                    boolean bird = sample % (RATE * 7L) < 1800;
                    value += smooth * .2 + (bird ? Math.sin(birdPhase) * .035 * Math.sin(Math.PI * (sample % (RATE * 7L)) / 1800.0) : 0); layers++;
                }
                if (wrapped.contains(",campfire,")) {
                    boolean crackle = random.nextDouble() > .992;
                    value += smooth * .25 + (crackle ? white * .38 : 0); layers++;
                }
                double gain = (volume / 100.0) / Math.sqrt(Math.max(1, layers));
                buffer[i] = (short)(Math.max(-1, Math.min(1, value * gain)) * 21000);
            }
            if (track != null) track.write(buffer, 0, buffer.length);
        }
    }

    private Notification notification(String activeScene) {
        createChannel();
        Intent open = new Intent(this, MainActivity.class);
        PendingIntent pending = PendingIntent.getActivity(this, 0, open, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Builder builder = new Notification.Builder(this)
                .setSmallIcon(android.R.drawable.ic_media_play)
                .setContentTitle("GM Deck ambience")
                .setContentText(capitalize(activeScene.replace(",", " + ")) + " playing")
                .setContentIntent(pending)
                .setOngoing(true);
        try {
            Method setChannelId = Notification.Builder.class.getMethod("setChannelId", String.class);
            setChannelId.invoke(builder, CHANNEL_ID);
        } catch (Exception ignored) { }
        return builder.build();
    }

    private void createChannel() {
        try {
            Class<?> channelClass = Class.forName("android.app.NotificationChannel");
            Constructor<?> constructor = channelClass.getConstructor(String.class, CharSequence.class, int.class);
            Object channel = constructor.newInstance(CHANNEL_ID, "GM Deck ambience", 2);
            NotificationManager manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
            Method create = NotificationManager.class.getMethod("createNotificationChannel", channelClass);
            create.invoke(manager, channel);
        } catch (Exception ignored) { }
    }

    private String capitalize(String text) { return text.substring(0, 1).toUpperCase() + text.substring(1); }

    @Override public void onDestroy() {
        running = false;
        if (track != null) {
            try { track.pause(); track.flush(); track.release(); } catch (Exception ignored) { }
            track = null;
        }
        stopForeground(true);
        super.onDestroy();
    }
}
