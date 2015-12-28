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

public class HaosouBaikePageProcessor implements PageProcessor {
	private static Logger logger = Logger.getLogger(HaosouBaikePageProcessor.class.getName());
	private static int count = 0;
	private static int num = 0;
    private Site site = Site.me()//.setHttpProxy(new HttpHost("127.0.0.1",8888))
            .setCycleRetryTimes(3).setSleepTime(1000).setUseGzip(true);//循环重试，失败的放入队尾
    private String pattern = "^(http://baike.haosou.com/doc/).*\\.html$";

	public void process(Page page) {
		String title = page.getHtml().css("title", "text").toString();
		title = title.substring(0, title.indexOf("_"));
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
		String info = getBox(doc);
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
			FileOperate.WriteFile("haosou/haosouBaike-"+num, info);
			count ++;
			//FileOperate.WriteFile("resource/urlQueue", page.getRequest().getUrl());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * @param basicInfo 网页中包含inforbox 的 element
	 * @return 返回一个实体的所有属性，用\t分隔
	 */
	public String getBox(Element cardListBox) {
		StringBuilder sb = new StringBuilder();
		Elements cardlist = cardListBox.select(".cardlist-con");
		if(cardlist.isEmpty()){
			return null;
		}
		for(Element card : cardlist){
			String property = card.select("p").get(0).text();
			String value = card.select("p").get(1).text();
			String result = property + ":" + value + "\t";
			sb.append(result);
		}
		sb.deleteCharAt(sb.length()-1);
		return sb.toString();
	}
	
	/**
	 * @param basicInfo 网页中包含infotable 的 element
	 * @return 返回一个实体的所有属性，用\t分隔
	 */
	public String getTable(Element table) {
		StringBuilder sb = new StringBuilder();
		Elements trList = table.select("tr");
		for(Element tr : trList){
			if(tr.getElementsByClass("more").isEmpty()){
				String property = tr.select("th").text();
				String value = tr.select("td").text();
				String result = property + ":" + value + "\t";
				sb.append(result);
			}else{
				Elements dlList = tr.select("dl");
				for(Element dl : dlList){
					String property = dl.select("dt").text().replace("：", "");
					String value = dl.select("dd").text();
					String result = property + ":" + value + "\t";
					sb.append(result);
				}
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
		Elements cardListBoxs = doc.getElementsByClass("card-list-box");
		if (cardListBoxs.isEmpty()) {
			Element basicInfo = doc.getElementById("basic-info");
			if(basicInfo == null || basicInfo.select("tbody").isEmpty()){
				logger.error("there is no inforbox !");
				return null;
			}
			Element table = basicInfo.select("tbody").first();
			return getTable(table);
		}
		return getBox(cardListBoxs.first());
	}
	
	/**
	 * @param doc Jsoup加载后的doc
	 * @return	其他百科链接的list
	 */
	public List<String> getUrl(Document doc) {
		List<String> list = new ArrayList<String>();
		Elements boxwraps = doc.getElementsByClass("boxwrap");
		if(boxwraps.isEmpty()){
			logger.error("there is no content !");
			return null;
		}
		Element boxwrap = boxwraps.first();
		Elements aHrefs = boxwrap.select("a");
		if(aHrefs.isEmpty()){
			logger.error("there is no href !");
			return null;
		}
		Pattern r = Pattern.compile(pattern);
		for(Element href : aHrefs){
			String url = href.attr("href");
			Matcher m = r.matcher(url);
			if (m.matches()) {
				logger.debug(href.text() + " : " + url);
				//System.out.println(href.text() + " : " + url);
				list.add(url);
			}
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
    	FileCacheQueueScheduler scheduler = new FileCacheQueueScheduler("haosou");
    	Spider spider = Spider.create(new HaosouBaikePageProcessor()).scheduler(scheduler);
    	scheduler.push(new Request(args[0]),spider);
    	spider.thread(5).run();
    	spider.stop();
    }
}
