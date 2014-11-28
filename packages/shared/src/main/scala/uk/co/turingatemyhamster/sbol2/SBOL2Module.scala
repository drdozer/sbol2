package uk.co.turingatemyhamster.sbol2

import uk.co.turingatemyhamster.web.{Web, WebOps}
import uk.co.turingatemyhamster.relations.{Relations, RelationsOps}

/**
 * SBOL2 Module package.
 */
trait SBOL2Module extends SBOL2Base {
  importedPackages : WebOps with RelationsOps with SBOL2Component with SBOL2Model =>

  @RDFType(namespaceUri = "http://sbols.org/sbolv2/", prefix = "sbol2", localPart = "ModuleDefinition")
  case class ModuleDefinition(identity: One[Uri],
                    persistentIdentity: ZeroOne[Uri] = ZeroOne(),
                    version: ZeroOne[String] = ZeroOne(),
                    timestamp:ZeroOne[Timestamp] = ZeroOne(),
                    annotations: ZeroMany[Annotation] = ZeroMany(),
                    displayId: ZeroOne[String] = ZeroOne(),
                    name: ZeroOne[String] = ZeroOne(),
                    description: ZeroOne[String] = ZeroOne(),

                    @RDFProperty(localPart = "role")
                    roles: OneMany[Uri],
                    @RDFProperty(localPart = "subModule")
                    subModule: ZeroMany[Module] = ZeroMany(),
                    @RDFProperty(localPart = "interaction")
                    interactions: ZeroMany[Interaction] = ZeroMany(),
                    @RDFProperty(localPart = "functionalComponents")
                    functionalComponents: ZeroMany[FunctionalComponent] = ZeroMany(),
                    @RDFProperty(localPart = "model")
                    models: ZeroMany[UriReference[Model]] = ZeroMany())
    extends TopLevel

  object ModuleDefinition {
    implicit val propertyWomble: PropertyWomble[ModuleDefinition] =
      BuilderMacro.propertyWomble[SBOL2Module with SBOL2Model, ModuleDefinition](importedPackages)
  }

  @RDFType(namespaceUri = "http://sbols.org/sbolv2/", prefix = "sbol2", localPart = "Module")
  case class Module(identity: One[Uri],
                                 persistentIdentity: ZeroOne[Uri] = ZeroOne(),
                                 version: ZeroOne[String] = ZeroOne(),
                                 timestamp:ZeroOne[Timestamp] = ZeroOne(),
                                 annotations: ZeroMany[Annotation] = ZeroMany(),
                                 displayId: ZeroOne[String] = ZeroOne(),
                                 name: ZeroOne[String] = ZeroOne(),
                                 description: ZeroOne[String] = ZeroOne(),

                                 @RDFProperty(localPart = "instantiatedModule")
                                 instantiatedModule: One[UriReference[ModuleDefinition]],
                                 @RDFProperty(localPart = "reference")
                                 references: ZeroMany[MapsTo])
    extends Documented

  object Module {
    implicit val propertyWomble: PropertyWomble[Module] =
      BuilderMacro.propertyWomble[SBOL2Module with SBOL2Component, Module](importedPackages)
  }

  @RDFType(namespaceUri = "http://sbols.org/sbolv2/", prefix = "sbol2", localPart = "Interaction")
  case class Interaction(identity: One[Uri],
                         persistentIdentity: ZeroOne[Uri] = ZeroOne(),
                         version: ZeroOne[String] = ZeroOne(),
                         timestamp:ZeroOne[Timestamp] = ZeroOne(),
                         annotations: ZeroMany[Annotation] = ZeroMany(),
                         displayId: ZeroOne[String] = ZeroOne(),
                         name: ZeroOne[String] = ZeroOne(),
                         description: ZeroOne[String] = ZeroOne(),

                         @RDFProperty(localPart = "type")
                         types: OneMany[Uri],
                         @RDFProperty(localPart = "participation")
                         participations: OneMany[Participation])
    extends Documented

  object Interaction {
    implicit val propertyWomble = BuilderMacro.propertyWomble[SBOL2Module, Interaction](importedPackages)
  }

  @RDFType(namespaceUri = "http://sbols.org/sbolv2/", prefix = "sbol2", localPart = "Participation")
  case class Participation(identity: One[Uri],
                           persistentIdentity: ZeroOne[Uri] = ZeroOne(),
                           version: ZeroOne[String] = ZeroOne(),
                           timestamp:ZeroOne[Timestamp] = ZeroOne(),
                           annotations: ZeroMany[Annotation] = ZeroMany(),

                           @RDFProperty(localPart = "role")
                           roles: OneMany[Uri],
                           @RDFProperty(localPart = "participant")
                           participant: One[UriReference[FunctionalComponent]])
    extends Identified

  object Participation {
    implicit val propertyWomble = BuilderMacro.propertyWomble[SBOL2Module, Participation](importedPackages)
  }

  @RDFType(namespaceUri = "http://sbols.org/sbolv2/", prefix = "sbol2", localPart = "ComponentInstantiation")
  case class FunctionalComponent(identity: One[Uri],
                                     persistentIdentity: ZeroOne[Uri] = ZeroOne(),
                                     version: ZeroOne[String] = ZeroOne(),
                                     timestamp:ZeroOne[Timestamp] = ZeroOne(),
                                     annotations: ZeroMany[Annotation] = ZeroMany(),
                                     displayId: ZeroOne[String] = ZeroOne(),
                                     name: ZeroOne[String] = ZeroOne(),
                                     description: ZeroOne[String] = ZeroOne(),
                                     access: One[AccessModifier],
                                     instantiatedComponent: One[UriReference[ComponentDefinition]],
                                     mappings: ZeroMany[MapsTo] = ZeroMany(),

                                     @RDFProperty(localPart = "direction")
                                     direction: One[Direction])
    extends ComponentInstance

  object FunctionalComponent {
    implicit val propertyWomble: PropertyWomble[FunctionalComponent] =
      BuilderMacro.propertyWomble[SBOL2Module with SBOL2Component, FunctionalComponent](importedPackages)
  }

  sealed trait Direction
  case object Input extends Direction
  case object Output extends Direction
  case object InOut extends Direction
  case object None extends Direction

  object Direction {
    implicit val enumStringMapping: EnumStringMapping[Direction] = new EnumStringMapping[Direction] {
      override def toString(d: Direction) = d match {
        case Input => "input"
        case Output => "output"
        case InOut => "in_out"
        case None => "none"
      }

      override def fromString(s: String) = s match {
        case "input" => Input
        case "output" => Output
        case "in_out" => InOut
        case "none" => None
      }
    }
  }


  abstract override def nestedBuilders: Seq[NestedBuilder[Identified]] =
    super.nestedBuilders ++ Seq(
      BuilderMacro.nestedBuilder[SBOL2Module with SBOL2Component, Module](importedPackages),
      BuilderMacro.nestedBuilder[SBOL2Module, Interaction](importedPackages),
      BuilderMacro.nestedBuilder[SBOL2Module, Participation](importedPackages),
      BuilderMacro.nestedBuilder[SBOL2Module with SBOL2Component, FunctionalComponent](importedPackages))

  abstract override def topBuilders: Seq[TopBuilder[TopLevel]] =
    super.topBuilders ++ Seq(BuilderMacro.topBuilder[SBOL2Module with SBOL2Model, ModuleDefinition](importedPackages))

}
