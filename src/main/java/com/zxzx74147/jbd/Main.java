package com.zxzx74147.jbd;

import com.zxzx74147.jbd.data.DBDData;
import com.zxzx74147.jbd.data.GetInfoTask;
import com.zxzx74147.jbd.data.WriteDBTask;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by zhengxin on 15/7/2.
 */

public class Main {
    private static final String SQL_CHECK = "select id from item_dbd order by id limit 1";
    private static WriteDBTask mWriteDBTask;
    private static GetInfoTask mGetInfoTask;

    public static void main(String[] args){
        boolean result = DBService.sharedInstance().init();
        System.out.println("DB_INIT_STATUS = " + result);
        if(!result){
            return;
        }

        mWriteDBTask = new WriteDBTask(null);
        mGetInfoTask = new GetInfoTask(mWriteDBTask);

        Runtime.getRuntime().addShutdownHook(new ExitHandler());

        int mStart = 0;
        Connection mConn = DBService.sharedInstance().getConnection();
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        try {
            preparedStatement= mConn.prepareStatement(SQL_CHECK);
            rs = preparedStatement.executeQuery();
            while (rs.next()) {
                mStart= rs.getInt("id");
                mStart+=200;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            if(preparedStatement != null){
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }


        for(int i=mStart;i>0;i--) {
            mGetInfoTask.putItem(i);
        }
        terminel();
    }

    private static class ExitHandler extends Thread {

        public void run() {
            System.out.println("Set exit");
            terminel();
        }
    }

    private static void terminel(){
        DBService.sharedInstance().close();
        mWriteDBTask.interrupt();
        mGetInfoTask.interrupt();
    }
}
