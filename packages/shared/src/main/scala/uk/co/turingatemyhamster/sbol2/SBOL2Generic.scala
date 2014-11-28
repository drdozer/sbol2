package uk.co.turingatemyhamster.sbol2

import uk.co.turingatemyhamster.datatree.Datatree
import uk.co.turingatemyhamster.relations.RelationsOps
import uk.co.turingatemyhamster.web.WebOps

/**
 *
 *
 * @author Matthew Pocock
 */
trait SBOL2Generic extends SBOL2Base {
  importedPackages : WebOps with RelationsOps =>

  trait ExplicitlyTyped {
    def `type`: One[QName]
  }

  case class GenericTopLevel(identity: One[Uri],
                             persistentIdentity: ZeroOne[Uri] = ZeroOne(),
                             version: ZeroOne[String] = ZeroOne(),
                             timestamp:ZeroOne[Timestamp] = ZeroOne(),
                             annotations: ZeroMany[Annotation] = ZeroMany(),
                             displayId: ZeroOne[String] = ZeroOne(),
                             name: ZeroOne[String] = ZeroOne(),
                             description: ZeroOne[String] = ZeroOne(),
                             `type`: One[QName]
                              ) extends TopLevel with ExplicitlyTyped

  case class StructuredAnnotation(identity: One[Uri] = One(TheBNodeUri), // fixme: bogus hack
                               persistentIdentity: ZeroOne[Uri] = ZeroOne(),
                               version: ZeroOne[String] = ZeroOne(),
                               timestamp:ZeroOne[Timestamp] = ZeroOne(),
                               annotations: ZeroMany[Annotation] = ZeroMany(),
                               `type`: One[QName]
                                ) extends Identified with ExplicitlyTyped

  object StructuredAnnotation {
    val nestedBuilder: NestedBuilder[StructuredAnnotation] = new NestedBuilder[StructuredAnnotation] {
      override def buildTo[DT <: Datatree with WebOps with RelationsOps]
      (dt: DT)(implicit implicits: ToImplicits[dt.Uri, dt.QName, dt.PropertyValue])
      : PartialFunction[Identified, dt.NestedDocument] = {
        case sa : StructuredAnnotation =>
          import implicits._
          val ph = toPropertyHelper(dt)(implicits)
          dt.NestedDocument(
            identity = ph.mapIdentity(sa),
            `type` = dt.One(qname2name(sa.`type`.theOne)),
            properties = dt.ZeroMany(
              Identified.propertyWomble.asProperties(dt, sa) : _*
            )
          )
      }

      override def buildFrom[DT <: Datatree with WebOps with RelationsOps]
      (dt: DT)(implicit implicits: FromImplicits[dt.Uri, dt.QName, dt.PropertyValue])
      : PartialFunction[dt.NestedDocument, StructuredAnnotation] = {
        case nd =>
          import implicits._
          val ph = fromPropertyHelper(dt)(implicits)
          val womble = Identified.propertyWomble
          StructuredAnnotation(
            identity = One(ph.mapIdentity(nd)),
            `type` = One(name2qname(dt.oneOps.theOne(nd.`type`))),
            annotations = ZeroMany(womble.readAnnotations(dt)(nd, womble.collectAllProperties(dt)) : _*),
            persistentIdentity = ZeroOne(womble.readProperty[DT, Uri](dt)(implicits, ph.value2identified).apply(nd -> "persistentIdentity") : _*),
            version = ZeroOne(womble.readProperty[DT, String](dt).apply(nd -> "version") : _*),
            timestamp = ZeroOne(womble.readProperty[DT, Timestamp](dt).apply(nd -> "timestamp") : _*)
          )
      }
    }
  }

  override def nestedBuilders: Seq[NestedBuilder[Identified]] = super.nestedBuilders ++ Seq(
    StructuredAnnotation.nestedBuilder)
}
