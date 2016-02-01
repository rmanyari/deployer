package com.manyari.deployer

import akka.actor.{ActorSystem, Props}
import com.manyari.deployer.Util._
import com.typesafe.config.{Config, ConfigFactory}
import com.typesafe.scalalogging.LazyLogging

/**
  * Created by Rodrigo Manyari on 1/31/2016.
  */
object Application extends App with ContainerApplication with LazyLogging {

  val configuration = extractConfiguration(args)

  val finalConfig = configuration match {
    case None =>
      logger.warn("Could not find Container configuration, running in standard mode")
      ConfigFactory.parseString("akka.remote.netty.tcp.port=" + 2551).withFallback(ConfigFactory.load())
    case Some(config) =>
      logger.info("Starting the application with the following arguments: {}", args.mkString(" "))
      config.withFallback(ConfigFactory.load())
  }

  val system = ActorSystem("ClusterSystem", finalConfig)
  system.actorOf(Props[ClusterListener], name="ClusterListener")

}

trait ContainerApplication{

  def extractConfiguration(args: Array[String]): Option[Config] = {
    if(args.length < 4){
      None
    }else{
      val hostname = args{0}
      val port = toInt(args{1})
      val bindHostname = args{2}
      val bindPort = toInt(args{3})

      if(port.isEmpty || bindPort.isEmpty){
        None
      }else{
        Some(ConfigFactory.parseString(
          s"akka.remote.netty.tcp.hostname=$hostname, " +
          s"port=$port, " +
          s"bind-hostname=$bindHostname, " +
          s"bind-port=$bindPort")
        )
      }
    }
  }

}