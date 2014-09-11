package uk.co.turingatemyhamster.sbol2

import uk.co.turingatemyhamster.cake.{Relations, ScalaRelations}


/**
 * Default scala type bindings for SBOL2Base.
 *
 * @author Matthew Pocock
 */
trait ScalaBase extends SBOL2Base {
  importedPackages : Relations =>

  override type URI = java.net.URI
  override type QName = javax.xml.namespace.QName
  override type Timestamp = java.util.Date

  override implicit def identifierOps = new UriOps {
    override def idString(id: URI) = id.toString
  }
}
