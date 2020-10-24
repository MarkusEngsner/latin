package lang

trait Word {
  val name: String
}

sealed trait Number

case object Singular extends Number

case object Plural extends Number


sealed trait Person

case object FirstPerson extends Person

case object SecondPerson extends Person

case object ThirdPerson extends Person


sealed trait Tense

case object Present extends Tense

case object Imperfect extends Tense

case object FutureTense extends Tense

case object Perfect extends Tense

case object Pluperfect extends Tense

case object FuturePerfect extends Tense


sealed trait Voice

case object Active extends Voice

case object Passive extends Voice


sealed trait Mood

case object Indicative extends Mood

case object Subjunctive extends Mood

case object Imperative extends Mood


case class Conjugation(override val name: String, person: Person, number: Number,
                       tense: Tense,
                       voice: Voice, mood: Mood, base: String) extends Word

