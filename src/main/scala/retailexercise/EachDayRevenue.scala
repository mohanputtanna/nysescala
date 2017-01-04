package retailexercise

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.sql
import com.typesafe.config.ConfigFactory
import org.apache.spark.SparkConf
import org.apache.spark.sql.SQLContext
import org.apache.hadoop.fs.FileSystem
import org.apache.hadoop.fs.Path
import caseclass.Orders
import caseclass.OrderItems



object EachDayRevenue{
  def main(args:Array[String]){
    val appconf = ConfigFactory.load()
    val conf = new SparkConf().setAppName("EachDayRevenue").setMaster(appconf.getString("deploymentMaster"))
    
    val sc = new SparkContext(conf)
    val sqlContext = new SQLContext(sc)
    sqlContext.setConf("spark.sql.shuffle.partitions", "3")
    
    import sqlContext.implicits._ 
    
     val inputPath = "/home/mohan/projects/data/data-master/retail_db"
     val outputPath = "/home/mohan/projects/data/eachdayrevenue/"
     
    
    val fs = FileSystem.get(sc.hadoopConfiguration)
    val inputPathExists = fs.exists(new Path(inputPath))
    val outputPathExists = fs.exists(new Path(outputPath))
    
     if (!inputPathExists) {
      println("Input Path does not exists")
      return
    }

    if (outputPathExists) {
      fs.delete(new Path(outputPath), true)
    }
    //read the textfile orders 
    //create a dataframe by reading each line and splitting it into case class Orders Type
    val ordersDF = sc.textFile(inputPath + "/orders").
    map ( rec => {
      val a = rec.split(",")
      Orders(a(0).toInt,a(1).toString(),a(2).toInt,a(3).toString())
    }).toDF()
    //read the textfile order_items
    //create a dataframe by reading each line and splitting it into case class order_items Type
    val orderItemsDF = sc.textFile(inputPath + "/order_items").
      map(rec => {
        val a = rec.split(",")
        OrderItems(a(0).toInt,a(1).toInt,a(2).toInt,a(3).toInt,a(4).toFloat,a(5).toFloat)
      }).toDF()
     //from the orders DF selects only completed orders
    val completedOrders = ordersDF.filter(ordersDF("order_status")==="COMPLETE")
    //join only completed orders DF with order_items DF on  order_ID in orders and order_items_order_id
    val ordersJoin = completedOrders.join(orderItemsDF, completedOrders("order_id") === orderItemsDF("order_item_order_id"))
    
   val finalData = ordersJoin.groupBy("order_date").sum("order_item_subtotal").sort("order_date").rdd.saveAsTextFile(outputPath)
    
  }
}