
import scala.reflect._
import com.google.inject.Guice
import com.google.sitebricks.client._
import com.google.sitebricks.client.transport._


val URL = "http://www.newzbin.com/search/?go=1&ss=425562&fpn=p&feed=rss&hauth=1"

// Grab page from url
val response = Guice.createInjector()
     .getInstance(classOf[Web])
     .clientOf(URL)
     .auth(Web.Auth.BASIC, "dhanji@gmail.com", "Z|P81P+e%-x*0rEar~yDDApxo")
     .transports(classOf[String])
     .over(classOf[Text])
     .get()

println(response)