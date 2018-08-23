package com.github.filosganga.avro.playground
import com.sksamuel.avro4s._
import org.apache.avro.generic.{GenericRecord, GenericRecordBuilder}
import org.apache.avro.{Schema, SchemaBuilder}

trait AvroCodec {

  implicit val pizzaSchemaFor: SchemaFor[Pizza] = new SchemaFor[Pizza] {

    override def apply(): Schema =
      SchemaBuilder
        .record(classOf[Pizza].getName)
        .fields()
        .requiredInt("size")
        .requiredString("flavour")
        .endRecord()
  }

  implicit val pizzaToRecord: ToRecord[Pizza] = new ToRecord[Pizza] {
    override def apply(t: Pizza): GenericRecord =
      new GenericRecordBuilder(pizzaSchemaFor())
        .set("size", t.size)
        .set("flavour", t.flavour)
        .build()
  }

  implicit val pizzaFromRecord: FromRecord[Pizza] = new FromRecord[Pizza] {
    override def apply(record: GenericRecord): Pizza =
      Pizza(
        size = record.get("size").asInstanceOf[Integer],
        flavour = record.get("flavour").toString
      )

  }

  implicit val pastaSchemaFor: SchemaFor[Pasta] = new SchemaFor[Pasta] {

    override def apply(): Schema =
      SchemaBuilder
        .record(classOf[Pasta].getName)
        .fields()
        .requiredInt("weight")
        .requiredString("format")
        .requiredString("seasoning")
        .endRecord()
  }

  implicit val pastaToRecord: ToRecord[Pasta] = new ToRecord[Pasta] {
    override def apply(t: Pasta): GenericRecord =
      new GenericRecordBuilder(pastaSchemaFor())
        .set("weight", t.weight)
        .set("format", t.format)
        .set("seasoning", t.seasoning)
        .build()
  }

  implicit val pastaFromRecord: FromRecord[Pasta] = new FromRecord[Pasta] {
    override def apply(record: GenericRecord): Pasta =
      Pasta(
        weight = record.get("weight").asInstanceOf[Integer],
        format = record.get("format").toString,
        seasoning = record.get("seasoning").toString
      )
  }

  implicit val dishSchemaFor: SchemaFor[Dish] = new SchemaFor[Dish] {
    override def apply(): Schema =
      SchemaBuilder
        .record(classOf[Dish].getName)
        .fields()
        .name("child")
        .`type`(
          SchemaBuilder
            .unionOf()
            .`type`(SchemaFor[Pizza]())
            .and()
            .`type`(SchemaFor[Pasta]())
            .endUnion()
        )
        .noDefault()
        .endRecord()

  }

  implicit val dishToRecord: ToRecord[Dish] = new ToRecord[Dish] {
    override def apply(t: Dish): GenericRecord =
      new GenericRecordBuilder(dishSchemaFor())
        .set("child", t match {
          case p: Pasta => pastaToRecord(p)
          case p: Pizza => pizzaToRecord(p)
        })
        .build()
  }

  implicit val dishFromRecord: FromRecord[Dish] = new FromRecord[Dish] {
    private val PizzaSchema = pizzaSchemaFor()
    private val PastaSchema = pastaSchemaFor()

    override def apply(record: GenericRecord): Dish = {
      val child = record.get("child").asInstanceOf[GenericRecord]
      child.getSchema match {
        case PizzaSchema => pizzaFromRecord(child)
        case PastaSchema => pastaFromRecord(child)
      }
    }
  }

}
