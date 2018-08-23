package com.github.filosganga.avro.playground

sealed trait Dish

case class Pizza(size: Int, flavour: String) extends Dish 

case class Pasta(weight: Int, format: String, seasoning: String) extends Dish