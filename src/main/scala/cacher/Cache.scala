package cacher

import io.getquill._
import lang._

case class QueryResult(verbs: Vector[FiniteVerb])

class Cache {
  implicit val number = MappedEncoding[Number, String](_.toString)
  implicit val person = MappedEncoding[Person, String](_.toString)
  implicit val tense = MappedEncoding[Tense, String](_.toString)
  implicit val voice = MappedEncoding[Voice, String](_.toString)
  implicit val mood = MappedEncoding[Mood, String](_.toString)

  implicit val deNumber = MappedEncoding[String, Number](Number.fromString)
  implicit val dePerson = MappedEncoding[String, Person](Person.fromString)
  implicit val deTense = MappedEncoding[String, Tense](Tense.fromString)
  implicit val deVoice = MappedEncoding[String, Voice](Voice.fromString)
  implicit val deMood = MappedEncoding[String, Mood](Mood.fromString)

  lazy val ctx = new PostgresJdbcContext(SnakeCase, "ctx")


  def getWord(s: String): Vector[Word] = {
    import ctx._
    val q = quote {
      query[FiniteVerb].filter(_.name == lift(s))
    }
    val conjMatches: List[FiniteVerb] = ctx.run(q)
    if (conjMatches.nonEmpty) conjMatches.toVector
    else {
      val result = fetchWord(s)
      addToDB(result)
      result
    }
    // if in db
    // return
  }

  def fetchWord(s: String): Vector[Verb] = {
    wiktionary.Fetcher.words(s)
  }

//  def filterType[A <: Word](v: Vector[Word]): Unit = {
//    val filtered: Vector[A] = v collect {case a: A => a}
//    import ctx._
//    val q = quote {
//      liftQuery(filtered) foreach (query[A].insert(_))
//    }
//    ctx.run(q)
//  }

  def addToDB(v: Vector[Verb]): Unit = {
//    filterType[Infinitive](v)
//    filterType[FiniteVerb](v)
    import ctx._
    val infinitives = v collect {case i: Infinitive => i}
    ctx.run(quote {
      liftQuery(infinitives) foreach (query[Infinitive].insert(_))
    })
    val finites = v collect {case f: FiniteVerb => f}
    ctx.run(quote {
      liftQuery(finites) foreach (query[FiniteVerb].insert(_))
    })
//    val qInsert = quote {
//      liftQuery(v) foreach {
//        case i: Infinitive => query[Infinitive].insert(i)
//        case f: FiniteVerb => query[FiniteVerb].insert(f)
//      }
//    }
//    ctx.run(qInsert)
  }

}
