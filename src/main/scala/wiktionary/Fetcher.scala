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
      !e.children.head.innerHtml.contains(name)

    def secondPred(e: Element): Boolean = e.tagName != f"h$level" ||
      e.children.head.innerHtml.contains(name)

    val cutPreceding = in dropWhile firstPred
    if (cutPreceding.nonEmpty) cutPreceding.tail.takeWhile(secondPred)
    else Vector()
  }

  // splits on elements that DON'T fulfil p, and throws away those elements.
  def sectionSplit(v: Vector[Element], p: Element => Boolean): Vector[Vector[Element]] = {
    val (pre, suf) = v span p
    suf match {
      case Vector() => Vector(pre)
      case x +: xs => pre match {
        case Vector() => sectionSplit(xs, p)
        case _ => pre +: sectionSplit(xs, p)
      }
    }
  }

  def words(lookupWord: String): Vector[lang.Conjugation] = {
    val browser = JsoupBrowser()
    val url = f"$baseurl$lookupWord"
    try {
      val doc = browser.get(url)
      val mainContent = doc >> element("#mw-content-text > .mw-parser-output")
      println(lookupWord)
      val latinSection = getSectionByHeader(mainContent.children.toVector, "Latin", 2)
      if (latinSection.count(e => e.tagName == "h3" && e.children.head.innerHtml.contains("Etymology")) > 1)
        {
          val etymSections = sectionSplit(latinSection, e => (e.tagName != "h3") || !(e.children.head.innerHtml contains "Etymology"))
          val verbSections = etymSections.map(getSectionByHeader(_, "Verb", 4))
          val verbs = verbSections.flatMap(getVerbs(_, lookupWord))
          verbs
        }
      else {
        val verbSection = getSectionByHeader(latinSection, "Verb", 3)
        val verbs = getVerbs(verbSection, lookupWord)
        verbs

      }
        // split section into multiple etymologies
        // else  do what we always do
    }
    catch {
      case e: HttpStatusException => Vector()
    }
  }

  def getVerbs(v: Vector[Element], lookupWord: String): Vector[lang.Conjugation] = {
    // if: first element (p) has multiple children: then it is the wikipage with
    // conjugation table (ie first-person singular active indicative present)
    if (v.nonEmpty && v.head.children.size > 1)
      Vector(Conjugation(lookupWord, Person.First, Number.Singular, Tense.Present,
        Voice.Active, Mood.Indicative, lookupWord))
    else {
      val defs = v filter (_.tagName == "ol") map (_.children.head.children.head)
      defs.map(spanToConjugation(_, lookupWord))
    }
  }

  def spanToConjugation(span: Element, word: String): lang.Conjugation = {
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

