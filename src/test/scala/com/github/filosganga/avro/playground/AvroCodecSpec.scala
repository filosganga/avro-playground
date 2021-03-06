package com.github.filosganga.avro.playground
import java.io.ByteArrayOutputStream

import com.github.filosganga.avro.playground.model._
import com.sksamuel.avro4s._
import org.apache.avro.Schema
import org.apache.avro.generic.{GenericDatumReader, GenericDatumWriter, GenericRecord}
import org.apache.avro.io.{DecoderFactory, EncoderFactory}
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{Matchers, WordSpec}

class AvroCodecSpec extends WordSpec with Matchers with AvroCodec with GeneratorDrivenPropertyChecks with Arbitraries {

  val expectedSchema = new Schema.Parser().parse("""
                                                   |{
                                                   |    "fields": [
                                                   |        {
                                                   |            "name": "id",
                                                   |            "type": "string"
                                                   |        },
                                                   |        {
                                                   |            "name": "dish",
                                                   |            "type": [
                                                   |                {
                                                   |                    "fields": [
                                                   |                        {
                                                   |                            "name": "size",
                                                   |                            "type": "int"
                                                   |                        },
                                                   |                        {
                                                   |                            "name": "flavour",
                                                   |                            "type": "string"
                                                   |                        }
                                                   |                    ],
                                                   |                    "name": "Pizza",
                                                   |                    "type": "record"
                                                   |                },
                                                   |                {
                                                   |                    "fields": [
                                                   |                        {
                                                   |                            "name": "weight",
                                                   |                            "type": "int"
                                                   |                        },
                                                   |                        {
                                                   |                            "name": "format",
                                                   |                            "type": "string"
                                                   |                        },
                                                   |                        {
                                                   |                            "name": "seasoning",
                                                   |                            "type": "string"
                                                   |                        }
                                                   |                    ],
                                                   |                    "name": "Pasta",
                                                   |                    "type": "record"
                                                   |                }
                                                   |            ]
                                                   |        }
                                                   |    ],
                                                   |    "name": "Order",
                                                   |    "namespace": "com.github.filosganga.avro.playground",
                                                   |    "type": "record"
                                                   |}
                                                 """.stripMargin)

  "AvroCoded" should {
    "generate correct schema" in {
      SchemaFor[Order]() shouldBe expectedSchema

    }

    "generate GenericRecord with the expected schema" in forAll { order: Order =>
      val record = ToRecord[Order](order)
      record.getSchema shouldBe expectedSchema
    }

    "be able to serialize and deserialize Order" in forAll { order: Order =>
      serializeAndDeserialize(order) shouldBe order
    }

  }

  def serializeAndDeserialize[A](
    a: A
  )(implicit schemaFor: SchemaFor[A], toRecord: ToRecord[A], fromRecord: FromRecord[A]): A = {

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
