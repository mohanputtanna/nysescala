package sparkstreaming

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.sql
import com.typesafe.config.ConfigFactory
import org.apache.spark.SparkConf
import org.apache.spark.sql.SQLContext
import org.apache.spark.streaming._
import org.apache.spark.streaming.StreamingContext._

object StreamLocal {
  
  def main(args:Array[String]){
    val conf = new SparkConf().setMaster("local").setAppName("STREAMWORDOUNT")
    val streamContext = new StreamingContext(conf,Seconds(2))
    val lines = streamContext.socketTextStream("localhost", 9999)
    
    val linesFlatMap = lines.flatMap { rec => (rec.split(" ")) }
    val linesMapWordCount = linesFlatMap.map { rec => (rec,1)}.reduceByKey((acc,value)=>(acc+value)) 
    linesMapWordCount.print()
    streamContext.start()
    streamContext.awaitTermination()
  }
  
}