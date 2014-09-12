package uk.co.turingatemyhamster.sbol2
package examples

import java.io.StringWriter
import javax.xml.stream.XMLOutputFactory

import com.sun.xml.internal.txw2.output.IndentingXMLStreamWriter
import uk.co.turingatemyhamster.datatree._

import java.net.URI

object ToggleSwitch {
  def main(args: Array[String]): Unit = {
    import SBOL2._

    val lacI_Inverter_module = Module(
      identity = URI.create("http://example.com/design/lacI_Inverter/module"),
      displayId = Some("LacI_Inverter"),
      roles = Seq(URI.create("someTerms:inverter")),
      functionalInstantiations = Seq(
        FunctionalInstantiation(
          identity = URI.create("http://example.com/design/lacI_Inverter/module/instantiation/LacI"),
          displayId = Some("LacI"),
          access = Public,
          instantiatedComponent = UriReference(URI.create("http://example.com/design/lacI/component")),
          direction = Input
        ),
        FunctionalInstantiation(
          identity = URI.create("http://example.com/design/lacI_Inverter/module/instantiation/TetR"),
          displayId = Some("TetR"),
          access = Public,
          instantiatedComponent = UriReference(URI.create("http://example.com/design/TetR/component")),
          direction = Output
        )
      )
    )

    val tetR_Inverter_module = Module(
      identity = URI.create("http://example.com/design/TetR_Inverter/module"),
      displayId = Some("TetR_Inverter"),
      roles = Seq(URI.create("someTerms:inverter")),
      functionalInstantiations = Seq(
        FunctionalInstantiation(
          identity = URI.create("http://example.com/design/TetR_Inverter/module/functionalInstantiation/LacI"),
          displayId = Some("LacI"),
          access = Public,
          instantiatedComponent = UriReference(URI.create("http://example.com/design/lacI/component")),
          direction = Output
        ),
        FunctionalInstantiation(
          identity = URI.create("http://example.com/design/TetR_Inverter/module/functionalInstantiation/TetR"),
          displayId = Some("TetR"),
          access = Public,
          instantiatedComponent = UriReference(URI.create("http://example.com/design/TetR/component")),
          direction = Input
        )
      )
    )

    val toggle_switch_module = Module(
      identity = URI.create("http://example.com/design/toggle_switch/module"),
      displayId = Some("Toggle_Switch"),
      roles = Seq(URI.create("someTerms:switch")),
      functionalInstantiations = Seq(
        FunctionalInstantiation(
          identity = URI.create("http://example.com/design/toggle_switch/module/functionalInstantiation/LacI"),
          displayId = Some("LacI"),
          access = Public,
          instantiatedComponent = UriReference(URI.create("http://example.com/design/lacI/component")),
          direction = Output
        ),
        FunctionalInstantiation(
          identity = URI.create("http://example.com/design/toggle_switch/module/functionalInstantiation/TetR"),
          displayId = Some("TetR"),
          access = Public,
          instantiatedComponent = UriReference(URI.create("http://example.com/design/TetR/component")),
          direction = Output
        ),
        FunctionalInstantiation(
          identity = URI.create("http://example.com/design/toggle_switch/module/functionalInstantiation/aTc"),
          displayId = Some("aTc"),
          access = Public,
          instantiatedComponent = UriReference(URI.create("http://example.com/design/aTc/component")),
          direction = Input
        ),
        FunctionalInstantiation(
          identity = URI.create("http://example.com/design/toggle_switch/module/functionalInstantiation/aTc_TetR_Complex"),
          access = Private,
          instantiatedComponent = UriReference(URI.create("http://example.com/design/aTc_TetR_Complex/component")),
          direction = None
        ),
        FunctionalInstantiation(
          identity = URI.create("http://example.com/design/toggle_switch/module/functionalInstantiation/IPTG"),
          displayId = Some("IPTG"),
          access = Public,
          instantiatedComponent = UriReference(URI.create("http://example.com/design/IPTG/component")),
          direction = Input
        ),
        FunctionalInstantiation(
          identity = URI.create("http://example.com/design/toggle_switch/module/functionalInstantiation/IPTG_LacI_Complex"),
          access = Private,
          instantiatedComponent = UriReference(URI.create("http://example.com/design/IPTG_LacI_Complex/component")),
          direction = None
        )
      ),
      moduleInstantiations = Seq(
        ModuleInstantiation(
          identity = URI.create("http://example.com/design/toggle_switch/module/moduleInstantiation/LacI_Inverter"),
          displayId = Some("LacI_Inverter"),
          instantiatedModule = UriReference(URI.create("http://example.com/design/lacI_Inverter/module")),
          references = Seq(
            RefersTo(
              identity = URI.create("http://example.com/design/toggle_switch/module/moduleInstantiation/LacI_Inverter/refersTo/LacI"),
              local = UriReference(URI.create("http://example.com/design/toggle_switch/module/functionalInstantiation/LacI")),
              remote = UriReference(URI.create("http://example.com/design/lacI_Inverter/module/instantiation/LacI")),
              refinement = VerifyIdentical
            ),
            RefersTo(
              identity = URI.create("http://example.com/design/toggle_switch/module/moduleInstantiation/LacI_Inverter/refersTo/TetR"),
              local = UriReference(URI.create("http://example.com/design/toggle_switch/module/functionalInstantiation/TetR")),
              remote = UriReference(URI.create("http://example.com/design/lacI_Inverter/module/instantiation/TetR")),
              refinement = VerifyIdentical
            )
          )
        ),
        ModuleInstantiation(
          identity = URI.create("http://example.com/design/toggle_switch/module/moduleInstantiation/TetR_Inverter"),
          displayId = Some("LacI_Inverter"),
          instantiatedModule = UriReference(URI.create("http://example.com/design/TetR_Inverter/module")),
          references = Seq(
            RefersTo(
              identity = URI.create("http://example.com/design/toggle_switch/module/moduleInstantiation/TetR_Inverter/refersTo/LacI"),
              local = UriReference(URI.create("http://example.com/design/toggle_switch/module/functionalInstantiation/LacI")),
              remote = UriReference(URI.create("http://example.com/design/TetR_Inverter/module/instantiation/LacI")),
              refinement = VerifyIdentical
            ),
            RefersTo(
              identity = URI.create("http://example.com/design/toggle_switch/module/moduleInstantiation/TetR_Inverter/refersTo/TetR"),
              local = UriReference(URI.create("http://example.com/design/toggle_switch/module/functionalInstantiation/TetR")),
              remote = UriReference(URI.create("http://example.com/design/TetR_Inverter/module/instantiation/TetR")),
              refinement = VerifyIdentical
            )
          )
        )
      ),
      interactions = Seq(
        Interaction(
          identity = URI.create("http://example.com/design/toggle_switch/module/interaction/IPTG_Binding"),
          displayId = Some("IPTG_Binding"),
          types = Seq(URI.create("someTerms:non-covalent_binding")),
          participations = Seq(
            Participation(
              identity = URI.create("http://example.com/design/toggle_switch/module/IPTG_Binding/interaction/IPTG_Binding/participation/IPTG"),
              roles = Seq(URI.create("someTerms:reactant")),
              participant = UriReference(URI.create("http://example.com/design/toggle_switch/module/functionalInstantiation/IPTG"))
            ),
            Participation(
              identity = URI.create("http://example.com/design/toggle_switch/module/IPTG_Binding/interaction/IPTG_Binding/participation/LacI"),
              roles = Seq(URI.create("someTerms:reactant")),
              participant = UriReference(URI.create("http://example.com/design/toggle_switch/module/functionalInstantiation/LacI"))
            ),
            Participation(
              identity = URI.create("http://example.com/design/toggle_switch/module/IPTG_Binding/interaction/IPTG_Binding/participation/IPTG_aTa_Complex"),
              roles = Seq(URI.create("someTerms:reactant")),
              participant = UriReference(URI.create("http://example.com/design/toggle_switch/module/functionalInstantiation/IPTG_LacI_Complex"))
            )
          )
        ),
        Interaction(
          identity = URI.create("http://example.com/design/toggle_switch/module/interaction/aTc_Binding"),
          displayId = Some("aTc_Binding"),
          types = Seq(URI.create("someTerms:non-covalent_binding")),
          participations = Seq(
            Participation(
              identity = URI.create("http://example.com/design/toggle_switch/module/aTa_Binding/interaction/IPTG_Binding/participation/aTc"),
              roles = Seq(URI.create("someTerms:reactant")),
              participant = UriReference(URI.create("http://example.com/design/toggle_switch/module/functionalInstantiation/aTc"))
            ),
            Participation(
              identity = URI.create("http://example.com/design/toggle_switch/module/aTa_Binding/interaction/IPTG_Binding/participation/TetR"),
              roles = Seq(URI.create("someTerms:reactant")),
              participant = UriReference(URI.create("http://example.com/design/toggle_switch/module/functionalInstantiation/TetR"))
            ),
            Participation(
              identity = URI.create("http://example.com/design/toggle_switch/module/aTa_Binding/interaction/IPTG_Binding/participation/aTc_TetR_Complex"),
              roles = Seq(URI.create("someTerms:reactant")),
              participant = UriReference(URI.create("http://example.com/design/toggle_switch/module/functionalInstantiation/aTc_TetR_Complex"))
            )
          )
        )
      )
    )

    val sbolDocument = SBOLDocument(Seq(lacI_Inverter_module, tetR_Inverter_module, toggle_switch_module))

    println("SBOL2 data model")
    println(lacI_Inverter_module)

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
