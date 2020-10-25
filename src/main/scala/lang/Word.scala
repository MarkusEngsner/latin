package lang

trait Word {
  val name: String
}

sealed trait Number extends Product with Serializable

object Number {

  final case object Singular extends Number

  final case object Plural extends Number

  val all = Seq(Singular, Plural)

  def fromString(s: String): Number =
    all.find(_.toString == s).getOrElse(throw new IllegalArgumentException)


}


sealed trait Person extends Product with Serializable

object Person {

  final case object First extends Person

  final case object Second extends Person

  final case object Third extends Person

  val all = Seq(First, Second, Third)

  def fromString(s: String): Person =
    all.find(_.toString == s).getOrElse(throw new IllegalArgumentException)
}


sealed trait Tense extends Product with Serializable

object Tense {

  final case object Present extends Tense

  final case object Imperfect extends Tense

  final case object Future extends Tense

  final case object Perfect extends Tense

  final case object PluPerfect extends Tense

  final case object FuturePerfect extends Tense

  val all = Seq(Present, Imperfect, Future, Perfect, PluPerfect, FuturePerfect)

  def fromString(s: String): Tense =
    all.find(_.toString == s).getOrElse(throw new IllegalArgumentException)

}


sealed trait Voice extends Product with Serializable

object Voice {

  final case object Active extends Voice

  final case object Passive extends Voice

  val all = Seq(Active, Passive)

  def fromString(s: String): Voice =
    all.find(_.toString == s).getOrElse(throw new IllegalArgumentException)

}


sealed trait Mood extends Product with Serializable

object Mood {

  final case object Indicative extends Mood

  final case object Subjunctive extends Mood

  final case object Imperative extends Mood

  val all = Seq(Indicative, Subjunctive, Imperative)

  def fromString(s: String): Mood =
    all.find(_.toString == s).getOrElse(throw new IllegalArgumentException)

}


case class Conjugation(override val name: String, person: Person, number: Number,
                       tense: Tense,
                       voice: Voice, mood: Mood, base: String) extends Word

