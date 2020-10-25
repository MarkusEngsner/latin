package cli

import io.getquill._
import lang._
import org.postgresql.ds.PGSimpleDataSource

object App {
  implicit val number = MappedEncoding[Number, String](_.toString)
  implicit val person = MappedEncoding[Person, String](_.toString)
  implicit val tense = MappedEncoding[Tense, String](_.toString)
  implicit val voice = MappedEncoding[Voice, String](_.toString)
  implicit val mood = MappedEncoding[Mood, String](_.toString)

  implicit val deNumber = MappedEncoding[String, Number](Number.fromString(_))
  implicit val dePerson = MappedEncoding[String, Person](Person.fromString(_))
  implicit val deTense =  MappedEncoding[String, Tense](Tense.fromString(_))
  implicit val deVoice =  MappedEncoding[String, Voice](Voice.fromString(_))
  implicit val deMood =   MappedEncoding[String, Mood](Mood.fromString(_))

  def main(args: Array[String]): Unit = {
//    val clause = "ira Cupidinis Phoebo amorem dedit"
//    val clause = "tracta atque comis antistita Phoebi non profectureas tendebat ad aethera palmas"
//    val clause = "palmas"
    val clause = "dedit"
    val words = clause.split(' ').toVector map wiktionary.Fetcher.words
    val possiblePredicates = words filter (_.size > 0)
    println(words)
    println("Possible predicates:")
    if (possiblePredicates.size == 1) println(possiblePredicates.head)
    else println(possiblePredicates)

    lazy val ctx = new PostgresJdbcContext(SnakeCase, "ctx")
    import ctx._
    val q = quote {
      query[lang.Conjugation]
    }
//    possiblePredicates.head.head match {
//      case c: Conjugation => ctx.run(query[Conjugation].insert(lift(c)))
//    }
    val result = ctx.run(q)
    println(result)
    // will have to try first word both with and without capitalization
//    val word = "responderis"
//    val result = wiktionary.Fetcher.words(word)
//    println(result)
  }

}
