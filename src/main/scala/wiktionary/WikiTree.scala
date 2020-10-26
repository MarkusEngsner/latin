package wiktionary

import net.ruippeixotog.scalascraper.model.Document
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.model.Element


abstract class WikiTree(val header: String, val self: Element)

final case class WikiLeaf(override val header: String, override val self: Element,
                          elements: Vector[Element]) extends WikiTree(header, self)

final case class WikiNode(override val header: String, override val self: Element,
                          children: Vector[WikiTree],
                          directChildElements: Vector[Element])
  extends WikiTree(header, self)


object WikiTree {

  private def headerFilter(level: Int)(e: Element): Boolean = e.tagName != f"h$level"

  // Note: headerLevel of self (same as parseTree)
  def parseNode(self: Element, v: Vector[Element], headerLevel: Int): WikiNode = {
    val (directContent, childContent) = v span headerFilter(headerLevel + 1)
    val headerText = self.children.head.innerHtml
    val children = if (childContent.isEmpty) Vector()
    else parseTree(childContent, headerLevel + 1)
    WikiNode(headerText, self, children, directContent)
  }

  def parseTree(page: Vector[Element], headerLevel: Int): Vector[WikiTree] = {
    val (currentContent, nextContent) = page.tail span headerFilter(headerLevel)
    val currentNode = parseNode(page.head, currentContent, headerLevel)
    nextContent match {
      case Vector() => Vector(currentNode)
      case _ => currentNode +: parseTree(nextContent, headerLevel)
    }
  }

  // removes everything upto but not including the first language header
  def removePreamble(e: Element): Vector[Element] = {
    val withoutPreamble = e.children.dropWhile(headerFilter(2))
    withoutPreamble.toVector
  }

  def parsePage(doc: Document): Vector[WikiTree] = {
    val mainContent = doc >> element("#mw-content-text > .mw-parser-output")
    val removedToC = removePreamble(mainContent)
    parseTree(removedToC, 2)
  }
}
