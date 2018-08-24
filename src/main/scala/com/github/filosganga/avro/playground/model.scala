package com.github.filosganga.avro.playground

object model {

  case class Order(id: String, dish: Dish)

  sealed trait Dish

  case class Pizza(size: Int, flavour: String) extends Dish

  case class Pasta(weight: Int, format: String, seasoning: String) extends Dish

}