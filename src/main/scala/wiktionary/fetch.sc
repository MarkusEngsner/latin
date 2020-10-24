import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL.Parse._
import net.ruippeixotog.scalascraper.model.Element

val browser = JsoupBrowser()

val query = "erat"
//val baseurl = "https://en.wiktionary.org/w/index.php"
val baseurl = "https://en.wiktionary.org/wiki/"
val url = f"$baseurl$query"
//val url = f"$baseurl?q=$query"

val doc = browser.get(url)



val mainContent = doc >> element("#mw-content-text > .mw-parser-output")

val dropFirst = mainContent.children.dropWhile((e: Element) =>
  !(e.tagName == "h2" && e.children.head.attr("id") == "Latin")
)

dropFirst.head.attrs

val onlyLatin = dropFirst.tail.takeWhile(_.tagName != "h2").toVector

mainContent.children.size
dropFirst.size
onlyLatin.size

/* verb instances */
// case class VerbInflection(mood, person, voice(active / passive), baseVerb, tense

val verbSection = (onlyLatin.dropWhile((e: Element) =>
  !(e.tagName == "h3" && e.children.head.attr("id") == "Verb")
)).tail.takeWhile(_.tagName != "h2") // should check for either h2 or h3

// base: lemma case class
//case class LemmaVerb
case class VerbForm(word: String, person: String, number: String, tense: String, voice: String, mood: String, base: String)

def spanToVerbForm(span: Element, word: String): VerbForm = {
  println(span.children.toVector(5))
  val props = span.children.toVector map (_.innerHtml)
  println(span.children.size)
  println(props.size)
  val base = span.children.last.children.head.children.head.innerHtml
  VerbForm(word, props(0), props(1), props(2), props(3), props(4), base)
}

//val defs = verbSection >> element(".form-of-definition")
val defs = (verbSection filter (_.tagName == "ol")) map (_.children.head.children.head)
defs.size
defs.head
val sp = defs.head
sp.tagName
spanToVerbForm(sp, query)
//sp.children
//val tables = doc >> elementList(".inflection-table")

//val table = tables.head
//table.children
//table.childNodes
//tables.map(_ >> allText("th"))

// h4 id: #Conjugation
// table class: ".inflection-table"


// "Daphne erat primus amor Phoebi"