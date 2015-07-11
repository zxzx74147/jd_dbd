package com.zxzx74147.jbd.decoder;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

/**
 * Created by zhengxin on 15/7/3.
 */
public class DecoderConfig {


    public static final String ORI_PRICE = "#auctionStatus2 > div.auction_intro > div.cost > del";
    public static final String DEAL_PRICE = "#records-list > div.records > dl > dd > span.wd2 > i.price";
    public static final String ORI_URL = "#productId";
    public static final String ABSTRACT = "#container > div.details_main > div.product_intro > div.intro_detail > div.name > h1";

}
