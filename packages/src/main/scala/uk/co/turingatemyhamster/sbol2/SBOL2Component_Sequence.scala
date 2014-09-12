package uk.co.turingatemyhamster.sbol2

import uk.co.turingatemyhamster.cake.Relations
import uk.co.turingatemyhamster.datatree.Datatree

/**
 * Created by caroline on 23/08/2014.
 */
trait SBOL2Component_Sequence extends SBOL2Base {
  importedPackages : SBOL2Component =>

  trait CutLocation extends Location {
    def at: One[Int]
  }

  trait RangeLocation extends Location {
    def start: One[Int]
    def end: One[Int]
  }

  trait Oriented {
    def orientation: One[Orientation]
  }

  case class MultiRange(identity: One[URI],
                        persistentIdentity: ZeroOne[URI] = ZeroOne(),
                        version: ZeroOne[String] = ZeroOne(),
                        timeStamp:ZeroOne[Timestamp] = ZeroOne(),
                        annotations: ZeroMany[Annotation] = ZeroMany(),
                        ranges: TwoMany[Range])
    extends Location


  sealed trait Orientation
  case object Inline extends Orientation
  case object ReverseComplement extends Orientation

  case class Cut(identity: One[URI],
                 persistentIdentity: ZeroOne[URI] = ZeroOne(),
                 version: ZeroOne[String] = ZeroOne(),
                 timeStamp:ZeroOne[Timestamp] = ZeroOne(),
                 annotations: ZeroMany[Annotation] = ZeroMany(),
                 at: One[Int])
    extends CutLocation

  case class OrientedCut(identity: One[URI],
                         persistentIdentity: ZeroOne[URI] = ZeroOne(),
                         version: ZeroOne[String] = ZeroOne(),
                         timeStamp:ZeroOne[Timestamp] = ZeroOne(),
                         annotations: ZeroMany[Annotation] = ZeroMany(),
                         at: One[Int],
                         orientation: One[Orientation])
    extends CutLocation with Oriented

  case class Range(identity: One[URI],
                   persistentIdentity: ZeroOne[URI] = ZeroOne(),
                   version: ZeroOne[String] = ZeroOne(),
                   timeStamp:ZeroOne[Timestamp] = ZeroOne(),
                   annotations: ZeroMany[Annotation] = ZeroMany(),
                   start: One[Int],
                   end: One[Int])
    extends RangeLocation


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

  object Orientation {
    implicit val enumToString: EnumToString[Orientation] = new EnumToString[Orientation] {
      override def toString(o: Orientation) = o match {
        case Inline => "inline"
        case ReverseComplement => "reverse_complement"
      }
    }
  }

  object OrientedRange {

    implicit val propertyWomble = new PropertyWomble[OrientedRange] {
      def asProperties[DT <: Datatree with Relations]
      (dt: DT, or: OrientedRange)
      (implicit implicits: Implicits[dt.URI, dt.Name, dt.PropertyValue]): Seq[dt.NamedProperty] =
      {
        import implicits._
        val ph = propertyHelper(dt)

        implicitly[PropertyWomble[Identified]].asProperties(dt, or) ++
          ph.asProperty("sbol2" -> "start", or.start.seq) ++
          ph.asProperty("sbol2" -> "end", or.end.seq) ++
          ph.asProperty("sbol2" -> "orientation", or.orientation.seq)
      }
    }

  }


  abstract override def nestedBuilders: Seq[NestedBuilder[Any]] =
    super.nestedBuilders ++ Seq(
      BuilderMacro.nestedBuilder[SBOL2Component_Sequence, OrientedRange](importedPackages))

}
