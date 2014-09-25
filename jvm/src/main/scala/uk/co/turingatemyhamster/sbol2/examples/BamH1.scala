package uk.co.turingatemyhamster.sbol2
package examples

import java.io.{StringReader, StringWriter}
import javax.xml.stream.{XMLInputFactory, XMLOutputFactory}

import com.sun.xml.internal.txw2.output.IndentingXMLStreamWriter
import uk.co.turingatemyhamster.datatree.{RdfIo, Datatree, Datatrees}
import uk.co.turingatemyhamster.web.Web2Web

/**
 *
 *
 * @author Matthew Pocock
 */
object BamH1 {
  def main(args: Array[String]): Unit = {
    import SBOL2._

    val bamH1_recognition_site = Structure(
      identity = Uri("http://example.com/library/BamHI_recognition_site/structure"),
      displayId = Some("BamHI"),
      description = Some("BamHI recognition site"),
      encoding = Uri("someTerms:iupac-dna-nonambiguous"),
      elements = "ggatcc"
    )

    val bamH1_recognition_site_component = Component(
      identity = Uri("http://example.com/library/BamHI_recognition_site/component"),
      displayId = Some("BamHI"),
      description = Some("BamHI recognition site"),
      `type` = Uri("someTerms:dna"),
      roles = Seq(Uri("so:restriction_endonuclease_binding_site")),
      structuralAnnotations = Seq(
        StructuralAnnotation(
          identity = Uri("http://example.com/library/BamHI_recognition_site/component/structuralAnnotation/top_strand_cut"),
          location = OrientedCut(
            identity = Uri("http://example.com/library/BamHI_recognition_site/component/structuralAnnotation/top_strand_cut/location"),
            after = 1,
            orientation = Inline
          )
        ),
        StructuralAnnotation(
          identity = Uri("http://example.com/library/BamHI_recognition_site/component/structuralAnnotation/bottom_strand_cut"),
          location = OrientedCut(
            identity = Uri("http://example.com/library/BamHI_recognition_site/component/structuralAnnotation/bottom_strand_cut/location"),
            after = 5,
            orientation = ReverseComplement
          )
        )
      )
    )

    val sbolDocument = SBOLDocument(Seq(bamH1_recognition_site, bamH1_recognition_site_component))

    println("SBOL2 data model")
    println(sbolDocument)


    val dtree = {
      val w2w_sd = Web2Web(SBOL2, Datatrees)
      import w2w_sd._
      DTIO.build(SBOL2, Datatrees)(sbolDocument)
    }

    println("Datatree data model")
    println(dtree)

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

    println(readDtree)

    val readSbolDocument = {
      val w2w_ds = Web2Web(Datatrees, SBOL2)
      import w2w_ds._
      DTIO.build(SBOL2, Datatrees)(readDtree)
    }

    println(readSbolDocument)
  }
}
