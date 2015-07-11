package com.zxzx74147.jbd.data;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.zxzx74147.jbd.UrlConfig;
import com.zxzx74147.jbd.base.PipeTask;
import com.zxzx74147.jbd.decoder.DecoderConfig;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

/**
 * Created by zhengxin on 15/7/8.
 */
public class GetInfoTask extends PipeTask<Integer,WriteDBTask> {

    private OkHttpClient client = new OkHttpClient();

    public GetInfoTask(WriteDBTask outTask){
        super(1000,6,outTask);

    }
    @Override
    public Runnable getRunnable() {
        return new GetInfoRunnable();
    }

    private class GetInfoRunnable implements Runnable {

        @Override
        public void run() {
            try {
            while(true){
                int id = mDataQueue.take();
                DBDData data = getItemInfo(id);
                if(data == null){
                    continue;
                }
                mOutTask.putItem(data);
                System.out.print(data.toString());
            }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public DBDData getItemInfo(int paimaiId){
        DBDData result = new DBDData();
        try {

            result.mPaimaiID = paimaiId;
            String url = String.format(UrlConfig.JD_DBD_URL, paimaiId);
            String html = getNetData(url);
            Document document = Jsoup.parse(html);

            Elements elements = document.select(DecoderConfig.ORI_URL);
            result.mJDItemID = Integer.valueOf(elements.attr("value"));

            elements = document.select(DecoderConfig.ABSTRACT);
            result.mAbstract = elements.text();

            String paimaiPrice = String.format(UrlConfig.QUERY_DEAL_URL, paimaiId);
            String paimaiRsp = getNetData(paimaiPrice);

            ObjectMapper paimaiMapper = new ObjectMapper();
            try {
                JsonNode rootNode = paimaiMapper.readTree(paimaiRsp);
                JsonNode priceList = rootNode.path("bidList");
                if (priceList.isArray() && priceList.size() > 0) {
                    JsonNode dealNode = priceList.get(0);
                    JsonNode priceNode = dealNode.path("price");
                    result.mDealPrice = (float) priceNode.asDouble();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }catch (Exception e){
            result = null;
        }
        return result;
    }

    private String getNetData(String url){
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
            String html =  response.body().string();
            return html;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
