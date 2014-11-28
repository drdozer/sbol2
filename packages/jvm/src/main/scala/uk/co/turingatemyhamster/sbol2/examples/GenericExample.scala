package uk.co.turingatemyhamster.sbol2.examples

import java.io.{StringReader, StringWriter}
import javax.xml.stream.{XMLInputFactory, XMLOutputFactory}

import com.sun.xml.internal.txw2.output.IndentingXMLStreamWriter
import uk.co.turingatemyhamster.datatree.Datatrees
import uk.co.turingatemyhamster.sbol2.{DTIO, SBOL2}
import uk.co.turingatemyhamster.sbol2.SBOL2._
import uk.co.turingatemyhamster.web.Web2Web

/**
 *
 *
 * @author Matthew Pocock
 */
object GenericExample {
  def main(args: Array[String]): Unit = {
    import SBOL2._

    val grn = NamespaceBinding(Namespace(Uri("urn:bbn.com:tasbe:grn")), Prefix("grn"))
    val grn_regulation = grn.qName("regulation")
    val grn_RegulatoryReaction = grn.qName("RegulatoryReaction")
    val grn_substrate = grn.qName("substrate")
    val grn_ChemicalSpecies = grn.qName("ChemicalSpecies")
    val grn_uid = grn.qName("uid")
    val grn_regulatedBy = grn.qName("regulatedBy")
    val grn_repression = grn.qName("repression")
    val grn_design = grn.qName("design")
    val grn_DataType = grn.qName("DataType")
    val grn_logicalType = grn.qName("logicalType")

    val generic = GenericTopLevel(
      identity = Uri("http://example.com/genericTopLevel"),
      `type` = grn_RegulatoryReaction,
      annotations = Seq(
        grn_substrate := grn_ChemicalSpecies(
          grn_uid := "LacI"
        ),
        grn_regulatedBy := grn_ChemicalSpecies(
          grn_repression := true,
          grn_uid := "IPTG",
          grn_design := grn_DataType(
            grn_logicalType := "boolean"
          )
        )
      )
    )

    val col = Collection(
      identity = Uri("http://example.com/aCollection"),
      annotations = Seq(
        grn_regulation := Uri("http://example.com/genericTopLevel")
      )
    )

    val sbolDocument = SBOLDocument(
      namespaceBindings = Seq(grn),
      contents = Seq(generic, col))

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
