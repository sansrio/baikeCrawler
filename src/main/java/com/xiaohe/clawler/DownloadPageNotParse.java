package com.xiaohe.clawler;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.bcel.verifier.statics.StringRepresentation;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.xiaohe.util.FileOperate;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.FilePipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.FileCacheQueueScheduler;

public class DownloadPageNotParse implements PageProcessor{
	private static Logger logger = Logger.getLogger(BaiduBaikePageProcessor.class.getName());
    private Site site = Site.me()//.setHttpProxy(new HttpHost("127.0.0.1",8888))
            .setCycleRetryTimes(3).setSleepTime(1000).setUseGzip(true);//循环重试，失败的放入队尾
    private String pattern = "^(http://baike.baidu.com/view/).*\\.htm$|^(http://baike.baidu.com/subview/).*\\.htm$";
    private static String filePath;

	public void process(Page page) {
		String title = page.getHtml().css("title", "text").toString();
		title = title.substring(0, title.indexOf("_"));
		String pagehtml = page.getHtml().toString();
		Document doc = null;
		doc = Jsoup.parse(pagehtml);
		//get url
		List<String> list = getUrl(doc, title);
		if(list == null){
			return;
		}
		page.addTargetRequests(list);
		//写入文件
		Element tag = doc.getElementById("open-tag");
		if (tag != null) {
			String openTag = tag.text();
			if (openTag.contains("， 人物") || openTag.contains("： 人物 ，")) {
				System.out.println(openTag);
				try {
					logger.info("crawler the " + title);
					FileOperate.WriteFile(filePath + "/"+ title, pagehtml);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * @param doc Jsoup加载后的doc
	 * @return	其他百科链接的list
	 */
	public List<String> getUrl(Document doc, String title) {
		List<String> list = new ArrayList<String>();
		Elements mainContents = doc.getElementsByClass("main-content");
		if(mainContents.isEmpty()){
			logger.error("there is no content !");
			return null;
		}
		Element mainContent = mainContents.first();
		Elements aHrefs = mainContent.select("a");
		if(aHrefs.isEmpty()){
			logger.error("there is no href !");
			return null;
		}
		Pattern r = Pattern.compile(pattern);
		StringBuffer entityRelation = new StringBuffer();
		StringBuffer entityName = new StringBuffer();
		for(Element href : aHrefs){
			String url = href.attr("href");
			Matcher m = r.matcher(url);
			if (m.matches()) {
				logger.debug(href.text() + " : " + url);
				list.add(url);
				entityName.append(href.text()+"\n");
				entityRelation.append(title + "\t" + href.text() + "\n");
			}
		}
		try {
			FileOperate.WriteFile("entity/entityName", entityName.deleteCharAt(entityName.length()-1).toString());
			FileOperate.WriteFile("entity/entityRelation", entityRelation.deleteCharAt(entityRelation.length()-1).toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
	
    public Site getSite() {
        return site;
    }

    /**
     * @param 默认地址：结果保存路径resource文件夹下，文件缓存队列，也是在这个位置
     * 结果：	baiduBaike-i	每1000000万条百科为一个文件。
     * 			null.cursor.txt	记录已爬取的行数
     * 			null.urls.txt	待爬队列
     */
    public static void main(String[] args) {
    	System.out.println("参数0：种子url，参数1：网页保存路径，参数2：线程数");
    	FileCacheQueueScheduler scheduler = new FileCacheQueueScheduler(args[1]);
    	Spider spider = Spider.create(new DownloadPageNotParse()).scheduler(scheduler);
    	filePath = args[1];
    	scheduler.push(new Request(args[0]),spider);
    	spider.thread(Integer.parseInt(args[2])).run();
    	spider.stop();
    }
}
