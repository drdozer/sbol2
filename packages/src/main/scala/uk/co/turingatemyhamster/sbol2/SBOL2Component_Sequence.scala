package uk.co.turingatemyhamster.sbol2

import uk.co.turingatemyhamster.cake.Relations
import uk.co.turingatemyhamster.datatree.Datatree

/**
 * Created by caroline on 23/08/2014.
 */
trait SBOL2Component_Sequence extends SBOL2Base {
  importedPackages : SBOL2Component =>

  @RDFType(namespaceURI = "http://sbols.org/sbolv2/", prefix = "sbol2", localPart = "CutLocation")
  trait CutLocation extends Location {
    @RDFProperty(localPart = "at")
    def at: One[Int]
  }

  object CutLocation {
    implicit val propertyWomble: PropertyWomble[CutLocation] =
          BuilderMacro.propertyWomble[SBOL2Component_Sequence with SBOL2Component, CutLocation](importedPackages)
  }

  @RDFType(namespaceURI = "http://sbols.org/sbolv2/", prefix = "sbol2", localPart = "RangeLocation")
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

  @RDFType(namespaceURI = "http://sbols.org/sbolv2/", prefix = "sbol2", localPart = "Oriented")
  trait Oriented {
    @RDFProperty(localPart = "orientation")
    def orientation: One[Orientation]
  }

  object Oriented {
    implicit val propertyWomble: PropertyWomble[Oriented] =
          BuilderMacro.propertyWomble[SBOL2Component_Sequence, Oriented](importedPackages)
  }

  @RDFType(namespaceURI = "http://sbols.org/sbolv2/", prefix = "sbol2", localPart = "MultiRange")
  case class MultiRange(identity: One[URI],
                        persistentIdentity: ZeroOne[URI] = ZeroOne(),
                        version: ZeroOne[String] = ZeroOne(),
                        timeStamp:ZeroOne[Timestamp] = ZeroOne(),
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
    implicit val enumToString: EnumToString[Orientation] = new EnumToString[Orientation] {
      override def toString(o: Orientation) = o match {
        case Inline => "inline"
        case ReverseComplement => "reverse_complement"
      }
    }
  }

  @RDFType(namespaceURI = "http://sbols.org/sbolv2/", prefix = "sbol2", localPart = "Cut")
  case class Cut(identity: One[URI],
                 persistentIdentity: ZeroOne[URI] = ZeroOne(),
                 version: ZeroOne[String] = ZeroOne(),
                 timeStamp:ZeroOne[Timestamp] = ZeroOne(),
                 annotations: ZeroMany[Annotation] = ZeroMany(),
                 at: One[Int])
    extends CutLocation

  object Cut {
    implicit val propertyWomble: PropertyWomble[Cut] =
          BuilderMacro.propertyWomble[SBOL2Component_Sequence, Cut](importedPackages)
  }

  @RDFType(namespaceURI = "http://sbols.org/sbolv2/", prefix = "sbol2", localPart = "OrientedCut")
  case class OrientedCut(identity: One[URI],
                         persistentIdentity: ZeroOne[URI] = ZeroOne(),
                         version: ZeroOne[String] = ZeroOne(),
                         timeStamp:ZeroOne[Timestamp] = ZeroOne(),
                         annotations: ZeroMany[Annotation] = ZeroMany(),
                         at: One[Int],
                         orientation: One[Orientation])
    extends CutLocation with Oriented

  object OrientedCut {
    implicit val propertyWomble: PropertyWomble[OrientedCut] =
          BuilderMacro.propertyWomble[SBOL2Component_Sequence, OrientedCut](importedPackages)
  }

  @RDFType(namespaceURI = "http://sbols.org/sbolv2/", prefix = "sbol2", localPart = "Range")
  case class Range(identity: One[URI],
                   persistentIdentity: ZeroOne[URI] = ZeroOne(),
                   version: ZeroOne[String] = ZeroOne(),
                   timeStamp:ZeroOne[Timestamp] = ZeroOne(),
                   annotations: ZeroMany[Annotation] = ZeroMany(),
                   start: One[Int],
                   end: One[Int])
    extends RangeLocation

  object Range {
    implicit val propertyWomble: PropertyWomble[Range] =
      BuilderMacro.propertyWomble[SBOL2Component_Sequence, Range](importedPackages)
  }


  @RDFType(namespaceURI = "http://sbols.org/sbolv2/", prefix = "sbol2", localPart = "OrientedRange")
  case class OrientedRange(identity: One[URI],
                           persistentIdentity: ZeroOne[URI] = ZeroOne(),
                           version: ZeroOne[String] = ZeroOne(),
                           timeStamp:ZeroOne[Timestamp] = ZeroOne(),
                           annotations: ZeroMany[Annotation] = ZeroMany(),
                           start: One[Int],
                           end: One[Int],
                           orientation: One[Orientation])
    extends RangeLocation with Oriented

  object OrientedRange {
    implicit val propertyWomble: PropertyWomble[OrientedRange] =
      BuilderMacro.propertyWomble[SBOL2Component_Sequence, OrientedRange](importedPackages)
  }


  abstract override def nestedBuilders: Seq[NestedBuilder[Any]] =
    super.nestedBuilders ++ Seq(
      BuilderMacro.nestedBuilder[SBOL2Component_Sequence, OrientedRange](importedPackages))

}
