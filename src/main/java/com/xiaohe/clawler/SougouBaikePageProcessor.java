package com.xiaohe.clawler;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.FileCacheQueueScheduler;

public class SougouBaikePageProcessor implements PageProcessor {
	private static Logger logger = Logger.getLogger(SougouBaikePageProcessor.class.getName());
	private static int count = 0;
	private static int num = 0;
    private Site site = Site.me()//.setHttpProxy(new HttpHost("127.0.0.1",8888))
            .setCycleRetryTimes(3).setSleepTime(1000).setUseGzip(true);//循环重试，失败的放入队尾
    private String pattern = "^(http://http://baike.sogou.com)[^#]*\\.htm$";

	public void process(Page page) {
		System.out.println("1");
		String title = page.getHtml().css("title", "text").toString();
		title = title.substring(0, title.indexOf("-"));
		System.out.println(title);
		String pagehtml = page.getHtml().toString();
		Document doc = null;
		doc = Jsoup.parse(pagehtml);
		//get url
		List<String> list = getUrl(doc);
		if(list == null){
			return;
		}
		page.addTargetRequests(list);
		//get inforbox
		/*String info = getBox(doc);
		if(info == null){
			return;
		}
		info = title + "\t" + info;
		//写入文件
		try {
			logger.info("crawler the " + title);
			if (count == 1000000) {//每个文件100万条百科
				count = 0;
				num++;
			}
			FileOperate.WriteFile("sougou/sougouBaike-"+num, info);
			count ++;
			//FileOperate.WriteFile("resource/urlQueue", page.getRequest().getUrl());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}
	
	/**
	 * @param basicInfo 网页中包含inforbox 的 element
	 * @return 返回一个实体的所有属性，用\t分隔
	 */
	public String getBox(Element basicInfo) {
		Elements dtList = basicInfo.select("dt");
		Elements ddList = basicInfo.select("dd");
		StringBuilder sb = new StringBuilder();
		if ((!dtList.isEmpty()) && (!ddList.isEmpty())) {
			for (int i = 0; i < dtList.size(); i++) {
				String property = dtList.get(i).text();
				String value = ddList.get(i).text();
				String result = property + ":" + value + "\t";
				sb.append(result);
			}
		}
		sb.deleteCharAt(sb.length()-1);
		return sb.toString();
	}
	
	/**
	 * @param Jsoup加载后的doc
	 * @return 返回一个实体的所有属性，用\t分隔
	 */
	public String getBox(Document doc) {
		Elements basicInfos = doc.getElementsByClass("basic-info");
		if (basicInfos.isEmpty()) {
			logger.error("there is no inforbox !");
			return null;
		}
		return getBox(basicInfos.first());
	}
	
	/**
	 * @param doc Jsoup加载后的doc
	 * @return	其他百科链接的list
	 */
	public List<String> getUrl(Document doc) {
		List<String> list = new ArrayList<String>();
		Elements mainWraps = doc.getElementsByClass("main_wrap");
		if(mainWraps.isEmpty()){
			logger.error("there is no content !");
			return null;
		}
		Element mainWrap = mainWraps.first();
		Elements aHrefs = mainWrap.select("a");
		if(aHrefs.isEmpty()){
			logger.error("there is no href !");
			return null;
		}
		Pattern r = Pattern.compile(pattern);
		for(Element href : aHrefs){
			String url = href.attr("href");
			Matcher m = r.matcher(url);
			//if (m.matches()) {
				//logger.debug(href.text() + " : " + url);
				System.out.println(href.text() + " : " + url);
				list.add(url);
			//}
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
    	FileCacheQueueScheduler scheduler = new FileCacheQueueScheduler("sougou");
    	Spider spider = Spider.create(new HaosouBaikePageProcessor()).scheduler(scheduler);
    	scheduler.push(new Request("http://baike.sogou.com/v65544676.htm"),spider);
    	spider.thread(5).run();
    	spider.stop();
    }
}
