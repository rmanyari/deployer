package com.manyari.deployer

import akka.actor.{ActorLogging, Props, ActorSystem, Actor}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent._
import com.typesafe.config.ConfigFactory

/**
  * Created by Rodrigo Manyari on 1/31/2016.
  */
class HelloActor extends Actor {

  def receive = {
    case "hello"  => println("Yo!")
    case _        => println("woot, who are you?!")
  }

}

class SimpleClusterListener extends Actor with ActorLogging {

  val cluster = Cluster(context.system)

  override def preStart(): Unit = {
    cluster.subscribe(self, initialStateMode = InitialStateAsEvents, classOf[MemberEvent], classOf[UnreachableMember])
  }

  override def postStop(): Unit = {
    cluster.unsubscribe(self)
  }

  def receive = {
    case MemberUp(member)           =>
      log.info("Member {} is up", member.address)
    case UnreachableMember(member)  =>
      log.info("Member {} is unreachable", member.address)
    case MemberRemoved(member, previousStatus)      =>
      log.info("Member {} is removed after {}", member.address, previousStatus)
    case _: MemberEvent => // Ignore
  }

}

object Main extends App {

  val config = ConfigFactory.parseString("akka.remote.netty.tcp.port=" + 5000)
    .withFallback(ConfigFactory.load())

  val system = ActorSystem("ClusterSystem", config)

  system.actorOf(Props[SimpleClusterListener], name="ClusterListener")

}

