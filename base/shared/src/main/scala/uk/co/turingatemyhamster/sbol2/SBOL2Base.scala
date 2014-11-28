package uk.co.turingatemyhamster.sbol2

import uk.co.turingatemyhamster.web.{Web, WebOps}
import uk.co.turingatemyhamster.relations.{Relations, RelationsOps}
import uk.co.turingatemyhamster.datatree.Datatree

/**
 * SBOL2 base package.
 *
 * This package provides types shared by all SBOL packages.
 *
 * @group
 * @author Matthew Pocock
 */
abstract class SBOL2Base extends Web with Relations {
  importedPackages : WebOps with RelationsOps =>

  lazy val TheBNodeUri = Uri("_")

  /**
   * The abstract Timestamp type.
   *
   * @group AbstractApi
   */
  type Timestamp

  val Timestamp: TimestampApi

  trait TimestampApi {
    def apply(ts: String): Timestamp
  }

  case class UriReference[T](ref: Uri)

  @RDFType(namespaceUri = "http://sbols.org/sbolv2/", prefix = "sbol2", localPart = "Identified")
  trait Identified {
    @RDFSkip
    def identity: One[Uri]
    @RDFProperty(localPart = "persistentIdentity")
    def persistentIdentity: ZeroOne[Uri]
    @RDFProperty(localPart = "version")
    def version: ZeroOne[String]
    @RDFProperty(localPart = "timestamp")
    def timestamp: ZeroOne[Timestamp]
    @RDFSkip
    def annotations: ZeroMany[Annotation]
  }

  case class Annotation( relation: One[QName],
                         value: One[TurtleValue])

  sealed trait TurtleValue {
    type Value
    def value: Value
  }

  case class StringValue(value: String) extends TurtleValue {
    type Value = String
  }

  case class IntegerValue(value: Int) extends TurtleValue {
    type Value = Int
  }

  case class DoubleValue(value: Double) extends TurtleValue {
    type Value = Double
  }

  case class BooleanValue(value: Boolean) extends TurtleValue {
    type Value = Boolean
  }

  case class UriValue(value: Uri) extends TurtleValue {
    type Value = Uri
  }

  case class TypedValue(value: String, xsdType: String) extends TurtleValue {
    type Value = String
  }

  case class NestedValue(value: Identified) extends TurtleValue {
    type Value = Identified
  }

  @RDFType(namespaceUri = "http://sbols.org/sbolv2/", prefix = "sbol2", localPart = "Documented")
  trait Documented extends Identified {
    @RDFProperty(localPart = "displayId")
    def displayId: ZeroOne[String]
    @RDFProperty(localPart = "name")
    def name: ZeroOne[String]
    @RDFProperty(localPart = "description")
    def description: ZeroOne[String]
  }

  @RDFType(namespaceUri = "http://sbols.org/sbolv2/", prefix = "sbol2", localPart = "TopLevel")
  trait TopLevel extends Documented

  case class SBOLDocument(namespaceBindings: Seq[NamespaceBinding] = Seq(), contents: ZeroMany[TopLevel] = ZeroMany())


  // Companions. These should largely go away once we have macro generation of io boilerplate

  object Identified {

    implicit val propertyWomble: PropertyWomble[Identified] = new PropertyWomble[Identified] {
      def asProperties[DT <: Datatree with WebOps with RelationsOps]
      (dt: DT, i: Identified)
      (implicit implicits: ToImplicits[dt.Uri, dt.QName, dt.PropertyValue]): Seq[dt.NamedProperty] = {
        import implicits._
        val ph = toPropertyHelper(dt)
        ph.asProperty("sbol2" -> "persistentIdentity", i.persistentIdentity.seq) ++
          ph.annotations(i.annotations.seq) ++
          ph.asProperty("sbol2" -> "version", i.version.seq) ++
          ph.asProperty("sbol2" -> "timestamp", i.timestamp.seq)
      }

      override def readProperty[DT <: Datatree with WebOps with RelationsOps, T]
      (dt: DT)(implicit implicits: FromImplicits[dt.Uri, dt.QName, dt.PropertyValue], tpv: dt.PropertyValue => T)
      : PartialFunction[(dt.Document, String), Seq[T]] = {
        import implicits._
        val ph = fromPropertyHelper(dt)

        {
          case (doc: dt.Document, "persistentIdentity") =>
            ph.fetchProperty(doc, "sbol2" -> "persistentIdentity")
          case (doc: dt.Document, "version") =>
            ph.fetchProperty(doc, "sbol2" -> "version")
          case (doc: dt.Document, "timestamp") =>
            ph.fetchProperty(doc, "sbol2" -> "timestamp")
        }
      }

      override def collectAllProperties[DT <: Datatree with WebOps with RelationsOps]
          (dt: DT)(implicit implicits: FromImplicits[dt.Uri, dt.QName, dt.PropertyValue]): Set[dt.QName] =
      {
        import implicits._

        Set(
          ("sbol2" -> "persistentIdentity"): dt.QName,
          ("sbol2" -> "version"): dt.QName,
          ("sbol2" -> "timestamp"): dt.QName)
      }
    }

  }

  object Documented {

    implicit val propertyWomble = new PropertyWomble[Documented] {
      def asProperties[DT <: Datatree with WebOps with RelationsOps]
      (dt: DT, d: Documented)
      (implicit implicits: ToImplicits[dt.Uri, dt.QName, dt.PropertyValue]): Seq[dt.NamedProperty] = {
        import implicits._
        val ph = toPropertyHelper(dt)

        implicitly[PropertyWomble[Identified]].asProperties(dt, d) ++
          ph.asProperty("sbol2" -> "displayId", d.displayId.seq) ++
          ph.asProperty("sbol2" -> "name", d.name.seq) ++
          ph.asProperty("sbol2" -> "description", d.description.seq)
      }

      override def readProperty[DT <: Datatree with WebOps with RelationsOps, T]
      (dt: DT)(implicit implicits: FromImplicits[dt.Uri, dt.QName, dt.PropertyValue], tpv: dt.PropertyValue => T)
      : PartialFunction[(dt.Document, String), Seq[T]] = {
        import implicits._
        val ph = fromPropertyHelper(dt)

        {
          val lookup: PartialFunction[(dt.Document, String), Seq[T]] = {
            case (doc: dt.Document, "displayId") =>
              ph.fetchProperty(doc, "sbol2" -> "displayId")
            case (doc: dt.Document, "name") =>
              ph.fetchProperty(doc, "sbol2" -> "name")
            case (doc: dt.Document, "description") =>
              ph.fetchProperty(doc, "sbol2" -> "description")
          }
          lookup
        } orElse implicitly[PropertyWomble[Identified]].readProperty(dt)
      }

      override def collectAllProperties[DT <: Datatree with WebOps with RelationsOps]
          (dt: DT)
          (implicit implicits: FromImplicits[dt.Uri, dt.QName, dt.PropertyValue])
          : Set[dt.QName] =
      {
        import implicits._

        implicitly[PropertyWomble[Identified]].collectAllProperties(dt) ++ Seq(
          ("sbol2" -> "displayId"): dt.QName,
          ("sbol2" -> "name"): dt.QName,
          ("sbol2" -> "description"): dt.QName)
      }
    }
  }

  object TopLevel {
    implicit val propertyWomble = new PropertyWomble[TopLevel] {
      def asProperties[DT <: Datatree with WebOps with RelationsOps]
      (dt: DT, tl: TopLevel)
      (implicit implicits: ToImplicits[dt.Uri, dt.QName, dt.PropertyValue]): Seq[dt.NamedProperty] = {
        implicitly[PropertyWomble[Documented]].asProperties(dt, tl)
      }

      override def readProperty[DT <: Datatree with WebOps with RelationsOps, T]
      (dt: DT)(implicit implicits: FromImplicits[dt.Uri, dt.QName, dt.PropertyValue], tpv: dt.PropertyValue => T)
      : PartialFunction[(dt.Document, String), Seq[T]] = implicitly[PropertyWomble[Documented]].readProperty(dt)

      override def collectAllProperties[DT <: Datatree with WebOps with RelationsOps]
          (dt: DT)
          (implicit implicits: FromImplicits[dt.Uri, dt.QName, dt.PropertyValue])
          : Set[dt.QName] =
      {
        implicitly[PropertyWomble[Documented]].collectAllProperties(dt)
      }
    }
  }

  // IO utility stuff

  trait EnumStringMapping[T] {
    def toString(t: T): String
    def fromString(s: String): T
  }

  trait PropertyWomble[I] {
    def asProperties[DT <: Datatree with WebOps with RelationsOps]
    (dt: DT, i: I)(implicit implicits: ToImplicits[dt.Uri, dt.QName, dt.PropertyValue]): Seq[dt.NamedProperty]

    def readProperty[DT <: Datatree with WebOps with RelationsOps, T]
    (dt: DT)
    (implicit implicits: FromImplicits[dt.Uri, dt.QName, dt.PropertyValue], tpv: dt.PropertyValue => T):
    PartialFunction[(dt.Document, String), Seq[T]]

    def collectAllProperties[DT <: Datatree with WebOps with RelationsOps]
    (dt: DT)(implicit implicits: FromImplicits[dt.Uri, dt.QName, dt.PropertyValue]): Set[dt.QName]

    final def readAnnotations[DT <: Datatree with WebOps with RelationsOps]
    (dt: DT)
    (doc: dt.Document, skip: Set[dt.QName])
    (implicit implicits: FromImplicits[dt.Uri, dt.QName, dt.PropertyValue]): Seq[Annotation] = {
      val ph = fromPropertyHelper(dt)
      ph.annotations(dt.zeroManyOps.seq(doc.properties).filterNot(p => skip contains dt.oneOps.theOne(p.name)))
    }
  }

  final def nesteds[DT <: Datatree with WebOps with RelationsOps](dt: DT)
                                         (implicit implicits: ToImplicits[dt.Uri, dt.QName, dt.PropertyValue]): Seq[PartialFunction[Identified, dt.NestedDocument]] =
      for(tb <- nestedBuilders) yield {
        tb.buildTo(dt)
      }

  trait ToImplicits[DtUri, DtName, DtPropertyValue] {
    implicit def resolve: ((String, String)) => DtName

    implicit def uri2uri: Uri => DtUri
    implicit def qname2name: QName => DtName
    implicit def uri2Value: Uri => DtPropertyValue
    implicit def string2Value: String => DtPropertyValue
    implicit def integer2Value: Int => DtPropertyValue
    implicit def double2Value: Double => DtPropertyValue
    implicit def booleanValue: Boolean => DtPropertyValue
    implicit def timestamp2Value: Timestamp => DtPropertyValue

    implicit final def uriReference[T]: UriReference[T] => DtPropertyValue =
      (u: UriReference[T]) => uri2Value(u.ref)
    implicit final def enum[T](implicit enumT: EnumStringMapping[T]): T => DtPropertyValue = (t: T) =>
      string2Value(enumT.toString(t))
  }

  trait FromImplicits[DtUri, DtName, DtPropertyValue] {
    implicit def resolve: ((String, String)) => DtName

    implicit def uri2uri: DtUri => Uri
    implicit def name2qname: DtName => QName

    implicit def value2uri: DtPropertyValue => Uri
    implicit def value2string: DtPropertyValue => String
    implicit def value2integer: DtPropertyValue => Int
    implicit def value2double: DtPropertyValue => Double
    implicit def value2boolean: DtPropertyValue => Boolean
    implicit def value2timestamp: DtPropertyValue => Timestamp

    implicit final def enum[T](implicit enumT: EnumStringMapping[T]): DtPropertyValue => T = (pv: DtPropertyValue) =>
      enumT.fromString(value2string(pv))
  }

  def toPropertyHelper[DT <: Datatree with WebOps with RelationsOps]
  (dt: DT)
  (implicit implicits: ToImplicits[dt.Uri, dt.QName, dt.PropertyValue]) = new Object {

    def asProperty[T, Name](name: Name, values: Seq[T])
                     (implicit fv: T => dt.PropertyValue, evN: Name => dt.QName): Seq[dt.NamedProperty] = {
      for (v <- values) yield
        dt.NamedProperty(
          name = dt.One(evN(name)),
          propertyValue = dt.One(fv(v)))
    }

    def annotations(as: Seq[Annotation]): Seq[dt.NamedProperty] =
    {
      import implicits._
      for (a <- as) yield {
        dt.NamedProperty(
          name = dt.One(a.relation.theOne : QName),
          propertyValue = dt.One(a.value.theOne match {
            case StringValue(s) => s
            case IntegerValue(i) => i
            case DoubleValue(d) => d
            case BooleanValue(b) => b
            case UriValue(u) => u
            case TypedValue(v, t) => dt.TypedLiteral(v, t)
            case NestedValue(d) => identified2Value(d)
          })
        )
      }
    }

    def mapIdentity(i: Identified): dt.ZeroOne[dt.Uri] =
      i.identity.theOne match {
        case TheBNodeUri => dt.ZeroOne()
        case id => dt.ZeroOne(implicits.uri2uri(id))
      }

    implicit def identified2Value[I <: Identified]: I => dt.PropertyValue = (i: Identified) => {
      val ndO = for {
        ndb <- nestedBuilders.map(_ buildTo dt).to[Stream]
        nd <- ndb.lift.apply(i)
      } yield nd

      ndO.headOption.getOrElse(throw new IllegalArgumentException("Could not find nested document handler for: " + i))
    }
  }

  def fromPropertyHelper[DT <: Datatree with WebOps with RelationsOps]
  (dt: DT)
  (implicit implicits: FromImplicits[dt.Uri, dt.QName, dt.PropertyValue]) = new Object {
    def mapIdentity(d: dt.Document): Uri =
      dt.zeroOneOps.seq(d.identity).headOption map implicits.uri2uri getOrElse TheBNodeUri

    def fetchProperty[T, Name]
    (d: dt.Document, name: Name)(implicit evN: Name => dt.QName, pvt: dt.PropertyValue => T): Seq[T] = {
      val qName = evN(name)
      for (p <- dt.zeroManyOps.seq(d.properties) if dt.oneOps.theOne(p.name) == qName)
      yield pvt(dt.oneOps.theOne(p.propertyValue))
    }

    def annotations(nps: Seq[dt.NamedProperty]): Seq[Annotation] = {
      import implicits._
      for (np <- nps) yield {
        Annotation(
          relation = One(dt.oneOps.theOne(np.name) : QName),
          value = One(dt.oneOps.theOne(np.propertyValue) match {
            case dt.StringLiteral(s) =>
              StringValue(s)
            case dt.UriLiteral(uri) =>
              UriValue(Uri(dt.uriOps.uriString(uri)))
            case nd : dt.NestedDocument =>
              NestedValue(value2identified(nd))
            case v =>
              throw new IllegalStateException(s"Unable to handle annotation value $v")
          })
        )
      }
    }

    implicit def value2identified[I]: dt.PropertyValue => I = _ match {
      case (nd : dt.NestedDocument) =>
        val iO = for {
          ndb <- nestedBuilders.map(_ buildFrom dt)
          i <- ndb.lift.apply(nd)
        } yield i

        iO.headOption.getOrElse(throw new IllegalArgumentException("Could not parse nested document")).asInstanceOf[I]
      case dt.UriLiteral(dt.Uri(uri)) =>
        UriReference(Uri(uri)).asInstanceOf[I]
    }
  }

  trait NestedBuilder[+I] {
    def buildTo[DT <: Datatree with WebOps with RelationsOps]
        (dt: DT)
        (implicit implicits: ToImplicits[dt.Uri, dt.QName, dt.PropertyValue]): PartialFunction[Identified, dt.NestedDocument]
    def buildFrom[DT <: Datatree with WebOps with RelationsOps]
        (dt: DT)
        (implicit implicits: FromImplicits[dt.Uri, dt.QName, dt.PropertyValue]): PartialFunction[dt.NestedDocument, I]
  }

  def nestedBuilders: Seq[NestedBuilder[Identified]] = Seq()

  trait TopBuilder[+TL] {
    def buildTo[DT <: Datatree with WebOps with RelationsOps]
        (dt: DT)
        (implicit implicits: ToImplicits[dt.Uri, dt.QName, dt.PropertyValue]):
        PartialFunction[TopLevel, dt.TopLevelDocument]
    def buildFrom[DT <: Datatree with WebOps with RelationsOps]
        (dt: DT)
        (implicit implicits: FromImplicits[dt.Uri, dt.QName, dt.PropertyValue]):
        PartialFunction[dt.TopLevelDocument, TL]
  }

  def topBuilders: Seq[TopBuilder[TopLevel]] = Seq()

}

trait SBOL2BaseOps extends SBOL2Base {
  importedPackages : WebOps with RelationsOps =>

  type Timestamp = java.util.Calendar

  override val Timestamp = new TimestampApi {
    import javax.xml.bind.DatatypeConverter
    def apply(ts: String): Timestamp = DatatypeConverter.parseDateTime(ts)
    def unapply(ts: Timestamp): Option[String] = Some(DatatypeConverter.printDateTime(ts))
  }
}