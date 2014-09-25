package uk.co.turingatemyhamster.sbol2

import uk.co.turingatemyhamster.relations.{Relations, RelationsOps}
import uk.co.turingatemyhamster.datatree.Datatree

/**
 * Created by caroline on 23/08/2014.
 */
trait SBOL2Component_Sequence extends SBOL2Base {
  importedPackages : RelationsOps with SBOL2Component =>

  @RDFType(namespaceUri = "http://sbols.org/sbolv2/", prefix = "sbol2", localPart = "CutLocation")
  trait CutLocation extends Location {
    @RDFProperty(localPart = "after")
    def after: One[Int]
  }

  object CutLocation {
    implicit val propertyWomble: PropertyWomble[CutLocation] =
          BuilderMacro.propertyWomble[SBOL2Component_Sequence with SBOL2Component, CutLocation](importedPackages)
  }

  @RDFType(namespaceUri = "http://sbols.org/sbolv2/", prefix = "sbol2", localPart = "RangeLocation")
  trait RangeLocation extends Location {
    @RDFProperty(localPart = "start")
    def start: One[Int]
    @RDFProperty(localPart = "end")
    def end: One[Int]
  }

  object RangeLocation {
    implicit val propertyWomble: PropertyWomble[RangeLocation] =
          BuilderMacro.propertyWomble[SBOL2Component_Sequence with SBOL2Component, RangeLocation](importedPackages)
  }

  @RDFType(namespaceUri = "http://sbols.org/sbolv2/", prefix = "sbol2", localPart = "Oriented")
  trait Oriented {
    @RDFProperty(localPart = "orientation")
    def orientation: One[Orientation]
  }

  object Oriented {
    implicit val propertyWomble: PropertyWomble[Oriented] =
          BuilderMacro.propertyWomble[SBOL2Component_Sequence, Oriented](importedPackages)
  }

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

  @RDFType(namespaceUri = "http://sbols.org/sbolv2/", prefix = "sbol2", localPart = "Cut")
  case class Cut(identity: One[Uri],
                 persistentIdentity: ZeroOne[Uri] = ZeroOne(),
                 version: ZeroOne[String] = ZeroOne(),
                 timestamp:ZeroOne[Timestamp] = ZeroOne(),
                 annotations: ZeroMany[Annotation] = ZeroMany(),
                 after: One[Int])
    extends CutLocation

  object Cut {
    implicit val propertyWomble: PropertyWomble[Cut] =
          BuilderMacro.propertyWomble[SBOL2Component_Sequence, Cut](importedPackages)
  }

  @RDFType(namespaceUri = "http://sbols.org/sbolv2/", prefix = "sbol2", localPart = "OrientedCut")
  case class OrientedCut(identity: One[Uri],
                         persistentIdentity: ZeroOne[Uri] = ZeroOne(),
                         version: ZeroOne[String] = ZeroOne(),
                         timestamp:ZeroOne[Timestamp] = ZeroOne(),
                         annotations: ZeroMany[Annotation] = ZeroMany(),
                         after: One[Int],
                         orientation: One[Orientation])
    extends CutLocation with Oriented

  object OrientedCut {
    implicit val propertyWomble: PropertyWomble[OrientedCut] =
          BuilderMacro.propertyWomble[SBOL2Component_Sequence, OrientedCut](importedPackages)
  }

  @RDFType(namespaceUri = "http://sbols.org/sbolv2/", prefix = "sbol2", localPart = "Range")
  case class Range(identity: One[Uri],
                   persistentIdentity: ZeroOne[Uri] = ZeroOne(),
                   version: ZeroOne[String] = ZeroOne(),
                   timestamp:ZeroOne[Timestamp] = ZeroOne(),
                   annotations: ZeroMany[Annotation] = ZeroMany(),
                   start: One[Int],
                   end: One[Int])
    extends RangeLocation

  object Range {
    implicit val propertyWomble: PropertyWomble[Range] =
      BuilderMacro.propertyWomble[SBOL2Component_Sequence, Range](importedPackages)
  }


  @RDFType(namespaceUri = "http://sbols.org/sbolv2/", prefix = "sbol2", localPart = "OrientedRange")
  case class OrientedRange(identity: One[Uri],
                           persistentIdentity: ZeroOne[Uri] = ZeroOne(),
                           version: ZeroOne[String] = ZeroOne(),
                           timestamp:ZeroOne[Timestamp] = ZeroOne(),
                           annotations: ZeroMany[Annotation] = ZeroMany(),
                           start: One[Int],
                           end: One[Int],
                           orientation: One[Orientation])
    extends RangeLocation with Oriented

  object OrientedRange {
    implicit val propertyWomble: PropertyWomble[OrientedRange] =
      BuilderMacro.propertyWomble[SBOL2Component_Sequence, OrientedRange](importedPackages)
  }


  abstract override def nestedBuilders: Seq[NestedBuilder[Identified]] =
    super.nestedBuilders ++ Seq(
      BuilderMacro.nestedBuilder[SBOL2Component_Sequence, MultiRange](importedPackages),
      BuilderMacro.nestedBuilder[SBOL2Component_Sequence, Cut](importedPackages),
      BuilderMacro.nestedBuilder[SBOL2Component_Sequence, OrientedCut](importedPackages),
      BuilderMacro.nestedBuilder[SBOL2Component_Sequence, Range](importedPackages),
      BuilderMacro.nestedBuilder[SBOL2Component_Sequence, OrientedRange](importedPackages))
}
