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

val z = Vector(1, 2, 3)
z.appended(5)
z
import play.api.libs.json.Json
val value: JsValue = Json.toJson(Vector[Map[String, Int]](Map("x" -> 0, "y" -> 1), Map("x" -> 0, "y" -> 2)))
value.as[Vector[Map[String, Int]]]