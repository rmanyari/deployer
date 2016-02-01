package com.manyari.deployer

import akka.actor.{ActorLogging, Actor}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent._

/**
  * Created by rodrigo on 2/1/16.
  */
class ClusterListener extends Actor with ActorLogging {

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