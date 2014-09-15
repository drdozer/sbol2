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
  importedPackages : RelationsOps =>

  /**
   * The abstract Timestamp type.
   *
   * @group AbstractApi
   */
  type Timestamp

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
    def timeStamp: ZeroOne[Timestamp]
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

  case class SBOLDocument(contents: ZeroMany[TopLevel] = ZeroMany())


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
          ph.asProperty("sbol2" -> "timeStamp", i.timeStamp.seq)
      }

      override def readProperty[DT <: Datatree with WebOps with RelationsOps, T]
      (dt: DT)(doc: dt.Document, propName: String)(implicit implicits: FromImplicits[dt.Uri, dt.QName, dt.PropertyValue]) = ???
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
      (dt: DT)(doc: dt.Document, propName: String)(implicit implicits: FromImplicits[dt.Uri, dt.QName, dt.PropertyValue]) = ???
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
      (dt: DT)(doc: dt.Document, propName: String)(implicit implicits: FromImplicits[dt.Uri, dt.QName, dt.PropertyValue]) = ???
    }
  }

  // IO utility stuff

  trait EnumToString[T] {
    def toString(t: T): String
  }

  trait PropertyWomble[I] {
    def asProperties[DT <: Datatree with WebOps with RelationsOps]
    (dt: DT, i: I)(implicit implicits: ToImplicits[dt.Uri, dt.QName, dt.PropertyValue]): Seq[dt.NamedProperty]

    def readProperty[DT <: Datatree with WebOps with RelationsOps, T]
    (dt: DT)
    (doc: dt.Document, propName: String)
    (implicit implicits: FromImplicits[dt.Uri, dt.QName, dt.PropertyValue]): T
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
    implicit final def enum[T](implicit enumT: EnumToString[T]): T => DtPropertyValue = (t: T) =>
      string2Value(enumT.toString(t))
  }

  trait FromImplicits[DtUri, DtName, DtPropertyValue] {

  }

  def toPropertyHelper[DT <: Datatree with WebOps with RelationsOps]
  (dt: DT)
  (implicit implicits: ToImplicits[dt.Uri, dt.QName, dt.PropertyValue]) = new Object {

    def asProperty[T, Name](name: Name, values: Seq[T])
                     (implicit fv: T => dt.PropertyValue, evN: Name => dt.QName): Seq[dt.NamedProperty] = {
      for (v <- values) yield
        dt.NamedProperty(
          dt.One(evN(name)),
          dt.One(fv(v)))
    }

    def annotations(as: Seq[Annotation]): Seq[dt.NamedProperty] =
    {
      import implicits._
      for (a <- as) yield {
        dt.NamedProperty(
          dt.One(a.relation.theOne),
          dt.One(a.value.theOne match {
            case StringValue(s) => s
            case IntegerValue(i) => i
            case DoubleValue(d) => d
            case BooleanValue(b) => b
            case UriValue(u) => u
            case TypedValue(v, t) => dt.TypedLiteral(v, t)
          })
        )
      }
    }

    def mapIdentity(i: Identified): dt.One[dt.Uri] =
      dt.One(implicits.uri2uri(i.identity.theOne))

    implicit def identified2Value[I <: Identified]: I => dt.PropertyValue = (i: Identified) => {
      val ndO = for {
        ndb <- nestedBuilders.map(_ buildTo dt)
        nd <- ndb.lift.apply(i)
      } yield nd

      ndO.headOption.getOrElse(throw new IllegalArgumentException("Could not find nested document handler for: " + i))
    }
  }

  def fromPropertyHelper[DT <: Datatree]
  (dt: DT)
  (implicit implicits: FromImplicits[dt.Uri, dt.QName, dt.PropertyValue]) = new Object {

  }

  trait NestedBuilder[+I] {
    def buildTo[DT <: Datatree with WebOps with RelationsOps]
        (dt: DT)
        (implicit implicits: ToImplicits[dt.Uri, dt.QName, dt.PropertyValue]): PartialFunction[Identified, dt.NestedDocument]
    def buildFrom[DT <: Datatree with WebOps with RelationsOps]
        (dt: DT)
        (implicit implicits: FromImplicits[dt.Uri, dt.QName, dt.PropertyValue]): PartialFunction[dt.NestedDocument, I]  = ???
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

