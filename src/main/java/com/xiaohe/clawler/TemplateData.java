package com.xiaohe.clawler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.xiaohe.util.FileOperate;

public class TemplateData {
	private static int birthDay = 0;
	private static int birthPlace = 0;
	private static int job = 0;
	private static int achievement = 0;
	private static int school = 0;
	private static int works = 0;
	private static int alias = 0;
	private static int company = 0;
	private static String outputPath;
	private static int number = 5000;
	
	private static void exit_with_help()
	{
		System.out.print(
		 "Usage: java -jar xxx inputfileFolder outputfile\n"
		);
		System.exit(1);
	}
	
	public int process(File pagefile) throws IOException {
		String title = pagefile.getName();
		if (title.contains("（")) {
			title = title.substring(0, title.indexOf("（"));
		}
		Document doc = Jsoup.parse(pagefile, "utf-8");
		//get inforbox
		ArrayList<String> properList = getBox(doc);
		if(properList.isEmpty()){
			return 0;
		}
		System.out.println("crawler the " + title);
		for (Iterator iterator = properList.iterator(); iterator.hasNext();) {
			String proper = (String) iterator.next();
			String[] str = proper.split(":");
			if (str[0].equals("出生地")) {
				birthPlace++;
			}else if (str[0].equals("出生日期")) {
				birthDay++;
			}else if (str[0].equals("职业")) {
				job++;
			}else if (str[0].equals("主要成就")) {
				achievement++;
			}else if (str[0].equals("毕业院校")) {
				school++;
			}else if (str[0].equals("代表作品")) {
				works++;
			}else if (str[0].equals("别名")) {
				alias++;
			}else if (str[0].equals("经纪公司")) {
				company++;
			}else {
				continue;
			}
			String info = title + "###" + proper;
			try {
				FileOperate.WriteFile(outputPath, info);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (birthPlace == number && birthDay == number && job == number && achievement == number && school == number && works == number && alias == number && company == number) {
				return 2;
			}
		}
		return 1;
	}
	/**
	 * @param basicInfo 网页中包含inforbox 的 element
	 * @return 返回一个实体的所有属性，用\t分隔
	 */
	public ArrayList<String> getBox(Element basicInfo) {
		ArrayList<String> properList = new ArrayList<String>();
		Elements dtList = basicInfo.select("dt");
		Elements ddList = basicInfo.select("dd");
		if ((!dtList.isEmpty()) && (!ddList.isEmpty())) {
			for (int i = 0; i < dtList.size(); i++) {
				String property = dtList.get(i).text();
				String[] values = ddList.get(i).text().split("[ 、；,，;]");
				for (int j = 0; j < values.length; j++) {
					String result = property + ":" + values[j];
					properList.add(result);
				}
			}
		}
		return properList;
	}
	
	/**
	 * @param Jsoup加载后的doc
	 * @return 返回一个实体的所有属性，用\t分隔
	 */
	public ArrayList<String> getBox(Document doc) {
		Elements basicInfos = doc.getElementsByClass("basic-info");
		if (basicInfos.isEmpty()) {
			System.err.println("there is no inforbox !");
			return null;
		}
		return getBox(basicInfos.first());
	}
	
	public static void main(String[] args) throws Exception {
		TemplateData templateData = new TemplateData();
		String floderPath = args[0];
		outputPath = args[1];
		if (args.length != 2) {
			exit_with_help();
		}
		File fileFloder = new File(floderPath);
		if (fileFloder.isDirectory()) {
			File[] pagefile = fileFloder.listFiles();
			for (int i = 0; i < pagefile.length; i++) {
				int state = templateData.process(pagefile[i]);
				if (state == 2) {
					break;
				}
			}
		}
		System.out.println("reslove over");
	}
}
