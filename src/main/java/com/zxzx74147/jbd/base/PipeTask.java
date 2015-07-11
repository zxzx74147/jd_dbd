package com.zxzx74147.jbd.base;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhengxin on 15/7/7.
 */
public abstract class PipeTask<I,PipeTask> {

    protected LinkedBlockingQueue<I> mDataQueue;
    protected BlockingQueue<Runnable> mThreadQueue;
    protected PipeTask mOutTask;
    protected Runnable mWorker;
    protected volatile boolean isInterrupt = false;
    protected ThreadPoolExecutor mExecutor;

    public PipeTask(int capacity, int workNum,PipeTask outTask) {
        mDataQueue = new LinkedBlockingQueue<>(capacity);
        mThreadQueue = new LinkedBlockingQueue<>(100);
        mOutTask = outTask;
        mExecutor = new ThreadPoolExecutor(workNum, workNum * 2, 100, TimeUnit.DAYS, mThreadQueue);
        for(int i=0;i<workNum;i++) {
            mExecutor.execute(getRunnable());
        }
    }

    public void putItem(I data) {
        try {
            mDataQueue.put(data);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void interrupt(){
        isInterrupt = true;
        mExecutor.shutdown();
    }

    public abstract Runnable getRunnable();


}
