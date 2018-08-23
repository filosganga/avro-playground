package com.github.filosganga.avro.playground
import org.scalacheck.{Gen, Arbitrary}, Gen._, Arbitrary._

trait Arbitraries {

  implicit lazy val arbPasta: Arbitrary[Pasta] = Arbitrary(for {
    weight <- choose(30, 250)
    format <- oneOf("Penne", "Fusilli", "Spaghetti", "Linguine")
    seasoning <- oneOf("Carbonara", "Amatriciana", "Ragù")
  } yield Pasta(weight, format, seasoning))


  implicit lazy val arbPizza: Arbitrary[Pizza] = Arbitrary(for {
    size <- choose(12, 22)
    flavour <- oneOf("Margherita", "Capricciosa", "Marinara", "Bufalina")
  } yield Pizza(size, flavour))


  implicit lazy val arbDish: Arbitrary[Dish] =
    Arbitrary(oneOf(arbitrary[Pasta], arbitrary[Pizza]))
}
