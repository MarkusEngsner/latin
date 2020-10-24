package cli

object App {

  def main(args: Array[String]): Unit = {
//    val clause = "ira Cupidinis Phoebo amorem dedit"
    val clause = "tracta atque comis antistita Phoebi non profectureas tendebat ad aethera palmas"
//    val clause = "palmas"
    val words = clause.split(' ').toVector map wiktionary.Fetcher.words
    val possiblePredicates = words filter (_.size > 0)
    println(words)
    println("Possible predicates:")
    if (possiblePredicates.size == 1) println(possiblePredicates.head)
    else println(possiblePredicates)
    // will have to try first word both with and without capitalization
//    val word = "responderis"
//    val result = wiktionary.Fetcher.words(word)
//    println(result)
  }

}
