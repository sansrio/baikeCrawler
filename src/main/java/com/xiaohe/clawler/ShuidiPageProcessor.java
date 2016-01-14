package com.xiaohe.clawler;

import org.apache.log4j.Logger;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.downloader.selenium.SeleniumDownloader;
import us.codecraft.webmagic.pipeline.FilePipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.processor.example.BaiduBaikePageProcessor;

public class ShuidiPageProcessor implements PageProcessor{
	private static Logger logger = Logger.getLogger(BaiduBaikePageProcessor.class.getName());
    private Site site;

	public Site getSite() {
		if (null == site) {
           site = Site.me().setDomain("http://www.shuidixy.com/").setSleepTime(0);
           //site = Site.me().setDomain("huaban.com").setSleepTime(0);
        }
        return site;
	}

	public void process(Page page) {
		page.addTargetRequests(page.getHtml().links().regex("http://huaban\\.com/.*").all());
        if (page.getUrl().toString().contains("pins")) {
            page.putField("img", page.getHtml().xpath("//div[@id='pin_img']/a/img/@src").toString());
        } else {
            page.getResultItems().setSkip(true);
        }
	}
	
	public static void main(String[] args) {
		/*Spider.create(new ShuidiPageProcessor()).thread(5)
        .addPipeline(new FilePipeline("/data/webmagic/test/"))
        .setDownloader(new SeleniumDownloader("/Users/yihua/Downloads/chromedriver"))
        .addUrl("http://huaban.com/")
        .runAsync();*/
		Spider.create(new ShuidiPageProcessor()).thread(5)
        .addPipeline(new FilePipeline("/home/riter/toolkit/"))
        .setDownloader(new SeleniumDownloader("/home/riter/toolkit/chromedriver").setSleepTime(1000))
        .addUrl("http://www.shuidixy.com/")
        .runAsync();
	}
}
