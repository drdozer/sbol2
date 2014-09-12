package uk.co.turingatemyhamster.sbol2

import uk.co.turingatemyhamster.cake.Relations
import uk.co.turingatemyhamster.datatree.Datatree

/**
 * SBOL2 Component package.
 *
 * @author Matthew Pocock
 */
trait SBOL2Component extends SBOL2Base {
  importedPackages =>

  @RDFType(namespaceURI = "http://sbols.org/sbolv2/", prefix="sbol2", localPart="Component")
  case class Component(identity: One[URI],
                       persistentIdentity: ZeroOne[URI] = ZeroOne(),
                       version: ZeroOne[String] = ZeroOne(),
                       timeStamp:ZeroOne[Timestamp] = ZeroOne(),
                       annotations: ZeroMany[Annotation] = ZeroMany(),
                       displayId: ZeroOne[String] = ZeroOne(),
                       name: ZeroOne[String] = ZeroOne(),
                       description: ZeroOne[String] = ZeroOne(),
                       `type`: One[URI],
                       roles: OneMany[URI],
                       structuralAnnotations: ZeroMany[StructuralAnnotation] = ZeroMany(),
                       structuralConstraints: ZeroMany[StructuralConstraint] = ZeroMany(),
                       structuralInstantiations: ZeroMany[StructuralInstantiation] = ZeroMany(),
                       structure: ZeroOne[UriReference[Structure]] = ZeroOne())
    extends TopLevel

  @RDFType(namespaceURI = "http://sbols.org/sbolv2/", prefix="sbol2", localPart="Structure")
  case class Structure(identity: One[URI],
                       persistentIdentity: ZeroOne[URI] = ZeroOne(),
                       version: ZeroOne[String] = ZeroOne(),
                       timeStamp:ZeroOne[Timestamp] = ZeroOne(),
                       annotations: ZeroMany[Annotation] = ZeroMany(),
                       displayId: ZeroOne[String] = ZeroOne(),
                       name: ZeroOne[String] = ZeroOne(),
                       description: ZeroOne[String] = ZeroOne(),
                       elements: One[String],
                       encoding: One[URI])
    extends TopLevel

  trait ComponentInstantiation extends Documented {
    def access: One[AccessModifier]
    def instantiatedComponent: One[UriReference[Component]]
    def references: ZeroMany[RefersTo]
  }

  @RDFType(namespaceURI = "http://sbols.org/sbolv2/", prefix="sbol2", localPart="StructuralInstantiation")
  case class StructuralInstantiation(identity: One[URI],
                                     persistentIdentity: ZeroOne[URI] = ZeroOne(),
                                     version: ZeroOne[String] = ZeroOne(),
                                     timeStamp:ZeroOne[Timestamp] = ZeroOne(),
                                     annotations: ZeroMany[Annotation] = ZeroMany(),
                                     displayId: ZeroOne[String] = ZeroOne(),
                                     name: ZeroOne[String] = ZeroOne(),
                                     description: ZeroOne[String] = ZeroOne(),
                                     access: One[AccessModifier],
                                     instantiatedComponent: One[UriReference[Component]],
                                     references: ZeroMany[RefersTo] = ZeroMany())
    extends ComponentInstantiation

  sealed trait AccessModifier
  case object Public extends AccessModifier
  case object Private extends AccessModifier

  @RDFType(namespaceURI = "http://sbols.org/sbolv2/", prefix="sbol2", localPart="RefersTo")
  case class RefersTo(identity: One[URI],
                      persistentIdentity: ZeroOne[URI] = ZeroOne(),
                      version: ZeroOne[String] = ZeroOne(),
                      timeStamp:ZeroOne[Timestamp] = ZeroOne(),
                      annotations: ZeroMany[Annotation] = ZeroMany(),
                      refinement: One[Refinement],
                      local: One[UriReference[ComponentInstantiation]],
                      remote: One[UriReference[ComponentInstantiation]])
    extends Identified

  sealed trait Refinement
  case object VerifyIdentical extends Refinement
  case object UseLocal extends Refinement
  case object UseRemote extends Refinement
  case object Merge extends Refinement

  @RDFType(namespaceURI = "http://sbols.org/sbolv2/", prefix="sbol2", localPart="StructuralAnnotation")
  case class StructuralAnnotation(identity: One[URI],
                                  persistentIdentity: ZeroOne[URI] = ZeroOne(),
                                  version: ZeroOne[String] = ZeroOne(),
                                  timeStamp:ZeroOne[Timestamp] = ZeroOne(),
                                  annotations: ZeroMany[Annotation] = ZeroMany(),
                                  displayId: ZeroOne[String] = ZeroOne(),
                                  name: ZeroOne[String] = ZeroOne(),
                                  description: ZeroOne[String] = ZeroOne(),
                                  location: One[Location],
                                  structuralInstantiation: ZeroOne[UriReference[ComponentInstantiation]] = ZeroOne())
    extends Documented

  trait Location extends Identified

  @RDFType(namespaceURI = "http://sbols.org/sbolv2/", prefix="sbol2", localPart="StructuralConstraint")
  case class StructuralConstraint(identity: One[URI],
                                  persistentIdentity: ZeroOne[URI] = ZeroOne(),
                                  version: ZeroOne[String] = ZeroOne(),
                                  timeStamp:ZeroOne[Timestamp] = ZeroOne(),
                                  annotations: ZeroMany[Annotation] = ZeroMany(),
                                  displayId: ZeroOne[String] = ZeroOne(),
                                  name: ZeroOne[String] = ZeroOne(),
                                  description: ZeroOne[String] = ZeroOne(),

                                  @RDFProperty(localPart = "restriction") restriction: One[URI],
                                  @RDFProperty(localPart = "subject") subject: One[UriReference[StructuralInstantiation]],
                                  @RDFProperty(localPart = "object") `object`: One[UriReference[StructuralInstantiation]])
    extends Documented

  object Component {

    implicit val propertyWomble = new PropertyWomble[Component] {
      def asProperties[DT <: Datatree with Relations]
      (dt: DT, c: Component)
      (implicit implicits: Implicits[dt.URI, dt.Name, dt.PropertyValue]): Seq[dt.NamedProperty] =
      {
        import implicits._
        val ph = propertyHelper(dt)
        import ph.identified2Value

        implicitly[PropertyWomble[TopLevel]].asProperties(dt, c) ++
          ph.asProperty("sbol2" -> "type", c.`type`.seq) ++
          ph.asProperty("sbol2" -> "role", c.roles.seq) ++
          ph.asProperty("sbol2" -> "structuralAnnotation", c.structuralAnnotations.seq) ++
          ph.asProperty("sbol2" -> "structuralConstraint", c.structuralConstraints.seq) ++
          ph.asProperty("sbol2" -> "structuralInstantiation", c.structuralInstantiations.seq) ++
          ph.asProperty("sbol2" -> "structure", c.structure.seq)
      }
    }

  }

  object Structure {

    implicit val propertyWomble = new PropertyWomble[Structure] {
      def asProperties[DT <: Datatree with Relations]
      (dt: DT, s: Structure)(implicit implicits: Implicits[dt.URI, dt.Name, dt.PropertyValue]): Seq[dt.NamedProperty] = {
        import implicits._
        val ph = propertyHelper(dt)
        import ph.identified2Value

        implicitly[PropertyWomble[TopLevel]].asProperties(dt, s) ++
          ph.asProperty("sbol2" -> "elements", s.elements.seq) ++
          ph.asProperty("sbol2" -> "encoding", s.encoding.seq)
      }
    }

  }

  object AccessModifier {
    implicit val enumToString: EnumToString[AccessModifier] = new EnumToString[AccessModifier] {
      override def toString(am: AccessModifier) = am match {
        case Public => "public"
        case Private => "private"
      }
    }
  }

  object ComponentInstantiation {

    implicit val propertyWomble = new PropertyWomble[ComponentInstantiation] {
      def asProperties[DT <: Datatree with Relations]
      (dt: DT, ci: ComponentInstantiation)
      (implicit implicits: Implicits[dt.URI, dt.Name, dt.PropertyValue]): Seq[dt.NamedProperty] =
      {
        import implicits._
        val ph = propertyHelper(dt)
        import ph.identified2Value

        implicitly[PropertyWomble[Documented]].asProperties(dt, ci) ++
          ph.asProperty("sbol2" -> "access", ci.access.seq) ++
          ph.asProperty("sbol2" -> "instantiatedComponent", ci.instantiatedComponent.seq) ++
          ph.asProperty("sbol2" -> "reference", ci.references.seq)
      }
    }
  }

  object StructuralInstantiation {
    implicit def propertyWomble: PropertyWomble[StructuralInstantiation] = new PropertyWomble[StructuralInstantiation] {
      def asProperties[DT <: Datatree with Relations]
      (dt: DT, si: StructuralInstantiation)
      (implicit implicits: Implicits[dt.URI, dt.Name, dt.PropertyValue]): Seq[dt.NamedProperty] = {
        implicitly[PropertyWomble[ComponentInstantiation]].asProperties(dt, si)
      }
    }

  }

  object Refinement {
    implicit val enumToString: EnumToString[Refinement] = new EnumToString[Refinement] {
      override def toString(r: Refinement) = r match {
        case VerifyIdentical => "verify_identical"
        case UseLocal => "use_local"
        case UseRemote => "use_remote"
        case Merge => "merge"
      }
    }
  }

  object RefersTo {
    implicit def propertyWomble: PropertyWomble[RefersTo] = new PropertyWomble[RefersTo] {
      def asProperties[DT <: Datatree with Relations]
      (dt: DT, rt: RefersTo)
      (implicit implicits: Implicits[dt.URI, dt.Name, dt.PropertyValue]): Seq[dt.NamedProperty] = {
        import implicits._
        val ph = propertyHelper(dt)
        import ph.identified2Value

        implicitly[PropertyWomble[Identified]].asProperties(dt, rt) ++
          ph.asProperty("sbol2" -> "refinement", rt.refinement.seq) ++
          ph.asProperty("sbol2" -> "local", rt.local.seq) ++
          ph.asProperty("sbol2" -> "remote", rt.remote.seq)
      }
    }

  }
  
  object StructuralAnnotation {
    implicit def propertyWomble: PropertyWomble[StructuralAnnotation] = new PropertyWomble[StructuralAnnotation] {
      def asProperties[DT <: Datatree with Relations]
      (dt: DT, sa: StructuralAnnotation)
      (implicit implicits: Implicits[dt.URI, dt.Name, dt.PropertyValue]): Seq[dt.NamedProperty] = {
        import implicits._
        val ph = propertyHelper(dt)
        import ph.identified2Value

        implicitly[PropertyWomble[Documented]].asProperties(dt, sa) ++
          ph.asProperty("sbol2" -> "location", sa.location.seq) ++
          ph.asProperty("sbol2" -> "structuralInstantiation", sa.structuralInstantiation.seq)
      }
    }

  }

  object StructuralConstraint {
    implicit val propertyWomble: PropertyWomble[StructuralConstraint] =
      BuilderMacro.propertyWomble[SBOL2Component, StructuralConstraint](importedPackages)
  }

  abstract override def topBuilders: Seq[TopBuilder[_]] =
    super.topBuilders ++ Seq(
      BuilderMacro.topBuilder[SBOL2Component, Component](importedPackages),
      BuilderMacro.topBuilder[SBOL2Component, Structure](importedPackages))

  abstract override def nestedBuilders: Seq[NestedBuilder[Any]] =
    super.nestedBuilders ++ Seq(
      BuilderMacro.nestedBuilder[SBOL2Component, StructuralInstantiation](importedPackages),
      BuilderMacro.nestedBuilder[SBOL2Component, RefersTo](importedPackages),
      BuilderMacro.nestedBuilder[SBOL2Component, StructuralAnnotation](importedPackages),
      BuilderMacro.nestedBuilder[SBOL2Component, StructuralConstraint](importedPackages))
}
