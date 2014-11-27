package uk.co.turingatemyhamster.sbol2
package examples

import java.io.StringWriter
import javax.xml.stream.XMLOutputFactory

import com.sun.xml.internal.txw2.output.IndentingXMLStreamWriter
import uk.co.turingatemyhamster.datatree.Datatrees
import uk.co.turingatemyhamster.web.Web2Web

/**
 *
 *
 * @author Matthew Pocock
 */
object Example01_Basics {
  def main(args: Array[String]): Unit = {
    import SBOL2._

    val myDNA = ComponentDefinition(
      identity = Uri("http://example.com/MyDnaComponent"),
      displayId = Some("MyDnaComponent"),
      name = Some("myDNA"),
      description = Some("This is a very simple example"),
      `type` = dna,
      roles = Seq(region))

    val sbolDocument = SBOLDocument(Seq(myDNA))

    val dtree = {
      val w2w_sd = Web2Web(SBOL2, Datatrees)
      import w2w_sd._
      DTIO.build(SBOL2, Datatrees)(sbolDocument)
    }

    val writer = new StringWriter
    val xmlWriter = new IndentingXMLStreamWriter(
      XMLOutputFactory.newInstance.createXMLStreamWriter(
        writer))

    Datatrees.RDF.write(xmlWriter, dtree)
    val rdf = writer.toString

    println(rdf)
  }
}
