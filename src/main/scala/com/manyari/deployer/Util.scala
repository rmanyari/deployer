package com.manyari.deployer

/**
  * Created by rodrigo on 2/1/16.
  */
object Util {

  def toInt(s: String): Option[Int] = {
    try{
      Option.apply(s.toInt)
    }catch {
      case _ => Option.empty
    }
  }

}
