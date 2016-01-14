package com.xiaohe.clawler;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Iterator;
import java.util.List;

import org.apache.regexp.recompile;
import org.jsoup.nodes.Element;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlHiddenInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import com.gargoylesoftware.htmlunit.javascript.host.html.HTMLButtonElement;

import junit.framework.Assert;

public class HtmlUnitDemo {
	private static String TARGET_URL = "http://www.shuidixy.com/index.jsp";
		
	public static void main(String[] args) throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		//模拟一个浏览器  
        WebClient webClient = new WebClient();  
        //模拟浏览器打开一个目标网址  
        HtmlPage rootPage= webClient.getPage(TARGET_URL);
        List<HtmlForm> formList = rootPage.getForms();
        if(formList.isEmpty()){
        	return;
        }
        final HtmlForm form = formList.get(0);
       // final HtmlSubmitInput button = form.getInputByName("submitbutton");
        //final HtmlHiddenInput provinceCode = form.getInputByName("provinceCode");
        final HtmlHiddenInput searchType = form.getInputByName("searchType");
        final HtmlTextInput keyInput = form.getInputByName("key");
        /*Iterator<DomElement> inputs = form.getChildElements().iterator();
        System.out.println("1" + inputs.next().asXml());
        System.out.println("2" + inputs.next().asXml());
        DomElement button = inputs.next();
        System.out.println(button.asXml());*/
        final HtmlButton commit = form.querySelector("#btn-search");
        System.out.println(commit.asXml());
        // Change the value of the text field
        //provinceCode.setValueAttribute(null);
        searchType.setValueAttribute("addressContent");
        keyInput.setValueAttribute("长沙市开福区");
        // Now submit the form by clicking the button and get back the second page.
        final HtmlPage page2 = commit.click();
        System.out.println(page2.asXml());
	}
	
}
