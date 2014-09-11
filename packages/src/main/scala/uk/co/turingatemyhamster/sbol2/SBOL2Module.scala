package uk.co.turingatemyhamster.sbol2

import uk.co.turingatemyhamster.cake.Relations

/**
 * SBOL2 Module package.
 */
trait SBOL2Module extends SBOL2Base {
  importedPackages : Relations with SBOL2Component with SBOL2Model =>

  case class Module(identity: One[URI],
                    persistentIdentity: ZeroOne[URI] = ZeroOne(),
                    version: ZeroOne[String] = ZeroOne(),
                    timeStamp:ZeroOne[Timestamp] = ZeroOne(),
                    annotations: ZeroMany[Annotation] = ZeroMany(),
                    displayId: ZeroOne[String] = ZeroOne(),
                    name: ZeroOne[String] = ZeroOne(),
                    description: ZeroOne[String] = ZeroOne(),
                    roles: OneMany[URI],
                    moduleInstantiations: ZeroMany[ModuleInstantiation] = ZeroMany(),
                    interactions: ZeroMany[Interaction] = ZeroMany(),
                    functionalInstantiations: ZeroMany[FunctionalInstantiation] = ZeroMany(),
                    models: ZeroMany[UriReference[Model]] = ZeroMany())
    extends Documented

  case class ModuleInstantiation(identity: One[URI],
                                 persistentIdentity: ZeroOne[URI] = ZeroOne(),
                                 version: ZeroOne[String] = ZeroOne(),
                                 timeStamp:ZeroOne[Timestamp] = ZeroOne(),
                                 annotations: ZeroMany[Annotation] = ZeroMany(),
                                 displayId: ZeroOne[String] = ZeroOne(),
                                 name: ZeroOne[String] = ZeroOne(),
                                 description: ZeroOne[String] = ZeroOne(),
                                 instantiatedModule: One[UriReference[Module]],
                                 references: ZeroMany[RefersTo])
    extends Documented

  case class Interaction(identity: One[URI],
                         persistentIdentity: ZeroOne[URI] = ZeroOne(),
                         version: ZeroOne[String] = ZeroOne(),
                         timeStamp:ZeroOne[Timestamp] = ZeroOne(),
                         annotations: ZeroMany[Annotation] = ZeroMany(),
                         displayId: ZeroOne[String] = ZeroOne(),
                         name: ZeroOne[String] = ZeroOne(),
                         description: ZeroOne[String] = ZeroOne(),
                         types: OneMany[URI],
                         participations: OneMany[Participation])
    extends Documented

  case class Participation(identity: One[URI],
                           persistentIdentity: ZeroOne[URI] = ZeroOne(),
                           version: ZeroOne[String] = ZeroOne(),
                           timeStamp:ZeroOne[Timestamp] = ZeroOne(),
                           annotations: ZeroMany[Annotation] = ZeroMany(),
                           roles: OneMany[URI],
                           participant: One[UriReference[FunctionalInstantiation]])
    extends Identified

  case class FunctionalInstantiation(identity: One[URI],
                                     persistentIdentity: ZeroOne[URI] = ZeroOne(),
                                     version: ZeroOne[String] = ZeroOne(),
                                     timeStamp:ZeroOne[Timestamp] = ZeroOne(),
                                     annotations: ZeroMany[Annotation] = ZeroMany(),
                                     displayId: ZeroOne[String] = ZeroOne(),
                                     name: ZeroOne[String] = ZeroOne(),
                                     description: ZeroOne[String] = ZeroOne(),
                                     access: One[AccessModifier],
                                     instantiatedComponent: One[UriReference[Component]],
                                     references: ZeroMany[RefersTo],
                                     direction: One[Direction])
    extends ComponentInstantiation

  sealed trait Direction
  case object Input extends Direction
  case object Output extends Direction
  case object InOut extends Direction
  case object None extends Direction
}
