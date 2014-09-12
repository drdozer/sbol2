package uk.co.turingatemyhamster.sbol2

import scala.language.experimental.macros
import uk.co.turingatemyhamster.cake.Relations
import uk.co.turingatemyhamster.datatree.Datatree

/**
 *
 *
 * @author Matthew Pocock
 */
object BuilderMacro {
  def topBuilder[SB <: SBOL2Base, TL](sb: SB): sb.TopBuilder[TL] = macro topBuilderImpl[SB, TL]
  def nestedBuilder[SB <: SBOL2Base, TL](sb: SB): sb.NestedBuilder[TL] = macro nestedBuilderImpl[SB, TL]

  def topBuilderImpl[SB <: SBOL2Base : c.WeakTypeTag, TL: c.WeakTypeTag]
  (c: scala.reflect.macros.blackbox.Context)
  (sb: c.Expr[SB]): c.Expr[SB#TopBuilder[TL]] = builderImpl[SB, TL, SB#TopBuilder[TL]](c)(sb)("TopLevel", "TopLevelDocument")

  def nestedBuilderImpl[SB <: SBOL2Base : c.WeakTypeTag, I: c.WeakTypeTag]
  (c: scala.reflect.macros.blackbox.Context)
  (sb: c.Expr[SB]): c.Expr[SB#NestedBuilder[I]] = builderImpl[SB, I, SB#NestedBuilder[I]](c)(sb)("Identified", "NestedDocument")

  private def builderImpl[SB <: SBOL2Base : c.WeakTypeTag, I: c.WeakTypeTag, BT : c.WeakTypeTag]
  (c: scala.reflect.macros.blackbox.Context)
  (sb: c.Expr[SB])(idType: String, docType: String): c.Expr[BT] =
  {
    import c.universe._

    val tlTpe = c.weakTypeOf[I]
    val sbTpe = c.weakTypeOf[SB]
    val btTpe = c.weakTypeOf[BT]
    val datatreeTpe = c.weakTypeOf[Datatree]
    val relationsTpe = c.weakTypeOf[Relations]

    val rdfType = tlTpe.typeSymbol.annotations.find(_.tree.tpe =:= c.typeOf[RDFType]).getOrElse {
      c.abort(c.enclosingPosition,
        s"Could not find an RDFType annotation for ${tlTpe}. All objects converted to Datatree must be annotated with a type.")
    }
    val rdfProps = Map(rdfType.tree.children.tail map (t => t.children.head.toString() -> t.children.last) :_*)

    val expr = c.Expr[BT] {
      q"""def builder[SB <: ${sbTpe}](sb: SB)
             (implicit propertyWomble: sb.PropertyWomble[sb.${tlTpe.typeSymbol.name.toTypeName}]) = new sb.${btTpe.typeSymbol}[$tlTpe]
           {
             override def buildTo[DT <: $datatreeTpe with $relationsTpe]
             (dt: DT)
             (implicit implicits: sb.Implicits[dt.URI, dt.Name, dt.PropertyValue])
             : PartialFunction[sb.${TypeName(idType)}, dt.${TypeName(docType)}] =
             {
               case tl : sb.${tlTpe.typeSymbol.name.toTypeName} =>
                       import implicits._
                       val ph = sb.propertyHelper(dt)(implicits)
                       dt.${TermName(docType)}(
                          identity = ph.mapIdentity(tl),
                          `type` = dt.One(dt.Name(
                               namespaceURI = dt.URI(${rdfProps("namespaceURI")}),
                               prefix = ${rdfProps("prefix")},
                               localPart = ${rdfProps("localPart")}
                          )),
                          properties = dt.ZeroMany(
                              propertyWomble.asProperties(dt, tl) :_*
                          )
                       )
             }
           }

           builder($sb)
           """
    }

    expr
  }

  def propertyWomble[SB <: SBOL2Base, I](sb: SB): sb.PropertyWomble[I] = macro propertyWombleImpl[SB, I]

  def propertyWombleImpl[SB <: SBOL2Base : c.WeakTypeTag, I : c.WeakTypeTag]
  (c: scala.reflect.macros.blackbox.Context)
  (sb: c.Expr[SB]): c.Expr[SB#PropertyWomble[I]] = {
    import c.universe._

    val sbTpe = c.weakTypeOf[SB]
    val datatreeTpe = c.weakTypeOf[Datatree]
    val relationsTpe = c.weakTypeOf[Relations]
    val sbol2BaseTpe = c.weakTypeOf[SBOL2Base]

    val tlTpe = c.weakTypeOf[I]
    val tlClass = tlTpe.typeSymbol.asClass

    val rdfType = tlTpe.typeSymbol.annotations.find(_.tree.tpe =:= c.typeOf[RDFType]).getOrElse {
      c.abort(c.enclosingPosition,
        s"Could not find an RDFType annotation for ${tlTpe}. All nested objects must be annotated with a type.")
    }
    val rdfProps = Map(rdfType.tree.children.tail map (t => t.children.head.toString() -> t.children.last) :_*)

    val tlParents = tlTpe.typeSymbol.info.asInstanceOf[ClassInfoType].parents

    val usefulParents = tlParents filter { case(p) =>
      def ps(s: Symbol): List[Symbol] = {
        (s.owner match {
          case NoSymbol => Nil
          case o => ps(o)
        }) :+ s
      }
      val owners = ps(p.typeSymbol.owner)
      val ownerNames = owners.map(_.name.toString)
      val scalaType = ownerNames startsWith List("<root>", "scala")
      val javaType = ownerNames startsWith List("<root>", "java")

      !scalaType && !javaType
    }

    val implicits = for((up, i) <- usefulParents.zipWithIndex) yield {
      val implicit_i = "implicit_" + i
      q"implicitly[sb.PropertyWomble[sb.${up.typeSymbol.name.toTypeName}]].asProperties(dt, i)"
    }

    val annotations = if(tlClass.isCaseClass) {
      val cstr = tlClass.primaryConstructor

      // fixme: we're flattening multi-param case-class constructors - things may explode
      val cstrParams = (for {
        pl <- cstr.asMethod.paramLists
        p <- pl
      } yield {
        val accessor = tlTpe.member(p.name)
        p.name.toString ->(p, accessor)
      }).toMap
      val localParams = cstrParams.filter { case (k, v) => v._2.overrides.isEmpty}
      localParams.mapValues { case (param, accessor) => (accessor, param.annotations)}
    } else {
      val localProperties = tlTpe.decls.collect {
        case ms if ms.isMethod && ms.isAbstract => ms.asMethod
      } filter (_.typeSignature.paramLists.isEmpty)

      (for(accessor <- localProperties) yield {
        accessor.name.toString -> (accessor, accessor.annotations)
      }).toMap
    }

    val notSkipped = annotations.filterNot { case (k, v) => v._2.contains((_: Annotation).tree.tpe =:= c.typeOf[RDFSkip]) }
    val withRdfProperty = notSkipped.mapValues { case(accessor, anns) => (accessor, anns.filter((_: Annotation).tree.tpe =:= c.typeOf[RDFProperty])) }
    for((pn, (accessor, rp)) <- withRdfProperty) {
      if(rp.isEmpty)
        c.abort(c.enclosingPosition, s"Unable to generate PropertyWomble[${tlTpe}] because property ${pn} doesn't have a RDFProperty annotation.")
    }
    val withName = withRdfProperty.mapValues { case (accessor, rp) =>
      val rdfPA = rp.head
      (accessor, Map(rdfPA.tree.children.tail map (t => t.children.head.toString() -> t.children.last) :_*))
    }


    val localProperties = withName map { case (pn, (accessor, ann)) =>
      val resType = accessor.typeSignature.resultType
      val resTypeName = resType.typeSymbol.name.toString
      val resTypeNameL = resTypeName.substring(0, 1).toLowerCase + resTypeName.substring(1)
      val resOpsName = TermName(resTypeNameL + "Ops")

      val arg = accessor.typeSignature.resultType.typeArgs.head
      val argTypeName = arg.typeSymbol.name.toTypeName
      val argType = if(arg.typeArgs.isEmpty) {
        sb.actualType.members.collectFirst { case m if m.name == argTypeName =>
          m
        } match {
          case None =>
            tq"$argTypeName"
          case Some(m) =>
            tq"sb.$argTypeName"
        }
      } else {
        val aArg = arg.typeArgs.head
        val aArgType = aArg.typeSymbol.name.toTypeName
        tq"sb.$argTypeName[sb.${aArgType}]"
      }

      q"""ph.asProperty(
          dt.Name(
            namespaceURI = dt.URI(${ann.getOrElse("namespaceURI", rdfProps("namespaceURI"))}),
            prefix = ${ann.getOrElse("prefix", rdfProps("prefix"))},
            localPart = ${ann("localPart")}
          ), sb.${resOpsName}[${argType}].seq(i.${TermName(pn)})
        )"""
    }

    val allProperties = (implicits ++ localProperties).reduce((a, b) => q"$a ++ $b")

    c.Expr[SB#PropertyWomble[I]] {
      q"""def womble[SB <: ${sbTpe} with ${sbol2BaseTpe}](sb: SB): sb.PropertyWomble[sb.${tlTpe.typeSymbol.name.toTypeName}] =
           new sb.PropertyWomble[sb.${tlTpe.typeSymbol.name.toTypeName}]
           {
           def asProperties[DT <: ${datatreeTpe} with ${relationsTpe}]
             (dt: DT, i: sb.${tlTpe.typeSymbol.name.toTypeName})
             (implicit implicits: sb.Implicits[dt.URI, dt.Name, dt.PropertyValue])
             : Seq[dt.NamedProperty] =
             {
               import implicits._
               import sb._
               import sb.{OneSyntax, OneOps}
               val ph = sb.propertyHelper(dt)
               import ph.identified2Value
               ${allProperties}
             }
           }

           womble(${sb})
        """
    }
  }

}
