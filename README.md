# test
可以爬取 百度百科，好搜百科，互动百科的百科内容。

采用webMagic实现的，自己定义了PageProcessor，使用了作者写好的 FileCacheQueueScheduler，文件处理可以实现pipeline接口来定义自己输出格式，我是从自己的爬虫移植过来的代码，所以就直接在PageProcessor中处理了。

每一行是一个百科词条，使用 “\t” 分隔，属性名和属性之间使用 “:” 分隔。
 
