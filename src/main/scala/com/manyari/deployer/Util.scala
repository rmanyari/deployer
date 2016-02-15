package com.manyari.deployer

import java.util.Objects

import com.typesafe.scalalogging.LazyLogging

/**
  * Created by rodrigo on 2/1/16.
  */
object Util extends LazyLogging{

  def toInt(s: String): Option[Int] = {
    try{
      Option.apply(s.toInt)
    }catch {
      case e: Throwable =>
        logger.error(s"Caught an exception casting [%s] to Int", e);
        Option.empty
    }
  }

  def nonNull(objects: Object*) = objects.foldLeft(true)((x, y) => x && Objects.nonNull(y))

}
