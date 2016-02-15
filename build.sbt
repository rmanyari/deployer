name := "deployer"

maintainer in Debian := "Rodrigo Manyari"

version := "1.0"

scalaVersion := "2.11.7"

javacOptions ++= Seq("-source", "1.8", "-target", "1.8", "-Xlint")

resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies ++= {

    val sprayVersion = "1.3.3"
    val akkaVersion = "2.4.1"

    Seq(
        "com.typesafe.akka"             %% "akka-actor"         % akkaVersion,
        "com.typesafe.akka"             %% "akka-testkit"       % akkaVersion   % "test",
        "com.typesafe.akka"             %% "akka-cluster"       % akkaVersion,
        "com.typesafe.scala-logging"    %% "scala-logging"      % "3.1.0",
        "ch.qos.logback"                %  "logback-classic"    % "1.1.2",
        "io.spray"                      %% "spray-can"          % sprayVersion,
        "io.spray"                      %% "spray-routing"      % sprayVersion,
        "io.spray"                      %% "spray-testkit"      % sprayVersion  % "test",
        "org.specs2"                    %% "specs2-core"        % "3.7"       % "test"
    )

}

enablePlugins(JavaAppPackaging)