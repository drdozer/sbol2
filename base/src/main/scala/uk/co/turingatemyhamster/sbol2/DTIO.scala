package uk.co.turingatemyhamster.sbol2

import uk.co.turingatemyhamster.cake.{Relations, ScalaRelations}
import uk.co.turingatemyhamster.datatree.{DatatreeBuilder, Datatree}


object DTIO {

  def build[S2 <: SBOL2Base with Relations,
  DT <: Datatree with Relations](s2: S2, dt: DT)(sd: s2.SBOLDocument)(implicit i2uri: s2.URI => dt.URI, q2name: s2.QName => dt.Name): dt.DocumentRoot =
  {
    val sbol2 = dt.NamespaceBinding(prefix="sbol2", namespaceURI = dt.URI("http://sbol.org/v2#"))
    def bindings = Seq(sbol2)
    def lookupBinding(pfx: String) = bindings.filter(_.prefix == pfx).head

    implicit val s2impl = new s2.Implicits[dt.URI, dt.Name, dt.PropertyValue] {
      val uri2uri: s2.URI => dt.URI = i2uri
      val qname2name: s2.QName => dt.Name = q2name

      //def uriReference[T]: s2.UriReference[T] => dt.PropertyValue = ur => dt.UriLiteral(identifier2uri(ur.ref))

      override implicit def resolve: ((String, String)) => dt.Name = (prefix_localName: (String, String)) => {
        val nb = lookupBinding(prefix_localName._1)
        nb.withLocalName(prefix_localName._2)
      }

      val uri2Value: s2.URI => dt.PropertyValue = id => dt.UriLiteral(uri2uri(id))
      val string2Value: String => dt.PropertyValue = s => dt.StringLiteral(s)
      val integer2Value: Int => dt.PropertyValue = i => dt.IntegerLiteral(i)
      val double2Value: Double => dt.PropertyValue = d => dt.DoubleLiteral(d)
      val booleanValue: Boolean => dt.BooleanLiteral = b => dt.BooleanLiteral(b)

      // fixme: needs to know the details of ts
      val timestamp2Value: s2.Timestamp => dt.PropertyValue = ts => dt.TypedLiteral(ts.toString, "xsd:DateTime")

    }

    import s2._
    def bindingsFor(sd: s2.SBOLDocument) = bindings
    def documentsFor(sd: s2.SBOLDocument) = for {
      tl <- sd.contents.seq
      tlb <- s2.topLevels(dt)
      t <- tlb.lift.apply(tl)
    } yield t

    dt.DocumentRoot(
      bindings = dt.ZeroMany(bindingsFor(sd) :_*),
      documents = dt.ZeroMany(documentsFor(sd) :_*))
  }
}
