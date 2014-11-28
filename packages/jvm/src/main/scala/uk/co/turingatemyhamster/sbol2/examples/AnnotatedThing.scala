package uk.co.turingatemyhamster.sbol2
package examples

import java.io.{StringReader, StringWriter}
import javax.xml.stream.{XMLInputFactory, XMLOutputFactory}

import com.sun.xml.internal.txw2.output.IndentingXMLStreamWriter
import uk.co.turingatemyhamster.datatree.Datatrees
import uk.co.turingatemyhamster.web.Web2Web

/**
 *
 *
 * @author Matthew Pocock
 */
object AnnotatedThing {
  def main(args: Array[String]): Unit = {
    import SBOL2._

    val dc = NamespaceBinding(Namespace(Uri("http://purl.org/dc/elements/1.1/")), prefix = Prefix("dc"))
    val contributor = dc qName "contributor"

    val foaf = NamespaceBinding(Namespace(Uri("httpL//xmlns.com/foaf/0.1/")), prefix = Prefix("foaf"))
    val maker = foaf qName "maker"

    val myDNA = ComponentDefinition(
      identity = Uri("http://example.com/MyDnaComponent"),
      displayId = Some("MyDnaComponent"),
      name = Some("myDNA"),
      description = Some("This is a very simple example"),
      annotations = Seq(
        contributor := "Matthew Pocock",
        maker := Uri("mailto:myers@ece.utah.edu")
      ),
      `type` = Uri("chebi:DNA"),
      roles = Seq(Uri("so:region")))

    val sbolDocument = SBOLDocument(
      namespaceBindings = Seq(dc, foaf),
      contents = Seq(myDNA))

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

    val xmlReader = XMLInputFactory.newInstance.createXMLStreamReader(
      new StringReader(
        rdf))
    val readDtree = Datatrees.RDF.read(xmlReader)

    val readSbolDocument = {
      val w2w_ds = Web2Web(Datatrees, SBOL2)
      import w2w_ds._
      DTIO.build(SBOL2, Datatrees)(readDtree)
    }

    println(readSbolDocument)

  }
}
