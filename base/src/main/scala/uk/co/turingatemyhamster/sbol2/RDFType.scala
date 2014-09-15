package uk.co.turingatemyhamster.sbol2

import scala.annotation.{ClassfileAnnotation, StaticAnnotation}

/**
 *
 *
 * @author Matthew Pocock
 */
class RDFType(namespaceUri: String, prefix: String, localPart: String) extends ClassfileAnnotation

class RDFProperty(namespaceUri: Option[String] = None, prefix: Option[String] = None, localPart: String) extends ClassfileAnnotation

class RDFSkip extends ClassfileAnnotation