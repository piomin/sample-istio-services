# Spring Boot With Istio Demo Project [![Twitter](https://img.shields.io/twitter/follow/piotr_minkowski.svg?style=social&logo=twitter&label=Follow%20Me)](https://twitter.com/piotr_minkowski)
                                     
In this project I'm demonstrating you the most interesting features of [Istio](https://istio.io) for building service mesh with Spring Boot on Kubernetes.

## Getting Started 
Currently you may find examples of two Spring Boot applications that may be deployed on Kubernetes. The application caller-service is calling callme-service using Istio rules.
1. The older article about it has been published in 2018, and based on Istio in version 0.8. The example has been moved to the branch [old_master](https://github.com/piomin/sample-istio-services/tree/old_master). Detailed description can be found here: [Service Mesh with Istio on Kubernetes in 5 steps](https://piotrminkowski.com/2018/04/13/service-mesh-with-istio-on-kubernetes-in-5-steps/)
2. The latest example is based on Istio 1.5 and Spring Boot 2.2. The example is available in the branch [master](https://github.com/piomin/sample-istio-services/tree/master). Currently I'm working on the article on my blog - it will be published soon.
