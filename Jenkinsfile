node{
	stage("Git clone"){
		git credentialsId: 'GITHUB_CREDENTIALS', url: 'https://github.com/surjeetkm/multiproject.git'
	}
	stage("Maven clean build artifact"){	
		def mavenHome= tool name: "Maven", type: "maven"
		def command= "${mavenHome}/bin/mvn"
		sh "${command} clean install"
	}
	stage("Run Junit and Integration Test cases"){}
	stage('Caller-service image') {
   	 	dir ('caller-service') {
    			app=docker.build("microservices-2020/caller-service")
    			docker.withRegistry('https://eu.gcr.io', 'gcr:myregistry') {
 	 			app.push("${env.BUILD_NUMBER}")
 	 			app.push("latest")
        		}
  		}
  	}
  	
  	stage("callme image"){
       		dir ('callme-service') {
    			app=docker.build("microservices-2020/callme-service")
    			docker.withRegistry('https://eu.gcr.io', 'gcr:myregistry') {
 	 			app.push("${env.BUILD_NUMBER}")
 	 			app.push("latest")
        		}
        	}
     }  
}
