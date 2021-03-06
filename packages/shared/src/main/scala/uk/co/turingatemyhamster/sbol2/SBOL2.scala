package uk.co.turingatemyhamster.sbol2

import uk.co.turingatemyhamster.web.WebOpsImpl
import uk.co.turingatemyhamster.relations.RelationsOpsScalaImpl

import scala.language.implicitConversions

/**
 * A full implementation of SBOL2 with default bindings to scala datatypes.
 *
 * @author Matthew Pocock
 */
object SBOL2 extends SBOL2Base
with SBOL2BaseOps
with WebOpsImpl
with RelationsOpsScalaImpl
with SBOL2Collection
with SBOL2Component
with SBOL2Component_Sequence
with SBOL2Model
with SBOL2Module
with SBOL2Generic
{
  implicit def toUriReference[I <: Identified](i: I): UriReference[I] = UriReference[I](i.identity)

  def identifiersOrg(path: String): Uri = Uri(s"http://identifiers.org/$path")
  def chebi(chebiId: String): Uri = identifiersOrg(s"chebi/CHEBI:$chebiId")
  def so(soId: String): Uri = identifiersOrg(s"so/SO:$soId")
  def go(goId: String): Uri = identifiersOrg(s"go/GO:$goId")
  def sbo(sboId: String): Uri = identifiersOrg(s"sbo/SBO:$sboId")

  val dna = chebi("16991")
  val protein = chebi("36080")
  val organicMolecule = chebi("72695")
  val metabolite = chebi("78675")

  val region = so("0000110")
  val cds = so("0000316")
  val promoter = so("0000167")
  val terminator = so("0000141")

  val ligandRegulatedTF = go("0098531")
  val complex = go("0032991")

  val geneticProduction = sbo("0000589")
  val product = sbo("0000011")
  val inhibitor = sbo("0000020")
  val binding = sbo("0000177")
  val reactant = sbo("0000010")
  val interactor = sbo("0000336")

  val precedes = Uri("http://sbolstandard.org/constaints/precedes")

  def SubComponent(baseUri: Uri,
                   displayId: String,
                   access: AccessModifier,
                   instantiatedComponent: ComponentDefinition) = Component(
    identity = Uri(baseUri.uriString + "/subComponent/" + displayId),
    access = access,
    instantiatedComponent = instantiatedComponent
  )

  def FunctionalComponentOf(baseUri: Uri,
                            displayId: String,
                            access: AccessModifier,
                            instantiatedComponent: ComponentDefinition,
                            direction: Direction) = FunctionalComponent(
    identity = Uri(baseUri.uriString + "/instantiatedComponent/" + displayId),
    displayId = Some(displayId),
    access = access,
    instantiatedComponent = instantiatedComponent,
    direction = direction)

  implicit def idToTurtle(id: Identified): TurtleValue = NestedValue(id)
  implicit def stringToTurtle(s: String): TurtleValue = StringValue(s)
  implicit def booleanToTurtle(b: Boolean): TurtleValue = BooleanValue(b)
  implicit def uriToTurtle(uri: Uri): TurtleValue = UriValue(uri)

  implicit class QNameExtras(val qname: QName) {
    def := (tv: TurtleValue): Annotation = Annotation(relation = qname, value = tv)
    def apply(as: Annotation*): StructuredAnnotation = StructuredAnnotation(
      `type` = qname,
      annotations = as
    )
  }
}
