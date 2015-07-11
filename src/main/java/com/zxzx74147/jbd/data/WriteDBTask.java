package com.zxzx74147.jbd.data;

import com.zxzx74147.jbd.DBService;
import com.zxzx74147.jbd.base.PipeTask;
import com.zxzx74147.jbd.util.CloseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedList;

/**
 * Created by zhengxin on 15/7/8.
 */
public class WriteDBTask extends PipeTask<DBDData,PipeTask> {
    private static final String INSERT_SQL = "insert into item_dbd(id,jd_item_id,item_abs,time,deal_price) values(?,?,?,?,?) ON DUPLICATE KEY UPDATE id=VALUES(id)";

    public WriteDBTask(PipeTask outQueue){
        super(1000,3,outQueue);

    }
    @Override
    public Runnable getRunnable() {
        return new WriteDBRunnable();
    }

    private class WriteDBRunnable implements Runnable {
        private LinkedList<DBDData> mCacheData;

        public WriteDBRunnable(){
            mCacheData = new LinkedList<>();
        }
        @Override
        public void run() {
            Connection mConn = DBService.sharedInstance().getConnection();
            try {
                while(true) {
                    for (int i = 0; i < 80; i++) {
                        DBDData data = mDataQueue.poll();
                        if (data == null) {
                            if (mDataQueue.size() == 0) {
                                data = mDataQueue.take();
                            } else {
                                break;
                            }
                        }
                        if(data!= null){
                            mCacheData.add(data);
                        }
                    }
                    if (mCacheData.size() == 0) {
                        continue;
                    }
                    if(isInterrupt){
                        break;
                    }
                    PreparedStatement ps =null;
                    try {
                        ps = mConn.prepareStatement(INSERT_SQL);
                        System.out.println("write_size="+mCacheData.size());
                        for(DBDData data:mCacheData){
                            data.insertIntoDB(ps);
                        }
                        int[] result = ps.executeBatch();
                        System.out.println(result);
                    }catch (Exception e){
                        e.printStackTrace();
                    }finally {
                        try {
                            if(ps != null) {
                                ps.clearParameters();
                                ps.clearBatch();
                                ps.close();
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                    mCacheData.clear();
                }
            }catch (InterruptedException e){
                e.printStackTrace();
            }
            finally {
                CloseUtil.close(mConn);
            }
        }
    }
}
