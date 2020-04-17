package com.merico.inftest;

import org.testng.TestNG;
import org.testng.xml.XmlSuite;

/**
 * 实现自己的TestNG类 动态的创建虚拟的xml 确保case运行的顺序
 * todo 动态生成xml 为了报告更美观
 */

public class MyTestNG extends TestNG {

    XmlSuite xmlSuite = new XmlSuite();

}
