package sparkstreaming

//import org.apache.spark.{SparkConf,SparkContext}
//import org.apache.spark.SparkContext._
//import com.typesafe.config.ConfigFactory
//import org.apache.spark.SparkConf
//import org.apache.spark.streaming.Seconds
//import org.apache.spark.streaming.StreamingContext
//import org.apache.spark.streaming.twitter.TwitterUtils
//import org.apache.spark.streaming.twitter._
//import org.apache.spark.storage.StorageLevel
//import org.apache.spark.streaming.flume._
import com.typesafe.config.ConfigFactory
import org.apache.spark.streaming.{ Seconds, StreamingContext }
import org.apache.spark.SparkContext._
import org.apache.spark.streaming.twitter._
import org.apache.spark.streaming._
import org.apache.spark.{ SparkContext, SparkConf }
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.flume._

object TwitterStream {
  def main(args:Array[String]){
    val appConf = ConfigFactory.load()
    val conf = new SparkConf().setMaster(appConf.getString("deploymentMaster")).setAppName("STREAMTWITTER	")
    val sc = new SparkContext(conf)
    
    sc.setLogLevel("ERROR")
    
    System.setProperty("twitter4j.oauth.consumerKey", appConf.getString("consumnerKey"))
    System.setProperty("twitter4j.oauth.consumerSecret", appConf.getString("consumerSecret"))
    System.setProperty("twitter4j.oauth.accessToken", appConf.getString("accessToken"))
    System.setProperty("twitter4j.oauth.accessTokenSecret", appConf.getString("accessTokenSecret"))
    
    val streamSC = new StreamingContext(sc,Seconds(5))
    
    val filters = Array("new year")
    
    val stream = TwitterUtils.createStream(streamSC, None, filters);
    
    val hashTags = stream.flatMap(rec => rec.getText.split(" "))
    
    hashTags.foreachRDD(rdd => {println(rdd)
    })
    
    
    stream.print()
    
    streamSC.start()
    streamSC.awaitTermination()
    
    
    
    
  }
  
  
}