package stackoverflow

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.sql
import org.apache.spark.streaming.twitter
import org.apache.spark.streaming.flume._
import com.typesafe.config.ConfigFactory
import org.apache.spark.SparkConf
import org.apache.spark.sql.SQLContext
import org.apache.hadoop.fs.FileSystem
import org.apache.hadoop.fs.Path
import caseclass.Orders
import caseclass.OrderItems
import java.time.LocalDate 
import java.time.format.DateTimeFormatter

object DataToListRDD {

	def main(args:Array[String]){

		val appConf = ConfigFactory.load()
				val conf = new SparkConf().setAppName("TopNStocks").setMaster(appConf.getString("deploymentMaster"))
				val sc = new SparkContext(conf)
		
				val inputPath = "/home/mohan/projects/data/data-master/sflow"
				val outputPath = "/home/mohan/projects/data/data-master/sflow/op"

				val fs = FileSystem.get(sc.hadoopConfiguration)
				val inputPathExists = fs.exists(new Path(inputPath))
				val outputPathExists = fs.exists(new Path(outputPath))
				if(!inputPathExists) {
					println("Invalid input path")
					return
				}

		if(outputPathExists)
			fs.delete(new Path(outputPath), true)

	 def getDateDifference(dateStr:String):Int = {
					val startDate = "01-01-2016" 
							val formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy")
							val oldDate = LocalDate.parse(startDate, formatter)
							val currentDate = dateStr
							val newDate = LocalDate.parse(currentDate, formatter)
							return newDate.toEpochDay().toInt - oldDate.toEpochDay().toInt
		}

		def getArray(numberofDays:Int,data:Long):Iterable[Long] = {

				val daysArray = new Array[Long](366)
						daysArray(numberofDays) = data
						return daysArray
		}

		val idRDD = sc.textFile(inputPath)
		val idSingleDateRDD = 	idRDD.map { rec => ((rec.split(",")(0),rec.split(",")(1)),rec.split(",")(2).toLong) }.reduceByKey(_+_)

		val idfinalMapRDD = idSingleDateRDD.map(rec=>((rec._1._1,rec._1._2),(getDateDifference(rec._1._2),rec._2)))
//		val idRDDMap = idRDD.map { rec => ((rec.split(",")(0),rec.split(",")(1)),
//						(getDateDifference(rec.split(",")(1)),rec.split(",")(2).toInt))}
		val idRDDconsiceMap = idfinalMapRDD.map { rec => (rec._1._1,getArray(rec._2._1, rec._2._2)) }
	
		//	val finalRDD = idRDDconsiceMap.reduceByKey((acc,value)=>(acc+value))









	}
}