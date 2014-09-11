package uk.co.turingatemyhamster.sbol2

import uk.co.turingatemyhamster.cake.ScalaRelations

/**
 * A full implementation of SBOL2 with default bindings to scala datatypes.
 *
 * @author Matthew Pocock
 */
object SBOL2 extends SBOL2Base
with ScalaRelations
with ScalaBase
with SBOL2Collection
with SBOL2Component
with SBOL2Component_Sequence
with SBOL2Model
with SBOL2Module
