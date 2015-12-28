package com.xiaohe.clawler;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.nodes.Element;

public class Test {
	public static void main(String[] args) {
		String pattern = "老炮儿	中文名:老炮儿	英文名:Mr.Six	主演:冯小刚，张涵予，许晴，梁静，李易峰，吴亦凡，刘桦	上映时间:2015年12月24日	类别:喜剧 动作	导演:管虎	编剧:管虎，董润年	制片人:王中磊	画面颜色:彩色	imdb编码:tt4701702	出品公司:华谊兄弟传媒股份有限公司	发行公司:华谊兄弟传媒股份有限公司	制片地点:中国大陆	拍摄日期:2014年12月13日-2015年3月底";
		String []ss = pattern.split("\t");
		for(String s : ss){
			System.out.println(s);
		}
	}
}
