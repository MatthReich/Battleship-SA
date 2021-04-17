import play.api.libs.json.JsValue.jsValueToJsLookup
import play.api.libs.json.{JsValue, Json}

import scala.util.Try

val shipArray: Vector[Map[String, Int]] = Vector(
  Map("x" -> 0, "y" -> 0, "value" -> 1),
  Map("x" -> 0, "y" -> 1, "value" -> 1),
  Map("x" -> 0, "y" -> 2, "value" -> 1)
)

shipArray.updated(0, shipArray(0) + ("value" -> 0))

val x = Try("1".toInt)
val y = Try("c".toInt)

for (ship <- shipArray) yield ship.get("x") match {
  case Some(value) => println(ship, value)
  case _ =>
}
val test = Json.toJson(Vector[Map[String, Int]](Map("x" -> 0, "y" -> 1), Map("x" -> 0, "y" -> 2)))

val z = Vector(1, 2, 3)
z.appended(5)
z


import play.api.libs.json.Json

val vector = Vector[Map[String, Int]](Map("x" -> 0, "y" -> 1, "value" -> 7), Map("x" -> 0, "y" -> 2, "value" -> 5))
val value: JsValue = Json.toJson(vector)
val string = value.toString()

def function1(string: String): Unit = {
  println(string)
  jsValueToJsLookup(Json.toJson(value)).result.toOption match {
    case Some(value) => println(value.as[Vector[Map[String, Int]]])
    case None => println("lel")
  }
}
function1(string)
println("findish")

