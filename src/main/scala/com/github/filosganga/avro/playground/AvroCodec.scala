package com.github.filosganga.avro.playground
import com.github.filosganga.avro.playground.model._
import com.sksamuel.avro4s._
import org.apache.avro.generic.GenericRecord
import org.apache.avro.{Schema, SchemaBuilder}

trait AvroCodec {

  implicit def dishToSchema(implicit pastaSchemaFor: SchemaFor[Pasta], pizzaSchemaFor: SchemaFor[Pizza]): ToSchema[Dish] = new ToSchema[Dish] {
    override protected val schema: Schema = SchemaBuilder
      .unionOf()
      .`type`(pizzaSchemaFor())
      .and()
      .`type`(pastaSchemaFor())
      .endUnion()
  }


  implicit def dishToValue(implicit pastaToRecord: ToRecord[Pasta], pizzaToRecord: ToRecord[Pizza]): ToValue[Dish] = new ToValue[Dish] {
    override def apply(value: Dish): Any = value match {
      case p: Pasta => pastaToRecord(p)
      case p: Pizza => pizzaToRecord(p)
    }
  }

  implicit def dishFromValue(implicit pastaSchemaFor: SchemaFor[Pasta], pastaFromRecord: FromRecord[Pasta], pizzaFromRecord: FromRecord[Pizza], pizzaSchemaFor: SchemaFor[Pizza]): FromValue[Dish] = new FromValue[Dish] {
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
