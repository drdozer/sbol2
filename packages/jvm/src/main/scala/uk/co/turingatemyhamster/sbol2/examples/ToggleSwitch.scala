package uk.co.turingatemyhamster.sbol2
package examples

import java.io.{StringReader, StringWriter}
import javax.xml.stream.{XMLInputFactory, XMLOutputFactory}

import com.sun.xml.internal.txw2.output.IndentingXMLStreamWriter
import uk.co.turingatemyhamster.datatree._

import uk.co.turingatemyhamster.web.Web2Web

object ToggleSwitch {
  def main(args: Array[String]): Unit = {
    import SBOL2._

    val lacICds = ComponentDefinition(
      identity = Uri("http://example.com/design/lacI"),
      name = Some("lacI CDS"),
      `type` = dna,
      roles = Seq(cds)
    )

    val lacIProtein = ComponentDefinition(
      identity = Uri("http://example.com/design/LacI"),
      name = Some("LacI Protein"),
      `type` = protein,
      roles = Seq(ligandRegulatedTF)
    )

    val iptgMolecule = ComponentDefinition(
      identity = Uri("http://example.com/design/iptg"),
      name = Some("IPTG"),
      `type` = chebi("61448"),
      roles = Seq(metabolite)
    )

    val iptgLacIComplex = ComponentDefinition(
      identity = Uri("http://example.com/design/iptgLacIComplex"),
      name = Some("IPTG:LacI"),
      `type` = complex,
      roles = Seq(metabolite), // fixme
      subComponents = Seq(
        Component(
          identity = Uri("http://example.com/design/iptgLacIComplex/iptg"),
          access = Public,
          instantiatedComponent = iptgMolecule
        ),
        Component(
          identity = Uri("http://example.com/design/iptgLacIComplex/LacI"),
          access = Public,
          instantiatedComponent = lacIProtein
        )
      )
    )

    val tetRCds = ComponentDefinition(
      identity = Uri("http://example.com/design/tetR"),
      name = Some("tetR CDS"),
      `type` = dna,
      roles = Seq(cds)
    )

    val tetRProtein = ComponentDefinition(
      identity = Uri("http://example.com/design/TetR"),
      name = Some("TetR Protein"),
      `type` = dna,
      roles = Seq(cds)
    )

    val aTcMolecule = ComponentDefinition(
      identity = Uri("http://example.com/design/aTc"),
      name = Some("aTc"),
      `type` = chebi("28461"),
      roles = Seq(metabolite)
    )

    val aTcTetRComplex = ComponentDefinition(
      identity = Uri("http://example.com/design/aTcTetRComplex"),
      name = Some("aTc:TetR"),
      `type` = complex,
      roles = Seq(metabolite), // fixme
      subComponents = Seq(
        Component(
          identity = Uri("http://example.com/design/aTcTetRComplex/aTc"),
          access = Public,
          instantiatedComponent = aTcMolecule
        ),
        Component(
          identity = Uri("http://example.com/design/aTcTetRComplex/TetR"),
          access = Public,
          instantiatedComponent = tetRProtein
        )
      )
    )

    val lacI_Inverter_module = {
      val moduleUri = Uri("http://example.com/design/lacI_Inverter/module")
      val lacIC = FunctionalComponentOf(
        baseUri = moduleUri,
        displayId = "lacI",
        access = Public,
        instantiatedComponent = lacICds,
        direction = None
      )
      val lacIP = FunctionalComponentOf(
        baseUri = moduleUri,
        displayId = "LacI",
        access = Public,
        instantiatedComponent = lacIProtein,
        direction = Output
      )
      val tetRP = FunctionalComponentOf(
        baseUri = moduleUri,
        displayId = "TetR",
        access = Public,
        instantiatedComponent = aTcTetRComplex,
        direction = Input
      )
      val iptgM = FunctionalComponentOf(
        baseUri = moduleUri,
        displayId = "iptg",
        access = Public,
        instantiatedComponent = iptgMolecule,
        direction = Input
      )
      val iptgLacIC = FunctionalComponentOf(
        baseUri = moduleUri,
        displayId = "LacI:IPTG",
        access = Public,
        instantiatedComponent = iptgLacIComplex,
        direction = Output
      )
      ModuleDefinition(
        identity = moduleUri,
        displayId = Some("LacI_Inverter"),
        roles = Seq(Uri("someTerms:inverter")),
        functionalComponents = Seq(lacIC, lacIP, tetRP, iptgM, iptgLacIC),
        interactions = Seq(
          Interaction(
            identity = Uri("http://example.com/design/lacI_Inverter/module/LacIProduction"),
            types = Seq(geneticProduction),
            participations = Seq(
              Participation(
                identity = Uri("http://example.com/design/lacI_Inverter/module/LacIProduction/lacI"),
                roles = Seq(cds),
                participant = lacIC
              ),
              Participation(
                identity = Uri("http://example.com/design/lacI_Inverter/module/LaciIProduction/LacI"),
                roles = Seq(product),
                participant = lacIP
              ),
              Participation(
                identity = Uri("http://example.com/design/lacI_Inverter/module/LaciIProduction/TetR"),
                roles = Seq(inhibitor),
                participant = tetRP
              )
            )
          ),
          Interaction(
            identity = Uri("http://example.com/design/lacI_Inverter/module/iptgLacIBinding"),
            types = Seq(binding),
            participations = Seq(
              Participation(
                identity = Uri("http://example.com/design/lacI_Inverter/module/iptgLacIBinding/iptg"),
                roles = Seq(interactor),
                participant = iptgM
              ),
              Participation(
                identity = Uri("http://example.com/design/lacI_Inverter/module/iptgLacIBinding/LacI"),
                roles = Seq(interactor),
                participant = lacIP
              ),
              Participation(
                identity = Uri("http://example.com/design/lacI_Inverter/module/iptgLacIBinding/iptgLacI"),
                roles = Seq(product),
                participant = iptgLacIC
              )
            )
          )
        )
      )
    }

    val tetR_Inverter_module = {
      val moduleUri = Uri("http://example.com/design/TetR_Inverter/module")
      val tetRC = FunctionalComponentOf(
        baseUri = moduleUri,
        displayId = "tetR",
        access = Public,
        instantiatedComponent = tetRCds,
        direction = None
      )
      val tetRP = FunctionalComponentOf(
        baseUri = moduleUri,
        displayId = "TetR",
        access = Public,
        instantiatedComponent = tetRProtein,
        direction = Output
      )
      val lacIP = FunctionalComponentOf(
        baseUri = moduleUri,
        displayId = "LacI",
        access = Public,
        instantiatedComponent = lacIProtein,
        direction = Input
      )
      val aTcM = FunctionalComponentOf(
        baseUri = moduleUri,
        displayId = "aTc",
        access = Public,
        instantiatedComponent = aTcMolecule,
        direction = Input
      )
      val aTcTetRC = FunctionalComponentOf(
        baseUri = moduleUri,
        displayId = "TetR:aTc",
        access = Public,
        instantiatedComponent = aTcTetRComplex,
        direction = Output
      )
      ModuleDefinition(
        identity = moduleUri,
        displayId = Some("TetR_Inverter"),
        roles = Seq(Uri("someTerms:inverter")),
        functionalComponents = Seq(tetRC, tetRP, lacIP, aTcM, aTcTetRC),
        interactions = Seq(
          Interaction(
            identity = Uri("http://example.com/design/TetR_Inverter/module/interaction/TetRProduction"),
            types = Seq(geneticProduction),
            participations = Seq(
              Participation(
                identity = Uri("http://example.com/design/TetR_Inverter/module/interaction/TetRProduction/tetR"),
                roles = Seq(cds),
                participant = tetRC
              ),
              Participation(
                identity = Uri("http://example.com/design/TetR_Inverter/module/interaction/TetRProduction/TetR"),
                roles = Seq(product),
                participant = tetRP
              ),
              Participation(
                identity = Uri("http://example.com/design/TetR_Inverter/module/interaction/TetRProduction/LacI"),
                roles = Seq(inhibitor),
                participant = lacIP
              )
            )
          ),
          Interaction(
            identity = Uri("http://example.com/design/TetR_Inverter/module/interaction/aTcTetRBinding"),
            types = Seq(binding),
            participations = Seq(
              Participation(
                identity = Uri("http://example.com/design/TetR_Inverter/module/interaction/aTcTetRBinding/aTc"),
                roles = Seq(interactor),
                participant = aTcM
              ),
              Participation(
                identity = Uri("http://example.com/design/TetR_Inverter/module/interaction/aTcTetRBinding/TetR"),
                roles = Seq(interactor),
                participant = tetRP
              ),
              Participation(
                identity = Uri("http://example.com/design/TetR_Inverter/module/interaction/aTcTetRBinding/aTcTetR"),
                roles = Seq(product),
                participant = aTcTetRC
              )
            )
          )
        )
      )
    }

    val toggle_switch_module = {
      val c_lacI = FunctionalComponent(
        identity = Uri("http://example.com/design/toggle_switch/module/functionalInstantiation/LacI"),
        displayId = Some("LacI"),
        access = Public,
        instantiatedComponent = lacIProtein,
        direction = Output
      )
      val c_tetR = FunctionalComponent(
        identity = Uri("http://example.com/design/toggle_switch/module/functionalInstantiation/TetR"),
        displayId = Some("TetR"),
        access = Public,
        instantiatedComponent = tetRProtein,
        direction = Output
      )
      ModuleDefinition(
        identity = Uri("http://example.com/design/toggle_switch/module"),
        displayId = Some("Toggle_Switch"),
        roles = Seq(Uri("someTerms:switch")),
        functionalComponents = Seq(c_lacI, c_tetR),
        subModule = Seq(
          Module(
            identity = Uri("http://example.com/design/toggle_switch/module/moduleInstantiation/LacI_Inverter"),
            displayId = Some("LacI_Inverter"),
            instantiatedModule = lacI_Inverter_module,
            references = Seq(
              MapsTo(
                identity = Uri("http://example.com/design/toggle_switch/module/moduleInstantiation/LacI_Inverter/refersTo/LacI"),
                local = c_lacI,
                remote = lacI_Inverter_module.functionalComponents.find(_.displayId == Some("LacI")).get,
                refinement = Merge
              ),
              MapsTo(
                identity = Uri("http://example.com/design/toggle_switch/module/moduleInstantiation/LacI_Inverter/refersTo/TetR"),
                local = c_tetR,
                remote = lacI_Inverter_module.functionalComponents.find(_.displayId == Some("TetR")).get,
                refinement = Merge
              )
            )
          ),
          Module(
            identity = Uri("http://example.com/design/toggle_switch/module/moduleInstantiation/TetR_Inverter"),
            displayId = Some("TetR_Inverter"),
            instantiatedModule = tetR_Inverter_module,
            references = Seq(
              MapsTo(
                identity = Uri("http://example.com/design/toggle_switch/module/moduleInstantiation/TetR_Inverter/refersTo/LacI"),
                local = c_lacI,
                remote = tetR_Inverter_module.functionalComponents.find(_.displayId == Some("LacI")).get,
                refinement = Merge
              ),
              MapsTo(
                identity = Uri("http://example.com/design/toggle_switch/module/moduleInstantiation/TetR_Inverter/refersTo/TetR"),
                local = c_tetR,
                remote = tetR_Inverter_module.functionalComponents.find(_.displayId == Some("TetR")).get,
                refinement = Merge
              )
            )
          )
        )
      )
    }

    val sbolDocument = SBOLDocument(
      contents = Seq(
        lacICds, lacIProtein, iptgMolecule, iptgLacIComplex,
        tetRCds, tetRProtein, aTcMolecule, aTcTetRComplex,
        lacI_Inverter_module, tetR_Inverter_module, toggle_switch_module))

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
