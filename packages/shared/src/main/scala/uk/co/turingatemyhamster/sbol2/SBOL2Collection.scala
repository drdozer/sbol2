package uk.co.turingatemyhamster.sbol2

import uk.co.turingatemyhamster.web.WebOps
import uk.co.turingatemyhamster.relations.{Relations, RelationsOps}
import uk.co.turingatemyhamster.datatree.Datatree

/**
 * SBOL2 Collection package.
 *
 * @author Matthew Pocock
 */
trait SBOL2Collection extends SBOL2Base {
  importedPackages : WebOps with RelationsOps =>

  @RDFType(namespaceUri = "http://sbols.org/sbolv2/collection/", prefix = "sbol2-collection", localPart = "Collection")
  case class Collection(identity: One[Uri],
                        persistentIdentity: ZeroOne[Uri] = ZeroOne(),
                        version: ZeroOne[String] = ZeroOne(),
                        timestamp:ZeroOne[Timestamp] = ZeroOne(),
                        annotations: ZeroMany[Annotation] = ZeroMany(),
                        displayId: ZeroOne[String] = ZeroOne(),
                        name: ZeroOne[String] = ZeroOne(),
                        description: ZeroOne[String] = ZeroOne(),

                        @RDFProperty(localPart = "member")
                        members: ZeroMany[UriReference[TopLevel]] = ZeroMany())
    extends TopLevel

  object Collection {
    implicit val propertyWomble = BuilderMacro.propertyWomble[SBOL2Collection, Collection](importedPackages)
  }

  abstract override def topBuilders: Seq[TopBuilder[TopLevel]] =
    super.topBuilders ++ Seq(BuilderMacro.topBuilder[SBOL2Collection, Collection](importedPackages))

  abstract override def namespaceBindings = super.namespaceBindings ++ Seq(
    NamespaceBinding(Namespace(Uri("http://sbols.org/sbolv2/collection/")), Prefix("sbol2-collection"))
  )
}
