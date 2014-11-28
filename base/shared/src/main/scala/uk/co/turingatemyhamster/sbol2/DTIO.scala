package uk.co.turingatemyhamster.sbol2

import uk.co.turingatemyhamster.web.{Web2Web, Web, WebOps}
import uk.co.turingatemyhamster.relations.{Relations, RelationsOps}
import uk.co.turingatemyhamster.datatree.{Datatree}


object DTIO {
  def build[S2 <: SBOL2Base with WebOps with RelationsOps,
  DT <: Datatree with WebOps with RelationsOps](s2: S2, dt: DT) = new Object {
    val sbol2 = dt.NamespaceBinding(prefix = dt.Prefix("sbol2"), namespace = dt.Namespace(dt.Uri("http://sbols.org/sbolv2/")))
    def bindings = Seq(sbol2)
    def lookupBinding(pfx: String) = bindings.filter { b =>
      import dt._
      b.prefix == dt.Prefix(pfx) }.head

    def apply(sd: s2.SBOLDocument)(
      implicit i2uri: s2.Uri => dt.Uri, q2name: s2.QName => dt.QName): dt.DocumentRoot =
    {

      implicit val s2impl: s2.ToImplicits[dt.Uri, dt.QName, dt.PropertyValue] =
        new s2.ToImplicits[dt.Uri, dt.QName, dt.PropertyValue]
        {
        val uri2uri: s2.Uri => dt.Uri = i2uri
        val qname2name: s2.QName => dt.QName = q2name

        //def uriReference[T]: s2.UriReference[T] => dt.PropertyValue = ur => dt.UriLiteral(identifier2uri(ur.ref))

        override implicit def resolve: ((String, String)) => dt.QName = (prefix_localName: (String, String)) => {
          import dt._
          val nb = lookupBinding(prefix_localName._1)
          nb.qName(prefix_localName._2)
        }

        val uri2Value: s2.Uri => dt.PropertyValue = id => dt.UriLiteral(uri2uri(id))
        val string2Value: String => dt.PropertyValue = s => dt.StringLiteral(s)
        val integer2Value: Int => dt.PropertyValue = i => dt.IntegerLiteral(i)
        val double2Value: Double => dt.PropertyValue = d => dt.DoubleLiteral(d)
        val booleanValue: Boolean => dt.BooleanLiteral = b => dt.BooleanLiteral(b)

        // fixme: needs to know the details of ts
        val timestamp2Value: s2.Timestamp => dt.PropertyValue = ts => dt.TypedLiteral(ts.toString, "xsd:DateTime")

      }

      import s2._
      def bindingsFor(sd: s2.SBOLDocument) = bindings ++ {
        val w2w = Web2Web(s2, dt)
        sd.namespaceBindings map w2w.namespaceBinding12
      }
      def documentsFor(sd: s2.SBOLDocument) = for {
        tl <- sd.contents.seq
      } yield {
        val ts = for {
          tlb <- s2.topBuilders.to[Stream]
          t <- tlb.buildTo(dt).lift.apply(tl)
        } yield t

        ts.headOption getOrElse {
          throw new IllegalArgumentException("Could not find nested document handler for: " + tl)
        }
      }

      dt.DocumentRoot(
        bindings = dt.ZeroMany(bindingsFor(sd) :_*),
        documents = dt.ZeroMany(documentsFor(sd) :_*))
    }

    def apply(doc: dt.DocumentRoot)(implicit i2uri: dt.Uri => s2.Uri, n2q: dt.QName => s2.QName): s2.SBOLDocument = {
      implicit val s2impl = new s2.FromImplicits[dt.Uri, dt.QName, dt.PropertyValue] {
        def uri2uri: dt.Uri => s2.Uri = i2uri
        def name2qname: dt.QName => s2.QName = n2q

        override implicit def resolve: ((String, String)) => dt.QName = (prefix_localName: (String, String)) => {
          import dt._
          val nb = lookupBinding(prefix_localName._1)
          nb.qName(prefix_localName._2)
        }

        implicit def value2uri: dt.PropertyValue => s2.Uri = { case dt.UriLiteral(uri) => uri }
        implicit def value2string: dt.PropertyValue => String = { case dt.StringLiteral(s) => s }
        implicit def value2integer: dt.PropertyValue => Int = { case dt.IntegerLiteral(i) => i }
        implicit def value2double: dt.PropertyValue => Double = { case dt.DoubleLiteral(d) => d }
        implicit def value2boolean: dt.PropertyValue => Boolean = { case dt.BooleanLiteral(b) => b }
        implicit def value2timestamp: dt.PropertyValue => s2.Timestamp = { case dt.TypedLiteral(ts, "xsd:DateTime") => s2.Timestamp(ts) }

      }

      import dt._
      def topLevelsFor(doc: dt.DocumentRoot) = for {
        tld <- doc.documents.seq
      } yield {
        val ts = for {
          tlb <- s2.topBuilders.to[Stream]
          t <- tlb.buildFrom(dt).lift.apply(tld)
        } yield t

        ts.headOption getOrElse {
          throw new IllegalArgumentException(s"Could not find top-level handler for ${tld.`type`}")
        }
      }

      s2.SBOLDocument(
        contents = s2.ZeroMany(topLevelsFor(doc) :_*)
      )
    }
  }
}
