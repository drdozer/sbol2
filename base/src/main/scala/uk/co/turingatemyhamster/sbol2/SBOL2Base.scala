package uk.co.turingatemyhamster.sbol2

import uk.co.turingatemyhamster.cake.Relations
import uk.co.turingatemyhamster.datatree.Datatree

/**
 * SBOL2 base package.
 *
 * This package provides types shared by all SBOL packages.
 *
 * @author Matthew Pocock
 */
abstract class SBOL2Base extends Relations {

  type URI
  type QName
  type Timestamp

  implicit def identifierOps: UriOps

  trait UriOps {
    def idString(id: URI): String
  }

  implicit class UriSyntax(val _id: URI)(implicit ops: UriOps) {
    def idString = ops.idString(_id)
  }

  case class UriReference[T](ref: URI)

  @RDFType(namespaceURI = "http://sbols.org/sbolv2/", prefix = "sbol2", localPart = "Identified")
  trait Identified {
    @RDFSkip
    def identity: One[URI]
    
    @RDFProperty(localPart = "persistentIdentity")
    def persistentIdentity: ZeroOne[URI]
    
    
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

  case class UriValue(value: URI) extends TurtleValue {
    type Value = URI
  }

  case class TypedValue(value: String, xsdType: String) extends TurtleValue {
    type Value = String
  }

  @RDFType(namespaceURI = "http://sbols.org/sbolv2/", prefix = "sbol2", localPart = "Documented")
  trait Documented extends Identified {
    @RDFProperty(localPart = "displayId")
    def displayId: ZeroOne[String]

    @RDFProperty(localPart = "name")
    def name: ZeroOne[String]

    @RDFProperty(localPart = "description")
    def description: ZeroOne[String]
  }

  @RDFType(namespaceURI = "http://sbols.org/sbolv2/", prefix = "sbol2", localPart = "TopLevel")
  trait TopLevel extends Documented

  case class SBOLDocument(contents: ZeroMany[TopLevel] = ZeroMany())


  // Companions. These should largely go away once we have macro generation of io boilerplate

  object Identified {

    implicit val propertyWomble: PropertyWomble[Identified] = new PropertyWomble[Identified] {
      def asProperties[DT <: Datatree with Relations]
      (dt: DT, i: Identified)
      (implicit implicits: Implicits[dt.URI, dt.Name, dt.PropertyValue]): Seq[dt.NamedProperty] = {
        import implicits._
        val ph = propertyHelper(dt)
        ph.asProperty("sbol2" -> "persistentIdentity", i.persistentIdentity.seq) ++
          ph.annotations(i.annotations.seq) ++
          ph.asProperty("sbol2" -> "version", i.version.seq) ++
          ph.asProperty("sbol2" -> "timeStamp", i.timeStamp.seq)
      }
    }

  }

  object Documented {

    implicit val propertyWomble = new PropertyWomble[Documented] {
      def asProperties[DT <: Datatree with Relations]
      (dt: DT, d: Documented)
      (implicit implicits: Implicits[dt.URI, dt.Name, dt.PropertyValue]): Seq[dt.NamedProperty] = {
        import implicits._
        val ph = propertyHelper(dt)

        implicitly[PropertyWomble[Identified]].asProperties(dt, d) ++
          ph.asProperty("sbol2" -> "displayId", d.displayId.seq) ++
          ph.asProperty("sbol2" -> "name", d.name.seq) ++
          ph.asProperty("sbol2" -> "description", d.description.seq)
      }
    }
  }

  object TopLevel {
    implicit val propertyWomble = new PropertyWomble[TopLevel] {
      def asProperties[DT <: Datatree with Relations]
      (dt: DT, tl: TopLevel)
      (implicit implicits: Implicits[dt.URI, dt.Name, dt.PropertyValue]): Seq[dt.NamedProperty] = {
        implicitly[PropertyWomble[Documented]].asProperties(dt, tl)
      }
    }
  }

  // IO utility stuff

  trait PropertyWomble[I] {
    def asProperties[DT <: Datatree with Relations]
    (dt: DT, i: I)(implicit implicits: Implicits[dt.URI, dt.Name, dt.PropertyValue]): Seq[dt.NamedProperty]
  }

  def topBuilders: Seq[TopBuilder[Any]] = Seq()
  
  final def topLevels[DT <: Datatree with Relations](dt: DT)
                                       (implicit implicits: Implicits[dt.URI, dt.Name, dt.PropertyValue]): Seq[PartialFunction[TopLevel, dt.TopLevelDocument]] =
    for(tb <- topBuilders) yield {
      tb.buildTo(dt)
    }

  def nestedBuilders: Seq[NestedBuilder[Any]] = Seq()

  final def nesteds[DT <: Datatree with Relations](dt: DT)
                                         (implicit implicits: Implicits[dt.URI, dt.Name, dt.PropertyValue]): Seq[PartialFunction[Identified, dt.NestedDocument]] =
      for(tb <- nestedBuilders) yield {
        tb.buildTo(dt)
      }

  trait Implicits[DtURI, DtName, DtPropertyValue] {
    implicit def resolve: ((String, String)) => DtName

    implicit def uri2uri: URI => DtURI
    implicit def qname2name: QName => DtName
    implicit def uri2Value: URI => DtPropertyValue
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

  def propertyHelper[DT <: Datatree with Relations]
  (dt: DT)
  (implicit implicits: Implicits[dt.URI, dt.Name, dt.PropertyValue]) = new Object {

    def asProperty[T, Name](name: Name, values: Seq[T])
                     (implicit fv: T => dt.PropertyValue, evN: Name => dt.Name): Seq[dt.NamedProperty] = {
      for (v <- values) yield
        dt.NamedProperty(
          name,
          fv(v))
    }

    def annotations(as: Seq[Annotation]): Seq[dt.NamedProperty] =
    {
      import implicits._
      for (a <- as) yield {
        dt.NamedProperty(
          a.relation.theOne,
          a.value.theOne match {
            case StringValue(s) => s
            case IntegerValue(i) => i
            case DoubleValue(d) => d
            case BooleanValue(b) => b
            case UriValue(u) => u
            case TypedValue(v, t) => dt.TypedLiteral(v, t)
          }
        )
      }
    }

    def mapIdentity(i: Identified): dt.One[dt.URI] =
      dt.One(implicits.uri2uri(i.identity.theOne))

    implicit def identified2Value[I <: Identified]: I => dt.PropertyValue = (i: Identified) => {
      val ndO = for {
        ndb <- nestedBuilders.map(_ buildTo dt)
        nd <- ndb.lift.apply(i)
      } yield nd

      ndO.headOption.getOrElse(throw new IllegalArgumentException("Could not find nested document handler for: " + i))
    }
  }

  trait EnumToString[T] {
    def toString(t: T): String
  }

  trait NestedBuilder[+I] {

    def buildTo[DT <: Datatree with Relations]
        (dt: DT)
        (implicit implicits: Implicits[dt.URI, dt.Name, dt.PropertyValue]): PartialFunction[Identified, dt.NestedDocument]
  }

  trait TopBuilder[+TL] {
    def buildTo[DT <: Datatree with Relations]
        (dt: DT)
        (implicit implicits: Implicits[dt.URI, dt.Name, dt.PropertyValue]): PartialFunction[TopLevel, dt.TopLevelDocument]
  }

}

