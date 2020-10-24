package wiktionary

import lang._
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL.Parse._
import net.ruippeixotog.scalascraper.model.Element
import org.jsoup.HttpStatusException


// gets relevant parts of wiktionary entry.
object Fetcher {

  val baseurl = "https://en.wiktionary.org/wiki/"

  // in: Vector of elements from which to start
  // name: header that introduces the section
  // level: h1, h2, h3... etc which delimits the section
  // returns: elements between header elements
  def getSectionByHeader(in: Vector[Element], name: String, level: Int)
  : Vector[Element] = {
    def firstPred(e: Element): Boolean = e.tagName != f"h$level" ||
      e.children.head.attr("id") != name

    def secondPred(e: Element): Boolean = e.tagName != f"h$level" ||
      e.children.head.attr("id") == name

    val cutPreceding = in dropWhile firstPred
    if (cutPreceding.nonEmpty) cutPreceding.tail.takeWhile(secondPred)
    else Vector()
  }


  def words(lookupWord: String): Vector[lang.Word] = {
    val browser = JsoupBrowser()
    val url = f"$baseurl$lookupWord"
    try {
      val doc = browser.get(url)
      val mainContent = doc >> element("#mw-content-text > .mw-parser-output")
      println(lookupWord)
      val latinSection = getSectionByHeader(mainContent.children.toVector, "Latin", 2)
      println("checking verb")
      val verbSection = getSectionByHeader(latinSection, "Verb", 3)
      println("getSectionByHeader successful")
      val verbs = getVerbs(verbSection, lookupWord)
      verbs
    }
    catch {
      case e: HttpStatusException => Vector()
    }
  }

  def getVerbs(v: Vector[Element], lookupWord: String): Vector[lang.Word] = {
    val defs = v filter (_.tagName == "ol") map (_.children.head.children.head)
    defs.map(spanToConjugation(_, lookupWord))
  }

  def spanToConjugation(span: Element, word: String): lang.Word = {
    val props = span.children.toVector map (_.innerHtml)
    val base = span.children.last.children.head.children.head.innerHtml
    lang.Conjugation(
      word,
      ConjugationMapping.person(props(0)),
      ConjugationMapping.number(props(1)),
      ConjugationMapping.tense(props(2)),
      ConjugationMapping.voice(props(3)),
      ConjugationMapping.mood(props(4)),
      base
    )
  }


}

object ConjugationMapping {
  val number: Map[String, Number] = Map("singular" -> Singular, "plural" -> Plural)

  val person: Map[String, Person] = Map("first-person" -> FirstPerson,
    "second-person" -> SecondPerson,
    "third-person" -> ThirdPerson
  )

  val tense: Map[String, Tense] = Map(
    "present" -> Present,
    "imperfect" -> Imperfect,
    "future" -> FutureTense,
    "perfect" -> Perfect,
    "pluperfect" -> Pluperfect,
    "future perfect" -> FuturePerfect
  )

  val voice: Map[String, Voice] = Map("active" -> Active, "passive" -> Passive)

  val mood: Map[String, Mood] = Map(
    "indicative" -> Indicative,
    "subjunctive" -> Subjunctive,
    "imperative" -> Imperative
  )
}

