# FinancialProtocolIntegration
Integration with existing automated trading system

Build notes:
1)	Project builds by gradle(http://www.gradle.org/). If it not installed on your PC:
a.	All scripts will be generated, by scripts (one of with gradlew(for windows - bat)). Scripts was generated according(https://www.gradle.org/docs/current/userguide/gradle_wrapper.html)
2)	During build you need call(run from command line): 
a.	gradle test – test all functions
b.	gradle integrationTest – performs integration testing 
c.	gradle build – build project, according 
d.	if you want build jar “all in one” call “gradle buildAllInOne”
3)	Test resources included in stream, in folders resources
4)	Structure of project: http://www.gradle.org/docs/current/userguide/java_plugin.html and https://maven.apache.org/guides/introduction/introduction-to-the-standard-directory-layout.html
Develop additional requirements:
	Integration
1)	Files with OrderBook can be more than one, all of them mast be processed one by one
2)	OrderBook and Messages with same TICKER, Year-Month-Day_StartTime_EndTime, LEVEL mast exists. If not – this orderbook is skipped
3)	During processing folder only client work with it
Requirements for File with messages
1)	Date and time – milliseconds after midnight

Requirements for File for OrderBook
1)	Bid and Ask – integers, can be (-9999999999 and 9999999999) or ( 0<Bid<9999999999, AKS 0<ASK<9999999999).
2)	Volume – int, and can be only >=0
3)	If Bid volume or Ask volume – this Price level is ignored
4)	
Additional requirements to Application Specs
1)	Submit and Send - same operations, Cansel order  - its mean Close Order( If Sell order - opened by Bid closed by Ask, if Buy order - opened by Ask, sel by BId)
2)	At on moment can be opend only too orders (one Sell and one Buy)
3) all Orders are created accoring com.OrdersProcessor#processOrderBookItem