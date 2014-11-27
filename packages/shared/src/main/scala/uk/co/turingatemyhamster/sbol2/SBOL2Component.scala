package uk.co.turingatemyhamster.sbol2

import uk.co.turingatemyhamster.web.WebOps
import uk.co.turingatemyhamster.relations.{Relations, RelationsOps}
import uk.co.turingatemyhamster.datatree.Datatree

/**
 * SBOL2 Component package.
 *
 * @author Matthew Pocock
 */
trait SBOL2Component extends SBOL2Base {
  importedPackages : WebOps with RelationsOps =>

  @RDFType(namespaceUri = "http://sbols.org/sbolv2/component/", prefix="sbol2-component", localPart="ComponentDefinition")
  case class ComponentDefinition(identity: One[Uri],
                                 persistentIdentity: ZeroOne[Uri] = ZeroOne(),
                                 version: ZeroOne[String] = ZeroOne(),
                                 timestamp:ZeroOne[Timestamp] = ZeroOne(),
                                 annotations: ZeroMany[Annotation] = ZeroMany(),
                                 displayId: ZeroOne[String] = ZeroOne(),
                                 name: ZeroOne[String] = ZeroOne(),
                                 description: ZeroOne[String] = ZeroOne(),

                                 @RDFProperty(localPart = "type")
                                 `type`: One[Uri],
                                 @RDFProperty(localPart = "role")
                                 roles: OneMany[Uri],
                                 @RDFProperty(localPart = "sequenceAnnotation")
                                 sequenceAnnotations: ZeroMany[SequenceAnnotation] = ZeroMany(),
                                 @RDFProperty(localPart = "sequenceConstraint")
                                 sequenceConstraints: ZeroMany[SequenceConstraint] = ZeroMany(),
                                 @RDFProperty(localPart = "subComponent")
                                 subComponents: ZeroMany[Component] = ZeroMany(),
                                 @RDFProperty(localPart = "sequence")
                                 structure: ZeroOne[UriReference[Sequence]] = ZeroOne())
    extends TopLevel

  object ComponentDefinition {
    implicit val propertyWomble: PropertyWomble[ComponentDefinition] =
      BuilderMacro.propertyWomble[SBOL2Component, ComponentDefinition](importedPackages)
  }

  @RDFType(namespaceUri = "http://sbols.org/sbolv2/component/", prefix="sbol2-component", localPart="Sequence")
  case class Sequence(identity: One[Uri],
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

  object Sequence {
    implicit val propertyWomble: PropertyWomble[Sequence] =
      BuilderMacro.propertyWomble[SBOL2Component, Sequence](importedPackages)
  }

  @RDFType(namespaceUri = "http://sbols.org/sbolv2/component/", prefix="sbol2-component", localPart="ComponentInstance")
  trait ComponentInstance extends Documented {
    @RDFProperty(localPart = "access")
    def access: One[AccessModifier]
    @RDFProperty(localPart = "instantiatedComponent")
    def instantiatedComponent: One[UriReference[ComponentDefinition]]
    @RDFProperty(localPart = "reference")
    def references: ZeroMany[MapsTo]
  }

  object ComponentInstance {
    implicit val propertyWomble: PropertyWomble[ComponentInstance] =
      BuilderMacro.propertyWomble[SBOL2Component, ComponentInstance](importedPackages)
  }

  @RDFType(namespaceUri = "http://sbols.org/sbolv2/component/", prefix="sbol2-component", localPart="Component")
  case class Component(identity: One[Uri],
                       persistentIdentity: ZeroOne[Uri] = ZeroOne(),
                       version: ZeroOne[String] = ZeroOne(),
                       timestamp:ZeroOne[Timestamp] = ZeroOne(),
                       annotations: ZeroMany[Annotation] = ZeroMany(),
                       displayId: ZeroOne[String] = ZeroOne(),
                       name: ZeroOne[String] = ZeroOne(),
                       description: ZeroOne[String] = ZeroOne(),
                       access: One[AccessModifier],
                       instantiatedComponent: One[UriReference[ComponentDefinition]],
                       references: ZeroMany[MapsTo] = ZeroMany())
    extends ComponentInstance

  object Component {
    implicit val propertyWomble: PropertyWomble[Component] =
      BuilderMacro.propertyWomble[SBOL2Component, Component](importedPackages)
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

  @RDFType(namespaceUri = "http://sbols.org/sbolv2/component/", prefix="sbol2-component", localPart="MapsTo")
  case class MapsTo(identity: One[Uri],
                    persistentIdentity: ZeroOne[Uri] = ZeroOne(),
                    version: ZeroOne[String] = ZeroOne(),
                    timestamp:ZeroOne[Timestamp] = ZeroOne(),
                    annotations: ZeroMany[Annotation] = ZeroMany(),

                    @RDFProperty(localPart = "refinement")
                    refinement: One[Refinement],
                    @RDFProperty(localPart = "local")
                    local: One[UriReference[ComponentInstance]],
                    @RDFProperty(localPart = "remote")
                    remote: One[UriReference[ComponentInstance]])
    extends Identified

  object MapsTo {
    implicit val propertyWomble: PropertyWomble[MapsTo] =
      BuilderMacro.propertyWomble[SBOL2Component, MapsTo](importedPackages)
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

  @RDFType(namespaceUri = "http://sbols.org/sbolv2/component/", prefix="sbol2-component", localPart="SequenceAnnotation")
  case class SequenceAnnotation(identity: One[Uri],
                                persistentIdentity: ZeroOne[Uri] = ZeroOne(),
                                version: ZeroOne[String] = ZeroOne(),
                                timestamp:ZeroOne[Timestamp] = ZeroOne(),
                                annotations: ZeroMany[Annotation] = ZeroMany(),
                                displayId: ZeroOne[String] = ZeroOne(),
                                name: ZeroOne[String] = ZeroOne(),
                                description: ZeroOne[String] = ZeroOne(),

                                @RDFProperty(localPart = "location")
                                location: One[Location],
                                @RDFProperty(localPart = "component")
                                component: ZeroOne[UriReference[Component]] = ZeroOne())
    extends Documented

  object SequenceAnnotation {
    implicit val propertyWomble: PropertyWomble[SequenceAnnotation] =
      BuilderMacro.propertyWomble[SBOL2Component, SequenceAnnotation](importedPackages)
  }

  @RDFType(namespaceUri = "http://sbols.org/sbolv2/component/", prefix="sbol2-component", localPart="Location")
  trait Location extends Identified

  object Location {
    implicit val propertyWomble: PropertyWomble[Location] =
      BuilderMacro.propertyWomble[SBOL2Component, Location](importedPackages)
  }

  @RDFType(namespaceUri = "http://sbols.org/sbolv2/component/", prefix="sbol2-component", localPart="SequenceConstraint")
  case class SequenceConstraint(identity: One[Uri],
                                persistentIdentity: ZeroOne[Uri] = ZeroOne(),
                                version: ZeroOne[String] = ZeroOne(),
                                timestamp:ZeroOne[Timestamp] = ZeroOne(),
                                annotations: ZeroMany[Annotation] = ZeroMany(),
                                displayId: ZeroOne[String] = ZeroOne(),
                                name: ZeroOne[String] = ZeroOne(),
                                description: ZeroOne[String] = ZeroOne(),

                                @RDFProperty(localPart = "restriction") restriction: One[Uri],
                                @RDFProperty(localPart = "subject") subject: One[UriReference[Component]],
                                @RDFProperty(localPart = "object") `object`: One[UriReference[Component]])
    extends Documented

  object SequenceConstraint {
    implicit val propertyWomble: PropertyWomble[SequenceConstraint] =
      BuilderMacro.propertyWomble[SBOL2Component, SequenceConstraint](importedPackages)
  }

  abstract override def nestedBuilders: Seq[NestedBuilder[Identified]] =
    super.nestedBuilders ++ Seq(
      BuilderMacro.nestedBuilder[SBOL2Component, Component](importedPackages),
      BuilderMacro.nestedBuilder[SBOL2Component, MapsTo](importedPackages),
      BuilderMacro.nestedBuilder[SBOL2Component, SequenceAnnotation](importedPackages),
      BuilderMacro.nestedBuilder[SBOL2Component, SequenceConstraint](importedPackages))

  abstract override def topBuilders: Seq[TopBuilder[TopLevel]] =
    super.topBuilders ++ Seq(
      BuilderMacro.topBuilder[SBOL2Component, ComponentDefinition](importedPackages),
      BuilderMacro.topBuilder[SBOL2Component, Sequence](importedPackages))

  abstract override def namespaceBindings = super.namespaceBindings ++ Seq(
    NamespaceBinding(Namespace(Uri("http://sbols.org/sbolv2/component/")), Prefix("sbol2-component"))
  )
}
