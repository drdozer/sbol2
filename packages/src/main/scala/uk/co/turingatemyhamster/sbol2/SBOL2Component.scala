package uk.co.turingatemyhamster.sbol2

import uk.co.turingatemyhamster.relations.{Relations, RelationsOps}
import uk.co.turingatemyhamster.datatree.Datatree

/**
 * SBOL2 Component package.
 *
 * @author Matthew Pocock
 */
trait SBOL2Component extends SBOL2Base {
  importedPackages : RelationsOps =>

  @RDFType(namespaceUri = "http://sbols.org/sbolv2/", prefix="sbol2", localPart="Component")
  case class Component(identity: One[Uri],
                       persistentIdentity: ZeroOne[Uri] = ZeroOne(),
                       version: ZeroOne[String] = ZeroOne(),
                       timestamp:ZeroOne[Timestamp] = ZeroOne(),
                       annotations: ZeroMany[Annotation] = ZeroMany(),
                       displayId: ZeroOne[String] = ZeroOne(),
                       name: ZeroOne[String] = ZeroOne(),
                       description: ZeroOne[String] = ZeroOne(),

                       @RDFProperty(localPart = "type")
                       `type`: One[Uri],
                       @RDFProperty(localPart = "roles")
                       roles: OneMany[Uri],
                       @RDFProperty(localPart = "structuralAnnotation")
                       structuralAnnotations: ZeroMany[StructuralAnnotation] = ZeroMany(),
                       @RDFProperty(localPart = "structuralConstraint")
                       structuralConstraints: ZeroMany[StructuralConstraint] = ZeroMany(),
                       @RDFProperty(localPart = "structuralInstantiation")
                       structuralInstantiations: ZeroMany[StructuralInstantiation] = ZeroMany(),
                       @RDFProperty(localPart = "structure")
                       structure: ZeroOne[UriReference[Structure]] = ZeroOne())
    extends TopLevel

  object Component {
    implicit val propertyWomble: PropertyWomble[Component] =
      BuilderMacro.propertyWomble[SBOL2Component, Component](importedPackages)
  }

  @RDFType(namespaceUri = "http://sbols.org/sbolv2/", prefix="sbol2", localPart="Structure")
  case class Structure(identity: One[Uri],
                       persistentIdentity: ZeroOne[Uri] = ZeroOne(),
                       version: ZeroOne[String] = ZeroOne(),
                       timestamp:ZeroOne[Timestamp] = ZeroOne(),
                       annotations: ZeroMany[Annotation] = ZeroMany(),
                       displayId: ZeroOne[String] = ZeroOne(),
                       name: ZeroOne[String] = ZeroOne(),
                       description: ZeroOne[String] = ZeroOne(),

                       @RDFProperty(localPart = "elements")
                       elements: One[String],
                       @RDFProperty(localPart = "encoding")
                       encoding: One[Uri])
    extends TopLevel

  object Structure {
    implicit val propertyWomble: PropertyWomble[Structure] =
      BuilderMacro.propertyWomble[SBOL2Component, Structure](importedPackages)
  }

  @RDFType(namespaceUri = "http://sbols.org/sbolv2/", prefix="sbol2", localPart="ComponentInstantiation")
  trait ComponentInstantiation extends Documented {
    @RDFProperty(localPart = "access")
    def access: One[AccessModifier]
    @RDFProperty(localPart = "instantiatedComponent")
    def instantiatedComponent: One[UriReference[Component]]
    @RDFProperty(localPart = "reference")
    def references: ZeroMany[RefersTo]
  }

  object ComponentInstantiation {
    implicit val propertyWomble: PropertyWomble[ComponentInstantiation] =
      BuilderMacro.propertyWomble[SBOL2Component, ComponentInstantiation](importedPackages)
  }

  @RDFType(namespaceUri = "http://sbols.org/sbolv2/", prefix="sbol2", localPart="StructuralInstantiation")
  case class StructuralInstantiation(identity: One[Uri],
                                     persistentIdentity: ZeroOne[Uri] = ZeroOne(),
                                     version: ZeroOne[String] = ZeroOne(),
                                     timestamp:ZeroOne[Timestamp] = ZeroOne(),
                                     annotations: ZeroMany[Annotation] = ZeroMany(),
                                     displayId: ZeroOne[String] = ZeroOne(),
                                     name: ZeroOne[String] = ZeroOne(),
                                     description: ZeroOne[String] = ZeroOne(),
                                     access: One[AccessModifier],
                                     instantiatedComponent: One[UriReference[Component]],
                                     references: ZeroMany[RefersTo] = ZeroMany())
    extends ComponentInstantiation

  object StructuralInstantiation {
    implicit val propertyWomble: PropertyWomble[StructuralInstantiation] =
      BuilderMacro.propertyWomble[SBOL2Component, StructuralInstantiation](importedPackages)
  }

  sealed trait AccessModifier
  case object Public extends AccessModifier
  case object Private extends AccessModifier

  object AccessModifier {
    implicit val enumStringMapping: EnumStringMapping[AccessModifier] = new EnumStringMapping[AccessModifier] {
      override def toString(am: AccessModifier) = am match {
        case Public => "public"
        case Private => "private"
      }

      override def fromString(s: String) = s match {
        case "public" => Public
        case "private" => Private
      }
    }
  }

  @RDFType(namespaceUri = "http://sbols.org/sbolv2/", prefix="sbol2", localPart="RefersTo")
  case class RefersTo(identity: One[Uri],
                      persistentIdentity: ZeroOne[Uri] = ZeroOne(),
                      version: ZeroOne[String] = ZeroOne(),
                      timestamp:ZeroOne[Timestamp] = ZeroOne(),
                      annotations: ZeroMany[Annotation] = ZeroMany(),

                      @RDFProperty(localPart = "refinement")
                      refinement: One[Refinement],
                      @RDFProperty(localPart = "local")
                      local: One[UriReference[ComponentInstantiation]],
                      @RDFProperty(localPart = "remote")
                      remote: One[UriReference[ComponentInstantiation]])
    extends Identified

  object RefersTo {
    implicit val propertyWomble: PropertyWomble[RefersTo] =
      BuilderMacro.propertyWomble[SBOL2Component, RefersTo](importedPackages)
  }

  sealed trait Refinement
  case object VerifyIdentical extends Refinement
  case object UseLocal extends Refinement
  case object UseRemote extends Refinement
  case object Merge extends Refinement

  object Refinement {
    implicit val enumStringMapping: EnumStringMapping[Refinement] = new EnumStringMapping[Refinement] {
      override def toString(r: Refinement) = r match {
        case VerifyIdentical => "verify_identical"
        case UseLocal => "use_local"
        case UseRemote => "use_remote"
        case Merge => "merge"
      }

      override def fromString(s: String) = s match {
        case "verify_identical" => VerifyIdentical
        case "use_local" => UseLocal
        case "use_remote" => UseRemote
        case "merge" => Merge
      }
    }
  }

  @RDFType(namespaceUri = "http://sbols.org/sbolv2/", prefix="sbol2", localPart="StructuralAnnotation")
  case class StructuralAnnotation(identity: One[Uri],
                                  persistentIdentity: ZeroOne[Uri] = ZeroOne(),
                                  version: ZeroOne[String] = ZeroOne(),
                                  timestamp:ZeroOne[Timestamp] = ZeroOne(),
                                  annotations: ZeroMany[Annotation] = ZeroMany(),
                                  displayId: ZeroOne[String] = ZeroOne(),
                                  name: ZeroOne[String] = ZeroOne(),
                                  description: ZeroOne[String] = ZeroOne(),

                                  @RDFProperty(localPart = "location")
                                  location: One[Location],
                                  @RDFProperty(localPart = "structuralInstantiation")
                                  structuralInstantiation: ZeroOne[UriReference[ComponentInstantiation]] = ZeroOne())
    extends Documented

  object StructuralAnnotation {
    implicit val propertyWomble: PropertyWomble[StructuralAnnotation] =
      BuilderMacro.propertyWomble[SBOL2Component, StructuralAnnotation](importedPackages)
  }

  @RDFType(namespaceUri = "http://sbols.org/sbolv2/", prefix="sbol2", localPart="Location")
  trait Location extends Identified

  object Location {
    implicit val propertyWomble: PropertyWomble[Location] =
      BuilderMacro.propertyWomble[SBOL2Component, Location](importedPackages)
  }

  @RDFType(namespaceUri = "http://sbols.org/sbolv2/", prefix="sbol2", localPart="StructuralConstraint")
  case class StructuralConstraint(identity: One[Uri],
                                  persistentIdentity: ZeroOne[Uri] = ZeroOne(),
                                  version: ZeroOne[String] = ZeroOne(),
                                  timestamp:ZeroOne[Timestamp] = ZeroOne(),
                                  annotations: ZeroMany[Annotation] = ZeroMany(),
                                  displayId: ZeroOne[String] = ZeroOne(),
                                  name: ZeroOne[String] = ZeroOne(),
                                  description: ZeroOne[String] = ZeroOne(),

                                  @RDFProperty(localPart = "restriction") restriction: One[Uri],
                                  @RDFProperty(localPart = "subject") subject: One[UriReference[StructuralInstantiation]],
                                  @RDFProperty(localPart = "object") `object`: One[UriReference[StructuralInstantiation]])
    extends Documented

  object StructuralConstraint {
    implicit val propertyWomble: PropertyWomble[StructuralConstraint] =
      BuilderMacro.propertyWomble[SBOL2Component, StructuralConstraint](importedPackages)
  }

  abstract override def nestedBuilders: Seq[NestedBuilder[Identified]] =
    super.nestedBuilders ++ Seq(
      BuilderMacro.nestedBuilder[SBOL2Component, StructuralInstantiation](importedPackages),
      BuilderMacro.nestedBuilder[SBOL2Component, RefersTo](importedPackages),
      BuilderMacro.nestedBuilder[SBOL2Component, StructuralAnnotation](importedPackages),
      BuilderMacro.nestedBuilder[SBOL2Component, StructuralConstraint](importedPackages))

  abstract override def topBuilders: Seq[TopBuilder[TopLevel]] =
    super.topBuilders ++ Seq(
      BuilderMacro.topBuilder[SBOL2Component, Component](importedPackages),
      BuilderMacro.topBuilder[SBOL2Component, Structure](importedPackages))
}
