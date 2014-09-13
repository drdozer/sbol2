package uk.co.turingatemyhamster.sbol2

import uk.co.turingatemyhamster.cake.Relations
import uk.co.turingatemyhamster.datatree.Datatree

/**
 * SBOL2 Collection package.
 *
 * @author Matthew Pocock
 */
trait SBOL2Collection extends SBOL2Base {
  importedPackages =>

  @RDFType(namespaceURI = "http://sbols.org/sbolv2/", prefix = "sbol2", localPart = "Collection")
  case class Collection(identity: One[URI],
                        persistentIdentity: ZeroOne[URI] = ZeroOne(),
                        version: ZeroOne[String] = ZeroOne(),
                        timeStamp:ZeroOne[Timestamp] = ZeroOne(),
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
}
