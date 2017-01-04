package nyse

import com.typesafe.config.ConfigFactory

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import java.math.BigInteger


object avgstockvolume {
  def main(args: Array[String]){
    
    val appConf = ConfigFactory.load()
    val conf = new SparkConf().setAppName("AvgVolume").setMaster(appConf.getString("deploymentMaster"))
    val sc = new SparkContext(conf)
    
  //  val inputPath = args(0);
   // val outputPath = args(1);
    
    val ordersRDD = sc.textFile("/home/mohan/projects/data/data-master/nyse/rawdata/nyse_20[0-9][1-2].csv")
    
   
    val ordersFilterBAC = ordersRDD.filter(rec=>((rec.split(",")(0).toString().equals("BAC"))||
        rec.split(",")(0).toString().equals("APL")))
    val ordersRDDMap = ordersFilterBAC.map(rec=>{
      ((rec.split(",")(0),rec.split(",")(1).substring(3, 11)),(rec.split(",")(6)))
    })
    
    
    val avgStockVolumeRDD = ordersRDDMap.aggregateByKey((0.0,0))(
        (acc,value)=>(acc._1+value.toLong, acc._2 + 1),
        (total1,total2)=>(total1._1+total2._1,total1._2+total2._2))
    val finalRDD = avgStockVolumeRDD.map(rec=>(rec._1,(rec._2._1/rec._2._2)))
    val sortingRDDMap = finalRDD.map(rec=>(rec._1._1,(rec._1._2,rec._2)))
    val finalSorted = finalRDD.sortByKey(true, 1)
    finalSorted.saveAsTextFile("/home/mohan/projects/data/nyseop")
  }
}