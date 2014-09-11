package uk.co.turingatemyhamster.sbol2

import scala.annotation.{ClassfileAnnotation, StaticAnnotation}

/**
 *
 *
 * @author Matthew Pocock
 */
class RDFType(namespaceURI: String, prefix: String, localPart: String) extends ClassfileAnnotation

class RDFProperty(namespaceURI: Option[String] = None, prefix: Option[String] = None, localPart: String) extends ClassfileAnnotation

class RDFSkip extends ClassfileAnnotation