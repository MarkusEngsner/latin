package cacher
import io.getquill._
import lang._

case class QueryResult(verbs: Vector[Conjugation])

class Cache {
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

  lazy val ctx = new PostgresJdbcContext(SnakeCase, "ctx")



  def getWord(s: String): Vector[Word] = {
    import ctx._
    val q = quote {
      query[Conjugation].filter(_.name == lift(s))
    }
    val conjMatches: List[Conjugation] = ctx.run(q)
    if (conjMatches.nonEmpty) conjMatches.toVector
    else {
      val result = fetchWord(s)
      addToDB(result)
      result
    }
    // if in db
    // return
  }

  def fetchWord(s: String): Vector[Conjugation] = {
    wiktionary.Fetcher.words(s)
  }

  def addToDB(v: Vector[Conjugation]): Unit = {
    import ctx._
    val qInsert = quote {
      liftQuery(v).foreach(c => query[Conjugation].insert(c))
    }
    ctx.run(qInsert)
  }

}
