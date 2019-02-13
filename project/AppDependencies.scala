import play.core.PlayVersion.current
import play.sbt.PlayImport._
import sbt.Keys.libraryDependencies
import sbt._

object AppDependencies {

  val compile = Seq(

    "uk.gov.hmrc"             %% "govuk-template"           % "5.3.0",
    "uk.gov.hmrc"             %% "play-ui"                  % "7.27.0-play-25",
    // "uk.gov.hmrc"             %% "bootstrap-play-25"        % "4.6.0",
    "uk.gov.hmrc"             %% "bootstrap-play-25"        % "4.8.0",
    "uk.gov.hmrc"             %% "play-reactivemongo"       % "6.2.0",
    "uk.gov.hmrc"             %% "play-language"            % "3.4.0"
  )

  val test = Seq(
    "org.scalatest"           %% "scalatest"                % "3.0.4"                 % "test",
    "org.jsoup"               %  "jsoup"                    % "1.10.2"                % "test",
    "com.typesafe.play"       %% "play-test"                % current                 % "test",
    "org.pegdown"             %  "pegdown"                  % "1.6.0"                 % "test, it",
    "uk.gov.hmrc"             %% "service-integration-test" % "0.2.0"                 % "test, it",
    "org.scalatestplus.play"  %% "scalatestplus-play"       % "2.0.1"                 % "test, it",
    "org.asynchttpclient" % "async-http-client" % "2.0.39"   % "test, it",
    "io.netty" % "netty-buffer" % "4.0.56.Final"   % "test, it",
    "io.netty" % "netty-codec-http" % "4.0.56.Final"   % "test, it",
    "io.netty" % "netty-transport-native-epoll" % "4.0.56.Final"   % "test, it"
  )

}
