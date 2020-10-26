package wiktionary

import lang._
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.model.Element
import org.jsoup.HttpStatusException


// gets relevant parts of wiktionary entry.
object Fetcher {

  val baseurl = "https://en.wiktionary.org/wiki/"

  def words(lookupWord: String): Vector[Verb] = {
    val browser = JsoupBrowser()
    val url = f"$baseurl$lookupWord"
    try {
      val doc = browser.get(url)
      val wikiTrees = WikiTree.parsePage(doc)
      val wikiMap = wikiTrees.map(w => w.header -> w).toMap
      println(lookupWord)
      val latinSection = wikiMap("Latin")
      val defSection =
        if (latinSection.children.exists(_.header.contains("Etymology")))
          for {
            etymSection <- latinSection.children.filter(_.header.contains("Etymology"))
            sections <- etymSection.children
          } yield sections
        else latinSection.children
      for {
        verbSection <- defSection filter (_.header.contains("Verb"))
        verb <- getVerbs(verbSection.directChildren, lookupWord)
      } yield verb
    }
    catch {
      case e: HttpStatusException => Vector()
    }
  }

  def getVerbs(v: Vector[Element], lookupWord: String): Vector[Verb] = {
    // if: first element (p) has multiple children: then it is the wikipage with
    // conjugation table (ie first-person singular active indicative present)
    if (v.nonEmpty && v.head.children.size > 1)
      Vector(FiniteVerb(lookupWord, Person.First, Number.Singular, Tense.Present,
        Voice.Active, Mood.Indicative, lookupWord))
    else {
      val defs = v filter (_.tagName == "ol") map (_.children.head.children.head)
      defs.map(spanToConjugation(_, lookupWord))
    }
  }

  private def isInfinitive(props: Vector[String]): Boolean = {
    props.exists(_.toLowerCase.contains("infinitive"))
  }

  //  private def parseInfinitive(name: String, base: String, props: Vector[String])
  //  : Infinitive = {
  //    val
  //
  //  }

  def mapProperty[A](properties: Vector[String], mapping: Map[String, A]): A = {
    val matches = properties.filter(mapping.contains)
    if (matches.size == 1) mapping(matches.head)
    else throw new IllegalArgumentException
  }

  def spanToConjugation(span: Element, word: String): Verb = {
    // all elements except last contain keywords (information about conjugation)
    val props = span.children.init.toVector map (_.innerHtml)
    val base = span.children.last.children.head.children.head.innerHtml
    if (props.contains("infinitive"))
      Infinitive(word, mapProperty(props, ConjugationMapping.tense), base)
    else FiniteVerb(
      word,
      mapProperty(props, ConjugationMapping.person),
      mapProperty(props, ConjugationMapping.number),
      mapProperty(props, ConjugationMapping.tense),
      mapProperty(props, ConjugationMapping.voice),
      mapProperty(props, ConjugationMapping.mood),
      base
    )
  }


}

object ConjugationMapping {
  val number: Map[String, Number] = Map("singular" -> Number.Singular, "plural" ->
    Number.Plural)

  val person: Map[String, Person] = Map("first-person" -> Person.First,
    "second-person" -> Person.Second,
    "third-person" -> Person.Third
  )

  val tense: Map[String, Tense] = Map(
    "present" -> Tense.Present,
    "imperfect" -> Tense.Imperfect,
    "future" -> Tense.Future,
    "perfect" -> Tense.Perfect,
    "pluperfect" -> Tense.PluPerfect,
    "future perfect" -> Tense.FuturePerfect
  )

  val voice: Map[String, Voice] = Map("active" -> Voice.Active, "passive" -> Voice
    .Passive)

  val mood: Map[String, Mood] = Map(
    "indicative" -> Mood.Indicative,
    "subjunctive" -> Mood.Subjunctive,
    "imperative" -> Mood.Imperative
  )



}

