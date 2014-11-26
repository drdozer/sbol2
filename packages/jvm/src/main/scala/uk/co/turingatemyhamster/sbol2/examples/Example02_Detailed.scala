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
object Example02_Detailed {
  def main(args: Array[String]): Unit = {
    import SBOL2._

    val dnaStructure = Structure(
      identity = Uri("http://sbols.org/seq#d23749adb3a7e0e2f09168cb7267a6113b238973"),
      elements =
        """
          |aaagaggagaaatactagatgaaaaacataaatgccgacgacacatacagaataattaataaaattaaagcttgtagaagcaataatga
          |tattaatcaatgcttatctgatatgactaaaatggtacattgtgaatattatttactcgcgatcatttatcctcattctatggttaaat
          |ctgatatttcaatcctagataattaccctaaaaaatggaggcaatattatgatgacgctaatttaataaaatatgatcctatagtagat
          |tattctaactccaatcattcaccaattaattggaatatatttgaaaacaatgctgtaaataaaaaatctccaaatgtaattaaagaagc
          |gaaaacatcaggtcttatcactgggtttagtttccctattcatacggctaacaatggcttcggaatgcttagttttgcacattcagaaa
          |aagacaactatatagatagtttatttttacatgcgtgtatgaacataccattaattgttccttctctagttgataattatcgaaaaata
          |aatatagcaaataataaatcaaacaacgatttaaccaaaagagaaaaagaatgtttagcgtgggcatgcgaaggaaaaagctcttggga
          |tatttcaaaaatattaggttgcagtgagcgtactgtcactttccatttaaccaatgcgcaaatgaaactcaatacaacaaaccgctgcc
          |aaagtatttctaaagcaattttaacaggagcaattgattgcccatactttaaaaattaataacactgatagtgctagtgtagatcacta
          |ctagagccaggcatcaaataaaacgaaaggctcagtcgaaagactgggcctttcgttttatctgttgtttgtcggtgaacgctctctac
          |tagagtcacactggctcaccttcgggtgggcctttctgcgtttata
        """.stripMargin,
      encoding = Uri("http://www.iupac.org/DNA")
    )

    val dnaComponent1 = Component(
      identity = Uri("http://partsregistry.org/Part:BBa_B0034"),
      displayId = Some("BBa_B0034"),
      name = Some("B0034"),
      `type` = Uri("chebi:DNA"),
      roles = Seq(Uri("so:0000139"))
    )

    val dnaComponent2 = Component(
      identity = Uri("http://partsregistry.org/Part:BBa_C0062"),
      displayId = Some("BBa_C0062"),
      name = Some("luxR"),
      `type` = Uri("chebi:DNA"),
      roles = Seq(Uri("so:CDS"))
    )

    val dnaComponent3 = Component(
      identity = Uri("http://partsregistry.org/Part:BBa_B0015"),
      displayId = Some("BBa_B0015"),
      name = Some("B0015"),
      `type` = Uri("chebi:DNA"),
      roles = Seq(Uri("so:terminator"))
    )

    val dnaComponent = Component(
      identity = Uri("http://partsregistry.org/Part:BBa_I0462"),
      displayId = Some("BBa_I0462"),
      name = Some("I0462"),
      description = Some("LuxR protein generator"),
      `type` = Uri("chebi:DNA"),
      roles = Seq(Uri("so:region")),
      structure = Some(UriReference(dnaStructure.identity)),
      structuralAnnotations = Seq(
        StructuralAnnotation(
          identity = Uri("http://sbols.org/anot#1234567"),
          location = OrientedRange(
            identity = Uri("http://sbols.org/anot#1234567/location"),
            start = 1,
            end = 12,
            orientation = Inline
          ),
          structuralInstantiation = Some(UriReference(dnaComponent1.identity))
        ),
        StructuralAnnotation(
          identity = Uri("http://sbols.org/anot#2345678"),
          location = OrientedRange(
            identity = Uri("http://sbols.org/anot#2345678/location"),
            start = 19,
            end = 774,
            orientation = Inline
          ),
          structuralInstantiation = Some(UriReference(dnaComponent2.identity))
        ),
        StructuralAnnotation(
          identity = Uri("http://sbols.org/anot#3456789"),
          location = OrientedRange(
            identity = Uri("http://sbols.org/anot#3456789/location"),
            start = 808,
            end = 936,
            orientation = Inline
          ),
          structuralInstantiation = Some(UriReference(dnaComponent3.identity))
        )
      )
    )

    val sbolDocument = SBOLDocument(Seq(dnaComponent, dnaStructure, dnaComponent1, dnaComponent2, dnaComponent3))

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
