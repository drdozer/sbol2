package uk.co.turingatemyhamster.sbol2

import uk.co.turingatemyhamster.web.WebOps
import uk.co.turingatemyhamster.relations.{Relations, RelationsOps}
import uk.co.turingatemyhamster.datatree.Datatree

/**
 * Created by caroline on 23/08/2014.
 */
trait SBOL2Component_Sequence extends SBOL2Base {
  importedPackages : WebOps with RelationsOps with SBOL2Component =>

  @RDFType(namespaceUri = "http://sbols.org/sbolv2/", prefix = "sbol2", localPart = "MultiRange")
  case class MultiRange(identity: One[Uri],
                        persistentIdentity: ZeroOne[Uri] = ZeroOne(),
                        version: ZeroOne[String] = ZeroOne(),
                        timestamp:ZeroOne[Timestamp] = ZeroOne(),
                        annotations: ZeroMany[Annotation] = ZeroMany(),

                        @RDFProperty(localPart = "range")
                        ranges: TwoMany[Range])
    extends Location

  object MultiRange {
    implicit val propertyWomble: PropertyWomble[MultiRange] =
          BuilderMacro.propertyWomble[SBOL2Component_Sequence with SBOL2Component, MultiRange](importedPackages)
  }

  sealed trait Orientation
  case object Inline extends Orientation
  case object ReverseComplement extends Orientation

  object Orientation {
    implicit val enumStringMapping: EnumStringMapping[Orientation] = new EnumStringMapping[Orientation] {
      override def toString(o: Orientation) = o match {
        case Inline => "inline"
        case ReverseComplement => "reverse_complement"
      }

      override def fromString(s: String): Orientation = s match {
        case "inline" => Inline
        case "reverse_complement" => ReverseComplement
      }
    }
  }

  @RDFType(namespaceUri = "http://sbols.org/sbolv2/", prefix = "sbol2", localPart = "OrientedLocation")
  trait OrientedLocation extends Location {
    @RDFProperty(localPart = "orientation")
    def orientation: ZeroOne[Orientation]
  }

  object OrientedLocation {
    implicit val propertyWomble: PropertyWomble[OrientedLocation] =
          BuilderMacro.propertyWomble[SBOL2Component_Sequence with SBOL2Component, OrientedLocation](importedPackages)
  }

  @RDFType(namespaceUri = "http://sbols.org/sbolv2/", prefix = "sbol2", localPart = "Cut")
  case class Cut(identity: One[Uri],
                 persistentIdentity: ZeroOne[Uri] = ZeroOne(),
                 version: ZeroOne[String] = ZeroOne(),
                 timestamp:ZeroOne[Timestamp] = ZeroOne(),
                 annotations: ZeroMany[Annotation] = ZeroMany(),
                 orientation: ZeroOne[Orientation],

                 @RDFProperty(localPart = "after")
                 after: One[Int])
    extends OrientedLocation

  object Cut {
    implicit val propertyWomble: PropertyWomble[Cut] =
          BuilderMacro.propertyWomble[SBOL2Component_Sequence, Cut](importedPackages)
  }

  @RDFType(namespaceUri = "http://sbols.org/sbolv2/", prefix = "sbol2", localPart = "Range")
  case class Range(identity: One[Uri],
                   persistentIdentity: ZeroOne[Uri] = ZeroOne(),
                   version: ZeroOne[String] = ZeroOne(),
                   timestamp:ZeroOne[Timestamp] = ZeroOne(),
                   annotations: ZeroMany[Annotation] = ZeroMany(),
                   orientation: ZeroOne[Orientation],

                   @RDFProperty(localPart = "start")
                   start: One[Int],
                   @RDFProperty(localPart = "end")
                   end: One[Int])
    extends OrientedLocation

  object Range {
    implicit val propertyWomble: PropertyWomble[Range] =
      BuilderMacro.propertyWomble[SBOL2Component_Sequence with SBOL2Component, Range](importedPackages)
  }

  abstract override def nestedBuilders: Seq[NestedBuilder[Identified]] =
    super.nestedBuilders ++ Seq(
      BuilderMacro.nestedBuilder[SBOL2Component_Sequence, MultiRange](importedPackages),
      BuilderMacro.nestedBuilder[SBOL2Component_Sequence, Cut](importedPackages),
      BuilderMacro.nestedBuilder[SBOL2Component_Sequence, Range](importedPackages))
}
