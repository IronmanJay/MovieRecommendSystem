# MovieRecommendSystem
基于深度学习的监督学习，使用梯度下降、ALS、LFM算法，使用AngularJS2生成前端框架，数据库为MongoDB，使用ElasticSearch作为搜索服务器，Redis作为缓存数据库，其中包括Spark的离线统计服务、Azkaban的工作调度服务、Flume的日志采集服务、Kafka作为消息缓冲服务，全局采用Scala编写，Java作为Tomcat部署使用，实现离线推荐、实时推荐、服务器冷启动问题解决。
## 【项目环境】
Windows10、Centos7(三集群,三台都为3G，4核)、Idea2019.3、Maven3.3.9、Flume1.9、Tomcat8.5.23、Azkaban(自己编译)、elasticsearch5.6.2、kafka2.11-2.1.0、jce_policy8、node-v12.16.1、Postman、sacla2.1.18、jdk1.8、zookeeper3.4.10
## 【数据存储部分】 
业务数据库：项目采用广泛应用的文档数据库 MongDB 作为主数据库，责平台业务逻辑数据的存储。
搜索服务器：项目采用 ElasticSearch 作为模糊检索服务器，通过利用 ES 强大的匹配查询能力实现基于内容的推荐服务。 
缓存数据库：项目采用 Redis 作为缓存数据库，主要用来支撑实时推荐系统部分对于数据的高速获取需求。 
## 【离线推荐部分】 
离线统计服务：批处理统计性业务采用 Spark Core + Spark SQL 进行实现，实现对指标类数据的统计任务。 
离线推荐服务：离线推荐业务采用 Spark Core + Spark MLlib 进行实现，采用ALS 算法进行实现。 
工作调度服务：对于离线推荐部分需要以一定的时间频率对算法进行调度，采用 Azkaban 进行任务的调度。 
## 【实时推荐部分】 
日志采集服务：通过利用 Flume-ng 对业务平台中用户对于电影的一次评分行为进行采集，实时发送到 Kafka 集群。 
消息缓冲服务：项目采用 Kafka 作为流式数据的缓存组件，接受来自 Flume 的数据采集请求。并将数据推送到项目的实时推荐系统部分。 
实时推荐服务：项目采用 Spark Streaming 作为实时推荐系统，通过接收 Kafka中缓存的数据，通过设计的推荐算法实现对实时推荐的数据处理，并将结构合并更新到 MongoDB 数据库。 
## 【各模块划分】
DataLoader：数据加载模块，将数据加载到MongoDB和ElasticSearch。
StatisticsRecommender：离线统计模块，主要根据需求统计各类指标。
OfflineRecommender：主要是基于ALS的算法实现离线推荐。
StreamingRecommender：这部分是实时计算模块，达到实时推荐功能。
KafkaStream：对接Kafka和Flume，获取用户的实时评分数据，结合实时计算模块，给用户实时的推荐电影。
ContentRecommender：基于内容的推荐模块。