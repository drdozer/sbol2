package uk.co.turingatemyhamster.sbol2

import uk.co.turingatemyhamster.web.{WebOpsImpl}
import uk.co.turingatemyhamster.relations.{RelationsOpsScalaImpl}

/**
 * A full implementation of SBOL2 with default bindings to scala datatypes.
 *
 * @author Matthew Pocock
 */
object SBOL2 extends SBOL2Base
with WebOpsImpl
with RelationsOpsScalaImpl
with SBOL2Collection
with SBOL2Component
with SBOL2Component_Sequence
with SBOL2Model
with SBOL2Module
