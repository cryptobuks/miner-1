##模块

####miner-parse：网页解析模块
####miner-store：数据存储模块
####miner-spider：爬虫公共模块
####miner-topo：storm topology模块

###任务定时执行采用开源的Quartz实现
Quartz使用类似于Linux下的Cron表达式定义时间规则，Cron表达式由6或7个由空格分隔的时间字段组成，如下：

| 位置        | 时间域名   |  允许值  |允许的特殊字符|
| -- |:  | :  |: |
| 1     | 秒       |   0-59      |, - * /        |
| 2     | 分钟     |   0-59      |, - * /        |
| 3     | 小时     |   0-23      |, - * /        |
| 4     | 日期     |   1-31      |, - * ? / L W C|
| 5     | 月份     |   1-12      |, - * /        |
| 6     | 星期     |   1-7       |, - * ? / L W #|
| 7     | 年(可选) |空值1970-2099|, - * /        |

Cron 触发器利用一系列特殊字符，如下所示：
* 反斜线（/）字符表示增量值。例如，在秒字段中“5/15”代表从第 5 秒开始，每 15 秒一次。
* 问号（?）字符和字母L字符只有在月内日期和周内日期字段中可用。问号表示这个字段不包含具体值。所以，如果指定月内日期，可以在周内日期字段中插入“?”，表示周内日期值无关紧要。字母L字符是last的缩写。放在月内日期字段中，表示安排在当月最后一天执行。在周内日期字段中，如果“L”单独存在，就等于“7”，否则代表当月内周内日期的最后一个实例。所以“0L”表示安排在当月的最后一个星期日执行。
* 在月内日期字段中的字母（W）字符把执行安排在最靠近指定值的工作日。把“1W”放在月内。 日期字段中，表示把执行安排在当月的第一个工作日内。
* 井号（#）字符为给定月份指定具体的工作日实例。把“MON#2”放在周内日期字段中，表示把任务安排在当月的第二个星期一。
* 星号（*）字符是通配字符，表示该字段可以接受任何可能的值。