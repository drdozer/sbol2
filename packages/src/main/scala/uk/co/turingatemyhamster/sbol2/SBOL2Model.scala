package uk.co.turingatemyhamster.sbol2

import uk.co.turingatemyhamster.relations.{Relations, RelationsOps}
import uk.co.turingatemyhamster.datatree.Datatree

/**
 * SBOL2 Model package.
 *
 * @author Matthew Pocock
 */
trait SBOL2Model extends SBOL2Base {
  importedPackages : RelationsOps =>

  @RDFType(namespaceUri = "http://sbols.org/sbolv2/", prefix="sbol2", localPart="Model")
  case class Model(identity: One[Uri],
                   persistentIdentity: ZeroOne[Uri] = ZeroOne(),
                   version: ZeroOne[String] = ZeroOne(),
                   timestamp:ZeroOne[Timestamp] = ZeroOne(),
                   annotations: ZeroMany[Annotation] = ZeroMany(),
                   displayId: ZeroOne[String] = ZeroOne(),
                   name: ZeroOne[String] = ZeroOne(),
                   description: ZeroOne[String] = ZeroOne(),

                   @RDFProperty(localPart = "source")
                   source: One[Uri],
                   @RDFProperty(localPart = "language")
                   language: One[Uri],
                   @RDFProperty(localPart = "framework")
                   framework: One[Uri],
                   @RDFProperty(localPart = "roles")
                   role: OneMany[Uri])
    extends TopLevel

  object Model {
    implicit val propertyWomble: PropertyWomble[Model] =
      BuilderMacro.propertyWomble[SBOL2Model, Model](importedPackages)
  }

  abstract override def topBuilders: Seq[TopBuilder[TopLevel]] =
    super.topBuilders ++ Seq(BuilderMacro.topBuilder[SBOL2Model, Model](importedPackages))
}