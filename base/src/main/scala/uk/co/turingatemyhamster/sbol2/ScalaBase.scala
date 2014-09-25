package uk.co.turingatemyhamster.sbol2

import uk.co.turingatemyhamster.web.WebOpsImpl
import uk.co.turingatemyhamster.relations.{Relations, RelationsOpsScalaImpl}


/**
 * Default scala type bindings for SBOL2Base.
 *
 * @author Matthew Pocock
 */
trait ScalaBase extends SBOL2Base with WebOpsImpl with RelationsOpsScalaImpl {

  override type Timestamp = java.util.Date
}
