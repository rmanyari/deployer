package com.manyari.deployer

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import com.typesafe.config.{Config, ConfigFactory}
import com.typesafe.scalalogging.LazyLogging
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._
import spray.can.Http

/**
  * Created by Rodrigo Manyari on 1/31/2016.
  */
object Application extends App with ContainerApplication {

  val configuration = extractConfiguration()

  val finalConfig = configuration match {
    case None =>
      logger.info("Could not find Container configuration, running in standard mode listening on 2551")
      ConfigFactory.parseString("akka.remote.netty.tcp.port=" + 2551).withFallback(ConfigFactory.load())
    case Some(config) =>
      logger.info("Starting in container mode")
      config.withFallback(ConfigFactory.load())
  }

  implicit val system = ActorSystem(System.getProperty("clusterName"), finalConfig)
  system.actorOf(Props[ClusterListener], name="ClusterListener")

  val restService = system.actorOf(Props[RestServiceActor], "RestServiceActor")
  implicit val timeout = Timeout(5.seconds)
  IO(Http) ? Http.Bind(restService, interface ="0.0.0.0", port=8080)

}

trait ContainerApplication extends LazyLogging {

  def extractConfiguration(): Option[Config] = {

    val hostname      = System.getProperty("hostname")
    val port          = System.getProperty("port")
    val bindHostname  = System.getProperty("bindHostname")
    val bindPort      = System.getProperty("bindPort")
    val clusterName   = System.getProperty("clusterName")
    val seeds         = System.getProperty("seeds")

    val seedNodes = Util.nonNull(clusterName, seeds) match {
      case true => seeds.split(" ").map(x => "\"" + "akka.tcp://" + clusterName + "@" + x + "\"").mkString(",\n")
      case false => logger.warn("Invalid seed-nodes configuration. Not joining a cluster"); ""
    }

    if(Util.nonNull(hostname, port, bindHostname, bindPort)){
      logger.info(s"Found arguments [$hostname] [$port] [$bindHostname] [$bindPort]")
      Some(ConfigFactory.parseString(
        s"""
          akka{
            remote{
              netty.tcp{
                hostname      = $hostname
                port          = $port
                bind-hostname = $bindHostname
                bind-port     = $bindPort
              }
            }

          cluster{
            seed-nodes = [
              $seedNodes
            ]
          }
        }"""
      ))
    }else{
      None
    }
  }

}