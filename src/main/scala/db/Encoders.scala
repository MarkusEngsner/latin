package db

import lang._
import io.getquill.MappedEncoding


object Encoders {
  implicit val number = MappedEncoding[Number, String](_.toString)
  implicit val person = MappedEncoding[Person, String](_.toString)
  implicit val tense = MappedEncoding[Tense, String](_.toString)
  implicit val voice = MappedEncoding[Voice, String](_.toString)
  implicit val mood = MappedEncoding[Mood, String](_.toString)
}

object Decoders {
  implicit val number = MappedEncoding[String, Number](Number.fromString)
  implicit val person = MappedEncoding[String, Person](Person.fromString)
  implicit val tense =  MappedEncoding[String, Tense](Tense.fromString)
  implicit val voice =  MappedEncoding[String, Voice](Voice.fromString)
  implicit val mood =   MappedEncoding[String, Mood](Mood.fromString)
}
