package lang

trait Word {
  val name: String
}


case class Conjugation(override val name: String, person: String, number: String, tense: String,
                    voice: String, mood: String, base: String) extends Word

