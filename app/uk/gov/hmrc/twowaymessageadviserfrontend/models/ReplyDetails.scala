/*
 * Copyright 2019 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.twowaymessageadviserfrontend.models

import play.api.libs.json._
import play.api.Logger

import scala.io.Source
import scala.language.implicitConversions
import scala.util.{Failure, Success, Try}
import scala.xml._
import scala.xml.parsing.XhtmlParser
import scala.xml.transform.RewriteRule


case class ReplyDetails(private val content: String) {

  private def stringToXhtml(string: String): Try[Seq[Node]] = {
    val xhtmlString = s"<html>$string</html>".replaceAll("[\n\r]","").replaceAll("&nbsp;"," ")
    try {
      val parser = new XhtmlParser(Source.fromString(xhtmlString))
      val doc = parser.initialize.document()
      Success(doc.docElem.child)
    } catch {
      case e: Throwable => Failure(e)
    }
  }

  private val addListClass = new RewriteRule {
    override def transform(n: Node): Seq[Node] = n match {
      case elem: Elem if elem.label == "ol"  =>
        elem.copy(attributes = new UnprefixedAttribute("class","list list-number",Null), child = elem.child)
      case elem: Elem if elem.label == "ul" =>
        elem.copy(attributes = new UnprefixedAttribute("class","list list-bullet",Null), child = elem.child)
      case `n` => n
    }
  }

  private def updateLists(nodes: Seq[Node]): Seq[Node] = {
    nodes.flatMap(node => addListClass(node))
  }

  def getContent: String = {
    stringToXhtml(content) match {
      case Success(nodes) => updateLists(nodes).mkString
      case Failure(e) =>
        Logger.error(s"Failed to parse content due to: ${e.getMessage}")
        ""
    }
  }

  def validate(content: String): Boolean = {
    stringToXhtml(content) match {
      case Success(_) => true
      case Failure(_) => false
    }
  }
}

object ReplyDetails {
  implicit val format: OFormat[ReplyDetails] = Json.format[ReplyDetails]
}
