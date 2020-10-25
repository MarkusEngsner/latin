package lang

trait Word {
  val name: String
}

sealed trait Number extends Product with Serializable

object Number {

  final case object Singular extends Number

  final case object Plural extends Number

}


sealed trait Person extends Product with Serializable

object Person {

  final case object FirstPerson extends Person

  final case object SecondPerson extends Person

  final case object ThirdPerson extends Person

}


sealed trait Tense extends Product with Serializable

object Tense {

  final case object Present extends Tense

  final case object Imperfect extends Tense

  final case object FutureTense extends Tense

  final case object Perfect extends Tense

  final case object Pluperfect extends Tense

  final case object FuturePerfect extends Tense

}


sealed trait Voice extends Product with Serializable

object Voice {

  final case object Active extends Voice

  final case object Passive extends Voice

}


sealed trait Mood extends Product with Serializable

object Mood {

  final case object Indicative extends Mood

  final case object Subjunctive extends Mood

  final case object Imperative extends Mood

}


case class Conjugation(override val name: String, person: Person, number: Number,
                       tense: Tense,
                       voice: Voice, mood: Mood, base: String) extends Word

