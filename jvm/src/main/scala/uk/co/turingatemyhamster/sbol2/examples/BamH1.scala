package uk.co.turingatemyhamster.sbol2
package examples

import java.io.{StringReader, StringWriter}
import javax.xml.stream.{XMLInputFactory, XMLOutputFactory}

import com.sun.xml.internal.txw2.output.IndentingXMLStreamWriter
import uk.co.turingatemyhamster.datatree.{RdfIo, Datatree, Datatrees}

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


    implicit def prefixs(pf: SBOL2.Prefix): Datatrees.Prefix = pf match { case SBOL2.Prefix(pf) => Datatrees.Prefix(pf) }
    implicit def sxiferp(pf: Datatrees.Prefix): SBOL2.Prefix = pf match { case Datatrees.Prefix(pf) => SBOL2.Prefix(pf) }
    implicit def localNames(ln: SBOL2.LocalName): Datatrees.LocalName = ln match { case SBOL2.LocalName(ln) => Datatrees.LocalName(ln) }
    implicit def semanLocol(ln: Datatrees.LocalName): SBOL2.LocalName = ln match { case Datatrees.LocalName(ln) => SBOL2.LocalName(ln) }
    implicit def namespaces(ns: SBOL2.Namespace): Datatrees.Namespace = ns match { case SBOL2.Namespace(ns) => Datatrees.Namespace(ns) }
    implicit def secapseman(ns: Datatrees.Namespace): SBOL2.Namespace = ns match { case Datatrees.Namespace(ns) => SBOL2.Namespace(ns) }
    implicit def uris(uri: SBOL2.Uri): Datatrees.Uri = Datatrees.Uri(uri.raw)
    implicit def siru(uri: Datatrees.Uri): SBOL2.Uri = SBOL2.Uri(uri.raw)
    implicit def qnames(qname: SBOL2.QName): Datatrees.QName =
      Datatrees.QName(
        namespace = qname.namespace,
        prefix = qname.prefix,
        localName = qname.localName)
    implicit def semanq(qname: Datatrees.QName): SBOL2.QName =
      SBOL2.QName(
        namespace = qname.namespace,
        prefix = qname.prefix,
        localName = qname.localName)

    val dtree = DTIO.build(SBOL2, Datatrees)(sbolDocument)

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

    val readSbolDocument = DTIO.build(SBOL2, Datatrees)(readDtree)

    println(readSbolDocument)
  }
}
