import groovy.xml.XmlSlurper
import groovy.xml.slurpersupport.GPathResult
import groovy.xml.slurpersupport.NodeChild

plugins {
    `java-platform`
}

tasks {
    register<Exec>("eff.xml") {
        commandLine("bash", "-c", "mvn -B -f \"../../pom.xml\" help:effective-pom | grep -A9999999 '^<?xml' | grep -B9999999 '^</project>' > eff.xml")
    }
}

dependencies {
    operator fun GPathResult.div(child: String) = children().find { (it!! as NodeChild).name() == child } as GPathResult
    val deps = XmlSlurper().parse("platform/eff.xml") / "dependencyManagement" / "dependencies"
    constraints {
        deps.children().forEach {
            val node = it!! as NodeChild
            val g = node / "groupId"
            val a = node / "artifactId"
            val v = node / "version"
            api("$g:$a:$v")
//            println("$g:$a:$v")
        }
    }
}