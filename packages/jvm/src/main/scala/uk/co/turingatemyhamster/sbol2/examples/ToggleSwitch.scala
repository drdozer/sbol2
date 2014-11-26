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

    val lacI_Inverter_module = Module(
      identity = Uri("http://example.com/design/lacI_Inverter/module"),
      displayId = Some("LacI_Inverter"),
      roles = Seq(Uri("someTerms:inverter")),
      functionalInstantiations = Seq(
        FunctionalInstantiation(
          identity = Uri("http://example.com/design/lacI_Inverter/module/instantiation/LacI"),
          displayId = Some("LacI"),
          access = Public,
          instantiatedComponent = UriReference(Uri("http://example.com/design/lacI/component")),
          direction = Input
        ),
        FunctionalInstantiation(
          identity = Uri("http://example.com/design/lacI_Inverter/module/instantiation/TetR"),
          displayId = Some("TetR"),
          access = Public,
          instantiatedComponent = UriReference(Uri("http://example.com/design/TetR/component")),
          direction = Output
        )
      )
    )

    val tetR_Inverter_module = Module(
      identity = Uri("http://example.com/design/TetR_Inverter/module"),
      displayId = Some("TetR_Inverter"),
      roles = Seq(Uri("someTerms:inverter")),
      functionalInstantiations = Seq(
        FunctionalInstantiation(
          identity = Uri("http://example.com/design/TetR_Inverter/module/functionalInstantiation/LacI"),
          displayId = Some("LacI"),
          access = Public,
          instantiatedComponent = UriReference(Uri("http://example.com/design/lacI/component")),
          direction = Output
        ),
        FunctionalInstantiation(
          identity = Uri("http://example.com/design/TetR_Inverter/module/functionalInstantiation/TetR"),
          displayId = Some("TetR"),
          access = Public,
          instantiatedComponent = UriReference(Uri("http://example.com/design/TetR/component")),
          direction = Input
        )
      )
    )

    val toggle_switch_module = Module(
      identity = Uri("http://example.com/design/toggle_switch/module"),
      displayId = Some("Toggle_Switch"),
      roles = Seq(Uri("someTerms:switch")),
      functionalInstantiations = Seq(
        FunctionalInstantiation(
          identity = Uri("http://example.com/design/toggle_switch/module/functionalInstantiation/LacI"),
          displayId = Some("LacI"),
          access = Public,
          instantiatedComponent = UriReference(Uri("http://example.com/design/lacI/component")),
          direction = Output
        ),
        FunctionalInstantiation(
          identity = Uri("http://example.com/design/toggle_switch/module/functionalInstantiation/TetR"),
          displayId = Some("TetR"),
          access = Public,
          instantiatedComponent = UriReference(Uri("http://example.com/design/TetR/component")),
          direction = Output
        ),
        FunctionalInstantiation(
          identity = Uri("http://example.com/design/toggle_switch/module/functionalInstantiation/aTc"),
          displayId = Some("aTc"),
          access = Public,
          instantiatedComponent = UriReference(Uri("http://example.com/design/aTc/component")),
          direction = Input
        ),
        FunctionalInstantiation(
          identity = Uri("http://example.com/design/toggle_switch/module/functionalInstantiation/aTc_TetR_Complex"),
          access = Private,
          instantiatedComponent = UriReference(Uri("http://example.com/design/aTc_TetR_Complex/component")),
          direction = None
        ),
        FunctionalInstantiation(
          identity = Uri("http://example.com/design/toggle_switch/module/functionalInstantiation/IPTG"),
          displayId = Some("IPTG"),
          access = Public,
          instantiatedComponent = UriReference(Uri("http://example.com/design/IPTG/component")),
          direction = Input
        ),
        FunctionalInstantiation(
          identity = Uri("http://example.com/design/toggle_switch/module/functionalInstantiation/IPTG_LacI_Complex"),
          access = Private,
          instantiatedComponent = UriReference(Uri("http://example.com/design/IPTG_LacI_Complex/component")),
          direction = None
        )
      ),
      moduleInstantiations = Seq(
        ModuleInstantiation(
          identity = Uri("http://example.com/design/toggle_switch/module/moduleInstantiation/LacI_Inverter"),
          displayId = Some("LacI_Inverter"),
          instantiatedModule = UriReference(Uri("http://example.com/design/lacI_Inverter/module")),
          references = Seq(
            RefersTo(
              identity = Uri("http://example.com/design/toggle_switch/module/moduleInstantiation/LacI_Inverter/refersTo/LacI"),
              local = UriReference(Uri("http://example.com/design/toggle_switch/module/functionalInstantiation/LacI")),
              remote = UriReference(Uri("http://example.com/design/lacI_Inverter/module/instantiation/LacI")),
              refinement = VerifyIdentical
            ),
            RefersTo(
              identity = Uri("http://example.com/design/toggle_switch/module/moduleInstantiation/LacI_Inverter/refersTo/TetR"),
              local = UriReference(Uri("http://example.com/design/toggle_switch/module/functionalInstantiation/TetR")),
              remote = UriReference(Uri("http://example.com/design/lacI_Inverter/module/instantiation/TetR")),
              refinement = VerifyIdentical
            )
          )
        ),
        ModuleInstantiation(
          identity = Uri("http://example.com/design/toggle_switch/module/moduleInstantiation/TetR_Inverter"),
          displayId = Some("LacI_Inverter"),
          instantiatedModule = UriReference(Uri("http://example.com/design/TetR_Inverter/module")),
          references = Seq(
            RefersTo(
              identity = Uri("http://example.com/design/toggle_switch/module/moduleInstantiation/TetR_Inverter/refersTo/LacI"),
              local = UriReference(Uri("http://example.com/design/toggle_switch/module/functionalInstantiation/LacI")),
              remote = UriReference(Uri("http://example.com/design/TetR_Inverter/module/instantiation/LacI")),
              refinement = VerifyIdentical
            ),
            RefersTo(
              identity = Uri("http://example.com/design/toggle_switch/module/moduleInstantiation/TetR_Inverter/refersTo/TetR"),
              local = UriReference(Uri("http://example.com/design/toggle_switch/module/functionalInstantiation/TetR")),
              remote = UriReference(Uri("http://example.com/design/TetR_Inverter/module/instantiation/TetR")),
              refinement = VerifyIdentical
            )
          )
        )
      ),
      interactions = Seq(
        Interaction(
          identity = Uri("http://example.com/design/toggle_switch/module/interaction/IPTG_Binding"),
          displayId = Some("IPTG_Binding"),
          types = Seq(Uri("someTerms:non-covalent_binding")),
          participations = Seq(
            Participation(
              identity = Uri("http://example.com/design/toggle_switch/module/IPTG_Binding/interaction/IPTG_Binding/participation/IPTG"),
              roles = Seq(Uri("someTerms:reactant")),
              participant = UriReference(Uri("http://example.com/design/toggle_switch/module/functionalInstantiation/IPTG"))
            ),
            Participation(
              identity = Uri("http://example.com/design/toggle_switch/module/IPTG_Binding/interaction/IPTG_Binding/participation/LacI"),
              roles = Seq(Uri("someTerms:reactant")),
              participant = UriReference(Uri("http://example.com/design/toggle_switch/module/functionalInstantiation/LacI"))
            ),
            Participation(
              identity = Uri("http://example.com/design/toggle_switch/module/IPTG_Binding/interaction/IPTG_Binding/participation/IPTG_aTa_Complex"),
              roles = Seq(Uri("someTerms:reactant")),
              participant = UriReference(Uri("http://example.com/design/toggle_switch/module/functionalInstantiation/IPTG_LacI_Complex"))
            )
          )
        ),
        Interaction(
          identity = Uri("http://example.com/design/toggle_switch/module/interaction/aTc_Binding"),
          displayId = Some("aTc_Binding"),
          types = Seq(Uri("someTerms:non-covalent_binding")),
          participations = Seq(
            Participation(
              identity = Uri("http://example.com/design/toggle_switch/module/aTa_Binding/interaction/IPTG_Binding/participation/aTc"),
              roles = Seq(Uri("someTerms:reactant")),
              participant = UriReference(Uri("http://example.com/design/toggle_switch/module/functionalInstantiation/aTc"))
            ),
            Participation(
              identity = Uri("http://example.com/design/toggle_switch/module/aTa_Binding/interaction/IPTG_Binding/participation/TetR"),
              roles = Seq(Uri("someTerms:reactant")),
              participant = UriReference(Uri("http://example.com/design/toggle_switch/module/functionalInstantiation/TetR"))
            ),
            Participation(
              identity = Uri("http://example.com/design/toggle_switch/module/aTa_Binding/interaction/IPTG_Binding/participation/aTc_TetR_Complex"),
              roles = Seq(Uri("someTerms:reactant")),
              participant = UriReference(Uri("http://example.com/design/toggle_switch/module/functionalInstantiation/aTc_TetR_Complex"))
            )
          )
        )
      )
    )

    val sbolDocument = SBOLDocument(Seq(lacI_Inverter_module, tetR_Inverter_module, toggle_switch_module))

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