package app.revanced.integrations.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import java.text.Bidi;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import app.revanced.integrations.returnyoutubedislike.requests.ReturnYouTubeDislikeApi;

public class ReVancedUtils {
    @SuppressLint("StaticFieldLeak")
    public static Context context;

    public static Context getContext() {
        return context;
    }

    private ReVancedUtils() {
    } // utility class

    /**
     * Maximum number of background threads run concurrently
     */
    private static final int SHARED_THREAD_POOL_MAXIMUM_BACKGROUND_THREADS = 20;

    /**
     * General purpose pool for network calls and other background tasks.
     * All tasks run at max thread priority.
     */
    private static final ThreadPoolExecutor backgroundThreadPool = new ThreadPoolExecutor(
            1, // minimum 1 thread always ready to be used
            10, // For any threads over the minimum, keep them alive 10 seconds after they go idle
            SHARED_THREAD_POOL_MAXIMUM_BACKGROUND_THREADS,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(),
            r -> {
                Thread t = new Thread(r);
                t.setPriority(Thread.MAX_PRIORITY); // run at max priority
                return t;
            });

    public static <T> Future<T> submitOnBackgroundThread(Callable<T> call) {
        Future<T> future = backgroundThreadPool.submit(call);
        return future;
    }

    public static boolean containsAny(final String value, final String... targets) {
        for (String string : targets)
            if (!string.isEmpty() && value.contains(string)) return true;
        return false;
    }
    
    private static final boolean isRightToLeftTextLayout =
            new Bidi(Locale.getDefault().getDisplayLanguage(), Bidi.DIRECTION_DEFAULT_RIGHT_TO_LEFT).isRightToLeft();
    /**
     * If the device language uses right to left text layout (hebrew, arabic, etc)
     */
    public static boolean isRightToLeftTextLayout() {
        return isRightToLeftTextLayout;
    }

    /**
     * Automatically logs any exceptions the runnable throws
     */
    public static void runOnMainThread(Runnable runnable) {
        Runnable exceptLoggingRunnable = () -> {
            try {
                runnable.run();
            } catch (Exception ex) {
                LogHelper.printException(ReVancedUtils.class, "Exception on main thread from runnable: " + runnable.toString(), ex);
            }
        };
        new Handler(Looper.getMainLooper()).post(exceptLoggingRunnable);
    }

    public static void runDelayed(Runnable runnable, Long delay) {
        Runnable exceptLoggingRunnable = () -> {
            try {
                runnable.run();
            } catch (Exception ex) {
                LogHelper.printException(ReVancedUtils.class, "Exception on main thread from runnable: " + runnable.toString(), ex);
            }
        };
        new Handler(Looper.getMainLooper()).postDelayed(exceptLoggingRunnable, delay);
    }

    /**
     * @return if the calling thread is on the main thread
     */
    public static boolean currentIsOnMainThread() {
        return Looper.getMainLooper().isCurrentThread();
    }

    /**
     * @throws IllegalStateException if the calling thread _is_ on the main thread
     */
    public static void verifyOffMainThread() throws IllegalStateException {
        if (currentIsOnMainThread()) {
            throw new IllegalStateException("Must call _off_ the main thread");
        }
    }
}
