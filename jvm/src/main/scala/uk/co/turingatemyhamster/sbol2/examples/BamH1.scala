package uk.co.turingatemyhamster.sbol2
package examples

import java.io.StringWriter
import java.net.URI
import javax.xml.stream.XMLOutputFactory

import com.sun.xml.internal.txw2.output.IndentingXMLStreamWriter
import uk.co.turingatemyhamster.datatree.{RdfIo, Datatree}

/**
 *
 *
 * @author Matthew Pocock
 */
object BamH1 {
  def main(args: Array[String]): Unit = {
    import SBOL2._

    val bamH1_recognition_site = Structure(
      identity = URI.create("http://example.com/library/BamHI_recognition_site/structure"),
      displayId = Some("BamHI"),
      description = Some("BamHI recognition site"),
      encoding = URI.create("someTerms:iupac-dna-nonambiguous"),
      elements = "ggatcc"
    )

    val bamH1_recognition_site_component = Component(
      identity = URI.create("http://example.com/library/BamHI_recognition_site/component"),
      displayId = Some("BamHI"),
      description = Some("BamHI recognition site"),
      `type` = URI.create("someTerms:dna"),
      roles = Seq(URI.create("so:restriction_endonuclease_binding_site")),
      structuralAnnotations = Seq(
        StructuralAnnotation(
          identity = URI.create("http://example.com/library/BamHI_recognition_site/component/structuralAnnotation/top_strand_cut"),
          location = OrientedCut(
            identity = URI.create("http://example.com/library/BamHI_recognition_site/component/structuralAnnotation/top_strand_cut/location"),
            at = 1,
            orientation = Inline
          )
        ),
        StructuralAnnotation(
          identity = URI.create("http://example.com/library/BamHI_recognition_site/component/structuralAnnotation/bottom_strand_cut"),
          location = OrientedCut(
            identity = URI.create("http://example.com/library/BamHI_recognition_site/component/structuralAnnotation/bottom_strand_cut/location"),
            at = 5,
            orientation = ReverseComplement
          )
        )
      )
    )

    val sbolDocument = SBOLDocument(Seq(bamH1_recognition_site, bamH1_recognition_site_component))

    println("SBOL2 data model")
    println(sbolDocument)

    val dtree = DTIO.build(SBOL2, Datatree)(sbolDocument)

    println("Datatree data model")
    println(dtree)

    val writer = new StringWriter
    val xmlWriter = new IndentingXMLStreamWriter(
      XMLOutputFactory.newInstance.createXMLStreamWriter(
        writer))

    RdfIo(Datatree).write(xmlWriter, dtree)
    val rdf = writer.toString

    println(rdf)

  }
}
