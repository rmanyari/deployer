package com.manyari.deployer

import akka.actor.Actor
import spray.routing.HttpService

/**
  * Created by rodrigo on 2/14/16.
  */
class RestServiceActor extends Actor with RestService{

  def actorRefFactory = context

  def receive = runRoute(route)

}

trait RestService extends HttpService{

  val route =
    path("") {
      get{
          complete{
            "Hello world!"
          }
      }
    }

}
