package fr.aumgn.tobenamed.util;

import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;


public abstract class Timer implements Runnable {

    private static final int TICKS_PER_SECONDS = 20;
    private static final int MAJOR_DELAY = 2 * 60;
    private static final int MINOR_DELAY = 20;

    private Runnable runnable;
    private int seconds;

    private int currentDelay;
    private int taskId;
    private long taskStartTime;
    private int pauseDelay;

    public Timer(int seconds, Runnable runnable) {
        this.seconds = seconds;
        this.runnable = runnable;

        this.currentDelay = 0;
    }

    @Override
    public void run() {
        seconds -= currentDelay;
        if (seconds > MAJOR_DELAY) {
            scheduleAndPrintTime(MAJOR_DELAY, ChatColor.YELLOW);
        } else if (seconds > MINOR_DELAY) {
            scheduleAndPrintTime(MINOR_DELAY, ChatColor.GOLD);
        } else if (seconds > 0) {
            scheduleAndPrintTime(1, ChatColor.RED);
        } else {
            runnable.run();
        }
    }

    private void scheduleAndPrintTime(int delay, ChatColor color) {
        long minutes = TimeUnit.SECONDS.toMinutes(seconds);
        String msg = String.format("%02d:%02d", minutes, seconds % 60);
        sendTimeMessage(color + msg);
        currentDelay = delay;
        taskStartTime = System.currentTimeMillis();
        taskId = TBNUtil.scheduleDelayed(delay * TICKS_PER_SECONDS, this);
    }

    public void pause() {
        Bukkit.getScheduler().cancelTask(taskId);
        pauseDelay = (int) ((System.currentTimeMillis() - taskStartTime ) / 1000);
        seconds -= pauseDelay;
    }

    public void resume() {
        scheduleAndPrintTime(currentDelay - pauseDelay, ChatColor.YELLOW); 
    }

    public abstract void sendTimeMessage(String string);
}
