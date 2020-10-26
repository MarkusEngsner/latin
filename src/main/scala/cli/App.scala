package cli

import io.getquill._
import lang._
import org.postgresql.ds.PGSimpleDataSource

object App {
  def main(args: Array[String]): Unit = {
//    val clause = "ira Cupidinis Phoebo amorem dedit"
//    val clause = "tracta atque comis antistita Phoebi non profectureas tendebat ad aethera palmas"
//    val clause = "palmas"
    val clause = "esse"
    val cache = new cacher.Cache()
    val words = clause.split(' ').toVector map cache.getWord
    val possiblePredicates = words filter (_.nonEmpty)
    println(words)
    println("Possible predicates:")
    if (possiblePredicates.size == 1) println(possiblePredicates.head)
    else println(possiblePredicates)

//    lazy val ctx = new PostgresJdbcContext(SnakeCase, "ctx")
//    import ctx._
//    val q = quote {
//      query[lang.Conjugation].filter(_.name == "dedit")
//    }
//    possiblePredicates.head.head match {
//      case c: Conjugation => ctx.run(query[Conjugation].insert(lift(c)))
//    }
//    val result = ctx.run(q)
//    println(result)
    // will have to try first word both with and without capitalization
//    val word = "responderis"
//    val result = wiktionary.Fetcher.words(word)
//    println(result)
  }

}
