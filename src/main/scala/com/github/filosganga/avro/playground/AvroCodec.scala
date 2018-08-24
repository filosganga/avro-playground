package com.github.filosganga.avro.playground
import com.sksamuel.avro4s._
import org.apache.avro.generic.{GenericRecord, GenericRecordBuilder}
import org.apache.avro.{Schema, SchemaBuilder}

trait AvroCodec {

  implicit lazy val pizzaSchemaFor: SchemaFor[Pizza] = new SchemaFor[Pizza] {

    override def apply(): Schema =
      SchemaBuilder
        .record(classOf[Pizza].getName)
        .fields()
        .requiredInt("size")
        .requiredString("flavour")
        .endRecord()
  }

  implicit lazy val pizzaToRecord: ToRecord[Pizza] = new ToRecord[Pizza] {
    override def apply(t: Pizza): GenericRecord =
      new GenericRecordBuilder(pizzaSchemaFor())
        .set("size", t.size)
        .set("flavour", t.flavour)
        .build()
  }

  implicit lazy val pizzaFromRecord: FromRecord[Pizza] = new FromRecord[Pizza] {
    override def apply(record: GenericRecord): Pizza =
      Pizza(size = record.get("size").asInstanceOf[Integer], flavour = record.get("flavour").toString)

  }

  implicit lazy val pastaSchemaFor: SchemaFor[Pasta] = new SchemaFor[Pasta] {

    override def apply(): Schema =
      SchemaBuilder
        .record(classOf[Pasta].getName)
        .fields()
        .requiredInt("weight")
        .requiredString("format")
        .requiredString("seasoning")
        .endRecord()
  }

  implicit lazy val pastaToRecord: ToRecord[Pasta] = new ToRecord[Pasta] {
    override def apply(t: Pasta): GenericRecord =
      new GenericRecordBuilder(pastaSchemaFor())
        .set("weight", t.weight)
        .set("format", t.format)
        .set("seasoning", t.seasoning)
        .build()
  }

  implicit lazy val pastaFromRecord: FromRecord[Pasta] = new FromRecord[Pasta] {
    override def apply(record: GenericRecord): Pasta =
      Pasta(
        weight = record.get("weight").asInstanceOf[Integer],
        format = record.get("format").toString,
        seasoning = record.get("seasoning").toString
      )
  }

  implicit lazy val dishToSchema: ToSchema[Dish] = new ToSchema[Dish] {
    override protected val schema: Schema = SchemaBuilder
      .unionOf()
      .`type`(SchemaFor[Pizza]())
      .and()
      .`type`(SchemaFor[Pasta]())
      .endUnion()
  }


  implicit lazy val dishToValue: ToValue[Dish] = new ToValue[Dish] {
    override def apply(value: Dish): Any = value match {
      case p: Pasta => pastaToRecord(p)
      case p: Pizza => pizzaToRecord(p)
    }
  }

  implicit lazy val dishFromValue: FromValue[Dish] = new FromValue[Dish] {
    private val PizzaSchema = pizzaSchemaFor()
    private val PastaSchema = pastaSchemaFor()

    override def apply(value: Any, field: Schema.Field): Dish = {
      val record = value.asInstanceOf[GenericRecord]
      record.getSchema match {
        case PizzaSchema => pizzaFromRecord(value.asInstanceOf[GenericRecord])
        case PastaSchema => pastaFromRecord(value.asInstanceOf[GenericRecord])
      }
    }
  }

}
