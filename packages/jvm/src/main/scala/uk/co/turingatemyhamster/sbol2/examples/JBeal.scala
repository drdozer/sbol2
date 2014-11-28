package uk.co.turingatemyhamster.sbol2.examples

import java.io.StringWriter
import javax.xml.stream.XMLOutputFactory

import com.sun.xml.internal.txw2.output.IndentingXMLStreamWriter
import uk.co.turingatemyhamster.datatree.Datatrees
import uk.co.turingatemyhamster.sbol2.{DTIO, SBOL2}
import uk.co.turingatemyhamster.web.Web2Web

/**
 *
 *
 * @author Matthew Pocock
 */
object JBeal {
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
    val grn_logicalValue = grn.qName("logicalValue")
    val grn_property = grn.qName("property")
    val grn_Family = grn.qName("Family")
    val grn_name = grn.qName("name")
    val grn_product = grn.qName("product")

    val part_1 = ComponentDefinition(
      identity = Uri("http://example/part_1"),
      displayId = Some("part_1"),
      name = Some("Promoter 1"),
      `type` = dna,
      roles = Seq(promoter),
      annotations = Seq(
        grn_design := grn_DataType(
          grn_logicalType := "boolean",
          grn_logicalValue := "false"
        ),
        grn_regulation := grn_RegulatoryReaction(
          grn_regulatedBy := grn_ChemicalSpecies(
            grn_design := grn_DataType(
              grn_repression := false,
              grn_logicalType := "boolean"
            ),
            grn_property := grn_Family(
              grn_name := "Echo"
            )
          )
        )
      )
    )

    val part_3 = ComponentDefinition(
      identity = Uri("http://example/part_3"),
      displayId = Some("part_3"),
      name = Some("GFP CDS"),
      `type` = dna,
      roles = Seq(cds),
      annotations = Seq(
        grn_regulation := grn_RegulatoryReaction(
          grn_product := grn_ChemicalSpecies(
            grn_uid := "GFP",
            grn_design := grn_DataType(
              grn_logicalType := "boolean"
            ),
            grn_property := grn_Family(
              grn_name := "GFP"
            )
          )
        )
      )
    )

    val part_5 = ComponentDefinition(
      identity = Uri("http://example/part_5"),
      displayId = Some("part_5"),
      name = Some("Terminator 5"),
      `type` = dna,
      roles = Seq(terminator)
    )

    val part_7 = ComponentDefinition(
      identity = Uri("http://example/part_7"),
      displayId = Some("part_7"),
      name = Some("Promoter 7"),
      `type` = dna,
      roles = Seq(promoter)
    )

    val part_9 = ComponentDefinition(
      identity = Uri("http://example/part_9"),
      displayId = Some("part_9"),
      name = Some("Echo CDS"),
      `type` = dna,
      roles = Seq(cds)
    )

    val part_11 = ComponentDefinition(
      identity = Uri("http://example/part_11"),
      displayId = Some("part_11"),
      name = Some("Terminator 11"),
      `type` = dna,
      roles = Seq(cds)
    )

    val part_13 = ComponentDefinition(
      identity = Uri("http://example/part_13"),
      displayId = Some("part_13"),
      name = Some("Promoter 13"),
      `type` = dna,
      roles = Seq(promoter)
    )

    val part_15 = ComponentDefinition(
      identity = Uri("http://example/part_15"),
      displayId = Some("part_15"),
      name = Some("Echo CDS"),
      `type` = dna,
      roles = Seq(cds)
    )

    val part_17 = ComponentDefinition(
      identity = Uri("http://example/part_17"),
      displayId = Some("part_17"),
      name = Some("Terminator 17"),
      `type` = dna,
      roles = Seq(cds)
    )

    val part_19 = ComponentDefinition(
      identity = Uri("http://example/part_19"),
      displayId = Some("part_19"),
      name = Some("Promoter 19"),
      `type` = dna,
      roles = Seq(promoter)
    )

    val part_21 = ComponentDefinition(
      identity = Uri("http://example/part_21"),
      displayId = Some("part_21"),
      name = Some("LacI CDS"),
      `type` = dna,
      roles = Seq(cds)
    )

    val part_23 = ComponentDefinition(
      identity = Uri("http://example/part_23"),
      displayId = Some("part_23"),
      name = Some("TetR CDS"),
      `type` = dna,
      roles = Seq(cds)
    )

    val part_25 = ComponentDefinition(
      identity = Uri("http://example/part_25"),
      displayId = Some("part_25"),
      name = Some("Terminator 25"),
      `type` = dna,
      roles = Seq(cds)
    )

    val part_27 = ComponentDefinition(
      identity = Uri("http://example/part_27"),
      displayId = Some("part_27"),
      name = Some("Promoter 27"),
      `type` = dna,
      roles = Seq(promoter)
    )

    val part_29 = ComponentDefinition(
      identity = Uri("http://example/part_29"),
      displayId = Some("part_29"),
      name = Some("Charlie CDS"),
      `type` = dna,
      roles = Seq(cds)
    )

    val part_31 = ComponentDefinition(
      identity = Uri("http://example/part_31"),
      displayId = Some("part_31"),
      name = Some("Terminator 31"),
      `type` = dna,
      roles = Seq(cds)
    )


    val fu1 = {
      val baseUri = Uri("http://example/SHORT/partialFunctionalUnit_1")
      val an2 = SubComponent(
        baseUri = baseUri,
        displayId = "an_2",
        access = Public,
        instantiatedComponent = part_1
      )
      val an4 = SubComponent(
        baseUri = baseUri,
        displayId = "an_4",
        access = Public,
        instantiatedComponent = part_3
      )
      val an6 = SubComponent(
        baseUri = baseUri,
        displayId = "an_6",
        access = Public,
        instantiatedComponent = part_5
      )
      ComponentDefinition(
        identity = baseUri,
        `type` = dna,
        roles = Seq(region),
        displayId = Some("FunctionalUnit_1"),
        subComponents = Seq(an2, an4, an6),
        sequenceConstraints = Seq(
          SequenceConstraint(
            identity = Uri("http://example/SHORT/partialFunctionalUnit_1/sequenceConstraint/an2precedesan4"),
            subject = an2,
            restriction = precedes,
            `object` = an4
          ),
          SequenceConstraint(
            identity = Uri("http://example/SHORT/partialFunctionalUnit_1/sequenceConstraint/an4precedesan6"),
            subject = an4,
            restriction = precedes,
            `object` = an6
          )
        )
      )
    }

    val fu2 = {
      val baseUri = Uri("http://example/SHORT/part/FunctionalUnit_2")
      val an8 = SubComponent(
        baseUri = baseUri,
        displayId = "an_8",
        access = Public,
        instantiatedComponent = part_7
      )
      val an10 = SubComponent(
        baseUri = baseUri,
        displayId = "an_10",
        access = Public,
        instantiatedComponent = part_9
      )
      val an12 = SubComponent(
        baseUri = baseUri,
        displayId = "an_12",
        access = Public,
        instantiatedComponent = part_11
      )
      ComponentDefinition(
        identity = baseUri,
        displayId = Some("FunctionalUnit_2"),
        `type` = dna,
        roles = Seq(region),
        subComponents = Seq(an8, an10, an12),
        sequenceConstraints = Seq(
          SequenceConstraint(
            identity = Uri("http://example/SHORT/partialFunctionalUnit_2/sequenceConstraint/an8precedesan10"),
            subject = an8,
            restriction = precedes,
            `object` = an10
          ),
          SequenceConstraint(
            identity = Uri("http://example/SHORT/partialFunctionalUnit_2/sequenceConstraint/an10precedesan12"),
            subject = an10,
            restriction = precedes,
            `object` = an12
          )
        )
      )
    }

    val fu3 = {
      val baseUri = Uri("http://example/SHORT/part/FunctionalUnit_3")
      val an14 = SubComponent(
        baseUri = baseUri,
        displayId = "an_14",
        access = Public,
        instantiatedComponent = part_13
      )
      val an16 = SubComponent(
        baseUri = baseUri,
        displayId = "an_16",
        access = Public,
        instantiatedComponent = part_15
      )
      val an18 = SubComponent(
        baseUri = baseUri,
        displayId = "an_18",
        access = Public,
        instantiatedComponent = part_17
      )
      ComponentDefinition(
        identity = baseUri,
        displayId = Some("FunctionalUnit_3"),
        `type` = dna,
        roles = Seq(region),
        subComponents = Seq(an14, an16, an18),
        sequenceConstraints = Seq(
          SequenceConstraint(
            identity = Uri("http://example/SHORT/partialFunctionalUnit_3/sequenceConstraint/an14precedesan16"),
            subject = an14,
            restriction = precedes,
            `object` = an16
          ),
          SequenceConstraint(
            identity = Uri("http://example/SHORT/partialFunctionalUnit_3/sequenceConstraint/an16precedesan18"),
            subject = an16,
            restriction = precedes,
            `object` = an18
          )
        )
      )
    }

    val fu4 = {
      val baseUri = Uri("http://example/SHORT/part/FunctionalUnit_4")
      val an20 = SubComponent(
        baseUri = baseUri,
        displayId = "an_20",
        access = Public,
        instantiatedComponent = part_19
      )
      val an22 = SubComponent(
        baseUri = baseUri,
        displayId = "an_22",
        access = Public,
        instantiatedComponent = part_21
      )
      val an24 = SubComponent(
        baseUri = baseUri,
        displayId = "an_24",
        access = Public,
        instantiatedComponent = part_23
      )
      val an26 = SubComponent(
        baseUri = baseUri,
        displayId = "an_26",
        access = Public,
        instantiatedComponent = part_25
      )
      ComponentDefinition(
        identity = baseUri,
        displayId = Some("FunctionalUnit_4"),
        `type` = dna,
        roles = Seq(region),
        subComponents = Seq(an20, an22, an24, an26),
        sequenceConstraints = Seq(
          SequenceConstraint(
            identity = Uri("http://example/SHORT/partialFunctionalUnit_4/sequenceConstraint/an20precedesan22"),
            subject = an20,
            restriction = precedes,
            `object` = an22
          ),
          SequenceConstraint(
            identity = Uri("http://example/SHORT/partialFunctionalUnit_4/sequenceConstraint/an22precedesan24"),
            subject = an22,
            restriction = precedes,
            `object` = an24
          ),
          SequenceConstraint(
            identity = Uri("http://example/SHORT/partialFunctionalUnit_4/sequenceConstraint/an24precedesan26"),
            subject = an24,
            restriction = precedes,
            `object` = an26
          )
        )
      )
    }

    val fu5 = {
      val baseUri = Uri("http://example/SHORT/part/FunctionalUnit_5")
      val an28 = SubComponent(
        baseUri = baseUri,
        displayId = "an_28",
        access = Public,
        instantiatedComponent = part_27
      )
      val an30 = SubComponent(
        baseUri = baseUri,
        displayId = "an_30",
        access = Public,
        instantiatedComponent = part_29
      )
      val an32 = SubComponent(
        baseUri = baseUri,
        displayId = "an_32",
        access = Public,
        instantiatedComponent = part_31
      )
      ComponentDefinition(
        identity = baseUri,
        displayId = Some("FunctionalUnit_5"),
        `type` = dna,
        roles = Seq(region),
        subComponents = Seq(an28, an30, an32),
        sequenceConstraints = Seq(
          SequenceConstraint(
            identity = Uri("http://example/SHORT/partialFunctionalUnit_5/sequenceConstraint/an28precedesan30"),
            subject = an28,
            restriction = precedes,
            `object` = an30
          ),
          SequenceConstraint(
            identity = Uri("http://example/SHORT/partialFunctionalUnit_5/sequenceConstraint/an30precedesan32"),
            subject = an30,
            restriction = precedes,
            `object` = an32
          )
        )
      )
    }

    val geneticRegulatoryNetwork = Collection(
      identity = Uri("http://example/SHORT/col/GeneticRegulatoryNetwork"),
      displayId = Some("GeneticRegulatoryNetwork"),
      annotations = Seq(
        grn_regulation := grn_RegulatoryReaction(
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
        ),
        grn_regulation := grn_RegulatoryReaction(
          grn_substrate := grn_ChemicalSpecies(
            grn_uid := "TetR"
          ),
          grn_regulatedBy := grn_ChemicalSpecies(
            grn_repression := true,
            grn_design := grn_DataType(
              grn_logicalType := "boolean"
            )
          )
        )
      ),
      members = Seq(
        fu1,
        fu2,
        fu3,
        fu4,
        fu5
      )
    )

    val sbolDocument = SBOLDocument(Seq(
      part_1, part_3, part_5, part_7, part_9, part_11, part_13, part_15, part_17, part_19, part_21, part_23, part_25, part_27, part_29, part_31,
      fu1, fu2, fu3, fu4, fu5,
      geneticRegulatoryNetwork))


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
