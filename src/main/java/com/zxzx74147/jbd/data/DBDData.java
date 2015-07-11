package com.zxzx74147.jbd.data;

import com.zxzx74147.jbd.DBService;
import com.zxzx74147.jbd.util.CloseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by zhengxin on 15/7/3.
 */
public class DBDData {
        public int mPaimaiID;
    public int mJDItemID;
    public String mAbstract;
    public float mDealPrice;
    public float mJDPrice;
    public int mTime;

    public String toString() {
        StringBuffer sb = new StringBuffer(100);
        sb.append("jd_id:" + mJDItemID);
        sb.append("\npaimai_id:" + mPaimaiID);
        sb.append("\nabstrace:" + mAbstract);
        sb.append("\ndeal_price:" + mDealPrice);
        sb.append("\n");
        return sb.toString();
    }

    public void insertIntoDB(PreparedStatement ps) {

        try {
            ps.setInt(1, mPaimaiID);
            ps.setInt(2, mJDItemID);
            ps.setString(3, mAbstract);
            ps.setInt(4, mTime);
            ps.setFloat(5, mDealPrice);
            ps.addBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
