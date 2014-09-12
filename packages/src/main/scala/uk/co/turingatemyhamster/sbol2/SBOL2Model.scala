package uk.co.turingatemyhamster.sbol2

import uk.co.turingatemyhamster.cake.Relations
import uk.co.turingatemyhamster.datatree.Datatree

/**
 * SBOL2 Model package.
 *
 * @author Matthew Pocock
 */
trait SBOL2Model extends SBOL2Base {
  importedPackages =>

  @RDFType(namespaceURI = "http://sbols.org/sbolv2/", prefix="sbol2", localPart="Model")
  case class Model(identity: One[URI],
                   persistentIdentity: ZeroOne[URI] = ZeroOne(),
                   version: ZeroOne[String] = ZeroOne(),
                   timeStamp:ZeroOne[Timestamp] = ZeroOne(),
                   annotations: ZeroMany[Annotation] = ZeroMany(),
                   displayId: ZeroOne[String] = ZeroOne(),
                   name: ZeroOne[String] = ZeroOne(),
                   description: ZeroOne[String] = ZeroOne(),

                   @RDFProperty(localPart = "source") source: One[URI],
                   @RDFProperty(localPart = "language") language: One[URI],
                   @RDFProperty(localPart = "framework") framework: One[URI],
                   @RDFProperty(localPart = "roles") roles: OneMany[URI])
    extends TopLevel

  object Model {
    implicit val propertyWomble: PropertyWomble[Model] = BuilderMacro.propertyWomble[SBOL2Model, Model](importedPackages)
  }

  abstract override def topBuilders: Seq[TopBuilder[Any]] =
    super.topBuilders ++ Seq(BuilderMacro.topBuilder[SBOL2Model, Model](importedPackages))

}