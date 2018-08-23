package com.github.filosganga.avro.playground
import java.io.ByteArrayOutputStream

import com.sksamuel.avro4s._
import org.apache.avro.Schema
import org.apache.avro.generic.{GenericDatumReader, GenericDatumWriter, GenericRecord}
import org.apache.avro.io.{DecoderFactory, EncoderFactory}
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{Matchers, WordSpec}

class AvroCodecSpec extends WordSpec with Matchers with AvroCodec with GeneratorDrivenPropertyChecks with Arbitraries {

  "AvroCoded" should {
    "generate correct schema" in {
      val expectedSchema = new Schema.Parser().parse("""
          |{
          |  "type" : "record",
          |  "name" : "Dish",
          |  "namespace" : "com.github.filosganga.avro.playground",
          |  "fields" : [ {
          |    "name" : "child",
          |    "type" : [ {
          |      "type" : "record",
          |      "name" : "Pizza",
          |      "fields" : [ {
          |        "name" : "size",
          |        "type" : "int"
          |      }, {
          |        "name" : "flavour",
          |        "type" : "string"
          |      } ]
          |    }, {
          |      "type" : "record",
          |      "name" : "Pasta",
          |      "fields" : [ {
          |        "name" : "weight",
          |        "type" : "int"
          |      }, {
          |        "name" : "format",
          |        "type" : "string"
          |      }, {
          |        "name" : "seasoning",
          |        "type" : "string"
          |      } ]
          |    } ]
          | } ]
          | }
        """.stripMargin)

      dishSchemaFor() shouldBe expectedSchema

    }

    "be able to serialize and deserialize" in forAll { dish: Dish =>
      serializeAndDeserialize(dish) shouldBe dish
    }

  }

  def serializeAndDeserialize[A](a: A)(implicit schemaFor: SchemaFor[A], toRecord: ToRecord[A], fromRecord: FromRecord[A]): A = {

    val schema = schemaFor()

    val bout = new ByteArrayOutputStream()
    val encoder = EncoderFactory.get().binaryEncoder(bout, null)
    val writer = new GenericDatumWriter[GenericRecord](schema)

    writer.write(toRecord(a), encoder)
    encoder.flush()

    val decoder = DecoderFactory.get().binaryDecoder(bout.toByteArray, null)
    val reader = new GenericDatumReader[GenericRecord](schema)
    val result = fromRecord(reader.read(null.asInstanceOf[GenericRecord], decoder))

    result
  }

}
